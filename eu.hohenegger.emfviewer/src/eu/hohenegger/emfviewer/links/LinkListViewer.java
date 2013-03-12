package eu.hohenegger.emfviewer.links;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class LinkListViewer extends Composite implements IHyperlinkListener, IListChangeListener, IChangeListener {
	private Composite linkContainer;
	private List<IHyperlinkListener> listeners = new ArrayList<>();
	private EReference eReference;
	private EObject input;
	private EAttribute eAttribute;

	private IObservableList listObservable;
	private Map<IObservableValue, ImageHyperlink> map;

	public LinkListViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
	}

	public void setInput(EReference eReference, EObject input, final EAttribute eAttribute) {
		this.eReference = eReference;
		this.input = input;
		this.eAttribute = eAttribute;

		if (map != null) {
			Set<IObservableValue> keySet = map.keySet();
			for (IObservableValue observableValue : keySet) {
				observableValue.removeChangeListener(this);
				map.get(observableValue).dispose();
			}
		}
		map = new HashMap<>();

		if (linkContainer != null && !linkContainer.isDisposed()) {
			linkContainer.dispose();
		}
		linkContainer = new Composite(this, SWT.NONE);
		// GridLayoutFactory.fillDefaults().applyTo(linkContainer);
		TableWrapLayout layout = new TableWrapLayout();
		linkContainer.setLayout(layout);

		if (listObservable != null && !listObservable.isDisposed()) {
			listObservable.removeListChangeListener(this);
			listObservable.dispose();
		}
		listObservable = EMFProperties.list(eReference).observe(input);

		listObservable.addListChangeListener(this);

		for (Object object : listObservable) {
			EObject eObject = (EObject) object;
			IObservableValue observableValue = EMFProperties.value(eAttribute).observe(eObject);
			observableValue.addChangeListener(LinkListViewer.this);

			ImageHyperlink imageLink = new ImageHyperlink(linkContainer, SWT.WRAP);
			TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
			imageLink.setLayoutData(td);
			// imageLink.setImage(image);
			imageLink.setText(observableValue.getValue().toString());
			imageLink.setUnderlined(true);
			imageLink.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
			imageLink.addHyperlinkListener(LinkListViewer.this);
			imageLink.setData(eObject);

			map.put(observableValue, imageLink);

			layout(true, true);
			pack(true);
			redraw();
		}

	}

	public void addHyperlinkListener(IHyperlinkListener listener) {
		listeners.add(listener);
	}

	public void removeHyperlinkListener(IHyperlinkListener listener) {
		listeners.add(listener);
	}

	@Override
	public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent event) {
		for (IHyperlinkListener listener : listeners) {
			listener.linkEntered(event);
		}
	}

	@Override
	public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent event) {
		for (IHyperlinkListener listener : listeners) {
			listener.linkExited(event);
		}
	}

	@Override
	public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent event) {
		for (IHyperlinkListener listener : listeners) {
			listener.linkActivated(event);
		}
	}

	@Override
	public void handleListChange(ListChangeEvent event) {
		setInput(eReference, input, eAttribute);
	}

	@Override
	public void handleChange(ChangeEvent event) {
		final IObservableValue source = (IObservableValue) event.getSource();
		final ImageHyperlink imageHyperlink = map.get(source);
		Display.getCurrent().syncExec(new Runnable() {
			@Override
			public void run() {
				imageHyperlink.setText(source.getValue().toString());
			}
		});
		layout(true, true);
		pack(true);
		redraw();
	}
}
