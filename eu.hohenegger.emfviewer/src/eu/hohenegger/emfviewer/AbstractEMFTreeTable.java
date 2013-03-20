/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditValueProperty;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.CellEditorProperties;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.hohenegger.emfviewer.contentprovider.AbstractEMFContentProvider;
import eu.hohenegger.emfviewer.contentprovider.TreeContentChangeListener;
import eu.hohenegger.emfviewer.dnd.IDragSupport;
import eu.hohenegger.emfviewer.dnd.IDropSupport;
import eu.hohenegger.emfviewer.labelprovider.AbstractHighlightableTreeLabelProvider;
import eu.hohenegger.emfviewer.labelprovider.EMFHighlightableTreeLabelProvider;
import eu.hohenegger.emfviewer.labelprovider.HighlightableDecoratedCellLabelProvider;
import eu.hohenegger.emfviewer.labelprovider.TreeLabelDecorator;
import eu.hohenegger.emfviewer.tree.EMFTreePatternFilter;

public abstract class AbstractEMFTreeTable extends FilteredTree implements IEMFTreeViewer {
	
	private ObservablesManager modelObservablesManager = new ObservablesManager();
	private EditingDomain editingDomain;
	private TreeColumnLayout treeColumnLayout;

	private final EMFDataBindingContext dataBindingContext = new EMFDataBindingContext();
	private ObservableListTreeContentProvider contentProvider;
	private EObject containmentRoot;
	
	private EMFDataBindingContext ctx = new EMFDataBindingContext();

	private TreeContentChangeListener treeContentChangeListener;
	private HighlightableDecoratedCellLabelProvider highlightableDecoratedCellLabelProvider;

	
	private class PatternDelegator {
		private String pattern;

		public String getPattern() {
			return pattern;
		}

		public void setPattern(final String pattern) {
			this.pattern = pattern;
			
			Display.getCurrent().syncExec(new Runnable() {
				@Override
				public void run() {
					getPatternFilter().setPattern(pattern);
					highlightableDecoratedCellLabelProvider.setPattern(pattern);
			
					getViewer().refresh();
				}
			});
		}
	}
	
	public AbstractEMFTreeTable(Composite parent, int style, EditingDomain editingDomain) {
		super(parent, style, new EMFTreePatternFilter(), true);

		getPatternFilter().setIncludeLeadingWildcard(true);
		
		ISWTObservableValue observableValue = WidgetProperties.text(SWT.Modify).observeDelayed(300, getFilterControl());
		IObservableValue observableValue2 = PojoProperties.value("pattern").observe(new PatternDelegator());
		ctx.bindValue(observableValue, observableValue2);

		this.editingDomain = editingDomain;

		treeColumnLayout = new TreeColumnLayout();
		getViewer().getTree().getParent().setLayout(treeColumnLayout);

		contentProvider = createContentProvider();
		treeViewer.setContentProvider(contentProvider);

		Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(getViewer()) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		};

		TreeViewerEditor.create(getViewer(), actSupport, ColumnViewerEditor.DEFAULT);
		
		
		IDragSupport dragSupport = createDragSupport();
		if (dragSupport != null) {
			getViewer().addDragSupport(dragSupport.getOperations(), dragSupport.getTransferTypes(), dragSupport.getListener());
		}
		
