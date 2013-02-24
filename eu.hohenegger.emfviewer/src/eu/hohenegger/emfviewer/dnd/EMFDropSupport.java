/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer.dnd;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetListener;

public class EMFDropSupport extends AbstractDropSupport {
	private EffectDropAdapter dropListener;
	
	public EMFDropSupport(TreeViewer treeViewer, EditingDomain editingDomain) {
		dropListener = new EMFContainmentDropAdapter(treeViewer, editingDomain);
	}

	@Override
	public void setRoot(Object object) {
		dropListener.setRoot((EObject) object);
	}
	
	@Override
	public DropTargetListener getListener() {
		return dropListener;
	}

}
