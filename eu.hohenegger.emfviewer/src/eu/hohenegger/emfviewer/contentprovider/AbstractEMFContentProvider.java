/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer.contentprovider;

import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;

public abstract class AbstractEMFContentProvider extends ObservableListTreeContentProvider {
	public AbstractEMFContentProvider(IObservableFactory listFactory, TreeStructureAdvisor structureAdvisor) {
		super(listFactory, structureAdvisor);
	}

	abstract protected Object[] filter(Object[] input);

	@Override
	abstract public boolean hasChildren(Object element);

	@Override
	public Object[] getElements(Object inputElement) {
		return filter(super.getElements(inputElement));
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return filter(super.getChildren(parentElement));
	}
}