		IDropSupport dropSupport = createDropSupport();
		if (dropSupport != null) {
			getViewer().addDropSupport(dropSupport.getOperations(), dropSupport.getTransferTypes(), dropSupport.getListener());
		}
	}
	
	protected EObject getContainmentRoot() {
		return containmentRoot;
	}
	
	protected HighlightableDecoratedCellLabelProvider createHighlightableDecoratedCellLabelProvider(IObservableSet set, IEMFEditValueProperty property, IImageProvider imageProvider) {
		AbstractHighlightableTreeLabelProvider styledCellLabelProvider = createStyledCellLabelProvider(set, property, imageProvider);
		TreeLabelDecorator treeLabelDecorator = createTreeLabelDecorator();
		
		highlightableDecoratedCellLabelProvider = new HighlightableDecoratedCellLabelProvider(styledCellLabelProvider, treeLabelDecorator, null);
		return highlightableDecoratedCellLabelProvider;
	}
	
	protected AbstractHighlightableTreeLabelProvider createStyledCellLabelProvider(IObservableSet set, IEMFEditValueProperty property, final IImageProvider imageProvider) {
		return new EMFHighlightableTreeLabelProvider(getViewer(), property) {
			@Override
			public Image getImage(Object element) {
				if (imageProvider == null) {
					return null;
				}
				return imageProvider.getImage((EObject) element);
			}
		};
	}
	
	protected TreeLabelDecorator createTreeLabelDecorator() {
		return new TreeLabelDecorator() {
			@Override
			public Image decorateImage(Image image, Object element, IDecorationContext context) {
				// TODO decorate image
				return null;
			}
		};
	}
	
	abstract protected EReference getChildFeature(EObject eObject);
	
	protected ObservableListTreeContentProvider createContentProvider() {
		return new AbstractEMFContentProvider(createObservableFactory(), createTreeStructureAdvisor()) {

			@Override
			protected Object[] filter(Object[] input) {
				return AbstractEMFTreeTable.this.filter(input);
			}

			@Override
			public boolean hasChildren(Object element) {
				return AbstractEMFTreeTable.this.hasChildren(element);
			}
			
		};
	}
	
	/**
	 * Standard filter is no filter at all.
	 * 
	 * Override if you want to put a filter into place.
	 * 
	 * @param input
	 * @return
	 */
	protected Object[] filter(Object[] input) {
		return input;
	}
	
	/** Uses EMFs Containment feature to determine if the element has children.
	 * 
	 *  Override if the EMF Containment does not cover (all of the) children.  
	 * 
	 * @param element
	 * @return
	 */
	protected boolean hasChildren(Object element) {
		if (!(element instanceof EObject)) {
			return false;
		}
		EObject eObject = (EObject) element;
		EReference childFeature = getChildFeature(eObject);
		if (childFeature == null) {
			return false;
		}
		EList<?> children = (EList<?>) eObject.eGet(childFeature);
		return !children.isEmpty();
	}
	
	/** Uses EMFs Containment feature to determine Observables hierarchichly.
	 * 
	 *  Override if the EMF Containment does not cover (all of the) children.  
	 * 
	 * @return
	 */
	protected IObservableFactory createObservableFactory() {
		return new IObservableFactory() {
			@Override
			public IObservable createObservable(Object target) {
				if (target instanceof IObservableList) {
					return (IObservable) target;
				}
				
				if (!(target instanceof EObject)) {
					return null;
				}
				
				EObject eObject = (EObject) target;
				
				
				EReference childFeature = getChildFeature(eObject);
				return EMFProperties.list(childFeature).observe(target);
			}
		};
	}
	
	/** Uses EMFs Containment feature to determine an elements parent and children.
	 * 
	 * Required for filtering.
	 * 
	 * @return
	 */
	protected TreeStructureAdvisor createTreeStructureAdvisor() {
		return new TreeStructureAdvisor() {
			@Override
			public Object getParent(Object element) {
				if (!(element instanceof EObject)) {
					return null;
				}
				EObject eObject = (EObject) element;
				
				return eObject.eContainer();
			}

			@Override
			public Boolean hasChildren(Object element) {
				//TODO: check whether this is ever called or not
				if (!(element instanceof EObject)) {
					return null;
				}
				EObject eObject = (EObject) element;
				
				EList<?> children = (EList<?>) eObject.eGet(getChildFeature(eObject));
				return Boolean.valueOf(!children.isEmpty());
			}

		};
	}

	@Override
	public void dispose() {
		containmentRoot.eAdapters().remove(treeContentChangeListener);
		modelObservablesManager.dispose();
	}

	@Override
	public void setInput(EReference listProperty, EObject input) {
//		modelObservablesManager.dispose();
		
		IObservableList listObservable = MyEMFProperties.list(listProperty).observe(input);

		modelObservablesManager.addObservable(listObservable);

		getViewer().setInput(listObservable);

		setContainmentRoot(input);
	}
	
	private void setContainmentRoot(EObject containmentRoot) {
		this.containmentRoot = containmentRoot;
		treeContentChangeListener = createContentChangeListener();
		containmentRoot.eAdapters().add(treeContentChangeListener);
	}

	protected TreeContentChangeListener createContentChangeListener() {
		return new TreeContentChangeListener(getViewer());
	}
	
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}
	
	@Override
	public void createTableViewerColumn(String title, EAttribute eAttribute, int columWidth) {
		createTableViewerColumn(title, eAttribute, columWidth, null);
	}

	@Override
	public void createTableViewerColumn(String title, EAttribute eAttribute, int columWidth, IImageProvider imageProvider) {
		IEMFEditValueProperty property = EMFEditProperties.value(editingDomain, eAttribute);

		TreeViewerColumn result = new TreeViewerColumn(getViewer(), SWT.NONE);
		result.getColumn().setText(title);

		treeColumnLayout.setColumnData(result.getColumn(), new ColumnWeightData(columWidth, 10, true));

		IObservableSet set = contentProvider.getKnownElements();
		
		result.setLabelProvider(createHighlightableDecoratedCellLabelProvider(set, property, imageProvider));
		
		
		TextCellEditor cellEditor = new TextCellEditor(getViewer().getTree());
		IValueProperty observableValue = CellEditorProperties.control().value(WidgetProperties.text(SWT.Modify));
		EditingSupport editingSupport = ObservableValueEditingSupport.create(getViewer(), dataBindingContext, cellEditor,
				observableValue, property);
		result.setEditingSupport(editingSupport);
	}

	abstract protected IDropSupport createDropSupport();

	abstract protected IDragSupport createDragSupport();


	@Override
	public ISelection getSelection() {
		return getViewer().getSelection();
	}

	@Override
	public void setComparator(ViewerComparator comparator) {
		getViewer().setComparator(comparator);
	}
	

	@Override
	public void addDragSupport(int operations, Transfer[] transferTypes, ViewerDragAdapter listener) {
		getViewer().addDragSupport(operations, transferTypes, listener);
	}
}
