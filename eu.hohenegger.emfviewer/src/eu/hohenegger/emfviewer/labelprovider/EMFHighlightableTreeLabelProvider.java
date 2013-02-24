/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer.labelprovider;

import org.eclipse.emf.databinding.edit.IEMFEditValueProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

public class EMFHighlightableTreeLabelProvider extends AbstractHighlightableTreeLabelProvider {

	private IEMFEditValueProperty property;

	public EMFHighlightableTreeLabelProvider(TreeViewer treeViewer, IEMFEditValueProperty property) {
		super(treeViewer);
		this.property = property;
	}


	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getText(Object element) {
		EObject eObject = (EObject) element;
		return (String) property.getValue(eObject);
	}

}
