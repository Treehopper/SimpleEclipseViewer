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

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractEMFTable extends AbstractEMFTreeTable {

	public AbstractEMFTable(Composite parent, int style, EditingDomain editingDomain) {
		super(parent, style, editingDomain);
	}
	
	protected boolean hasChildren(Object element) {
		return false;
	}
	
	@Override
	protected EReference getChildFeature(EObject eObject) {
		return eObject.eContainmentFeature();
	}
	
	protected IObservableFactory createObservableFactory() {
		return new IObservableFactory() {
			@Override
			public IObservable createObservable(Object target) {
				if (target instanceof IObservableList) {
					return (IObservable) target;
				} /*else {
					return multi.observe(target);
					This causes: Exception in thread "main" java.lang.NullPointerException at java.util.Collections$UnmodifiableCollection.<init>(Unknown Source)
				}*/
				
				//not necessary to return anything else, as long it is only a list
				return null; 
			}
		};
	}
	
	@Override
	protected TreeStructureAdvisor createTreeStructureAdvisor() {
		return new TreeStructureAdvisor() {
			@Override
			public Object getParent(Object element) {
				 //not necessary to return anything else, as long it is only a list
				return null;
			}

			@Override
			public Boolean hasChildren(Object element) {
				 //not necessary to return anything else, as long it is only a list
				return Boolean.FALSE;
			}

		};
	}
}
