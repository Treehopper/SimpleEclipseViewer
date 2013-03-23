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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.hohenegger.emfviewer.dnd.EMFDropSupport;
import eu.hohenegger.emfviewer.dnd.IDragSupport;
import eu.hohenegger.emfviewer.dnd.IDropSupport;
import eu.hohenegger.emfviewer.dnd.LocalDragSupport;

public class EMFContainmentTreeTable extends AbstractEMFTreeTable {

	private IDropSupport dropSupport;

	public EMFContainmentTreeTable(Composite parent, int style, EditingDomain editingDomain) {
		super(parent, style | SWT.FULL_SELECTION, editingDomain);
	}

	protected EReference getChildFeature(EObject eObject) {
		return eObject.eContainmentFeature();
	}
	
	protected IDropSupport createDropSupport() {
		dropSupport = new EMFDropSupport(getViewer(), getEditingDomain());
		return dropSupport;
	}

	protected IDragSupport createDragSupport() {
		return new LocalDragSupport(getViewer());
	}
	
	@Override
	public void setInput(EReference listProperty, EObject input) {
		super.setInput(listProperty, input);
		dropSupport.setRoot(input);
	}
}
