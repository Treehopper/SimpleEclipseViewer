/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer.table;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditValueProperty;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.CellEditorProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * <pre>
 * {@code
 * tableViewer = new EMFTableViewer(tableComposite, SWT.NONE | SWT.FULL_SELECTION, getEditingDomain());
 * tableViewer.createTableViewerColumn("Description", ModelPackage.eINSTANCE.getThing_Description(), 100);
 * tableViewer.setInput(ModelPackage.Literals.FOLDER__THOUGHTS, folder);
 * tableViewer.addSelectionChangedListener(new ModelElementSelectionChangedListener());
 * }
 * </pre>
 * 
 */
public class EMFTableViewer extends TableViewer implements IEMFTableViewer {
	private ObservablesManager modelObservablesManager = new ObservablesManager();

	private TableColumnLayout tableColumnLayout;

	private final EMFDataBindingContext dataBindingContext = new EMFDataBindingContext();
	private final ObservableListContentProvider listContentProvider = new ObservableListContentProvider();

	private EditingDomain editingDomain;

	public EditingDomain getEditingDomain() {
		return editingDomain;
	}

	public EMFTableViewer(Composite parent, int style, EditingDomain editingDomain) {
		super(parent, style);
		this.editingDomain = editingDomain;

		FillLayout layout = new FillLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		parent.setLayout(layout);

		tableColumnLayout = new TableColumnLayout();
		parent.setLayout(tableColumnLayout);

		setContentProvider(listContentProvider);

		Table actionTable = getTable();
		actionTable.setLinesVisible(true);
		actionTable.setHeaderVisible(true);

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(this) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		};

		TableViewerEditor.create(this, actSupport, ColumnViewerEditor.DEFAULT);
	}

	public void dispose() {
		modelObservablesManager.dispose();
	}

	@Override
	public void setInput(EReference listProperty, EObject input) {
		modelObservablesManager.dispose();

		IListProperty thoughtListProperty = EMFEditProperties.list(getEditingDomain(), listProperty);

		IObservableList listObservable = thoughtListProperty.observe(input);
		modelObservablesManager.addObservable(listObservable);

		setInput(listObservable);

	}

	@Override
	public void createTableViewerColumn(String title, EAttribute eAttribute, int columWidth) {
		createTableViewerColumn(title, eAttribute, columWidth, null);
	}

	@Override
	public void createTableViewerColumn(String title, EAttribute eAttribute, int columWidth, IImageProvider imageProvider) {
		IEMFEditValueProperty property = EMFEditProperties.value(getEditingDomain(), eAttribute);
		TableViewerColumn result = new TableViewerColumn(this, SWT.NONE);
		result.getColumn().setText(title);

		tableColumnLayout.setColumnData(result.getColumn(), new ColumnWeightData(columWidth, 10, true));

		IObservableSet set = listContentProvider.getKnownElements();

		ObservableMapCellLabelProvider labelProvider = new CellLabelProvider(property.observeDetail(set), imageProvider);

		result.setLabelProvider(labelProvider);
		TextCellEditor cellEditor = new TextCellEditor(getTable());
		IValueProperty observableValue = CellEditorProperties.control().value(WidgetProperties.text(SWT.Modify));
		EditingSupport editingSupport = ObservableValueEditingSupport.create(this, dataBindingContext, cellEditor,
				observableValue, property);
		result.setEditingSupport(editingSupport);
	}
}
