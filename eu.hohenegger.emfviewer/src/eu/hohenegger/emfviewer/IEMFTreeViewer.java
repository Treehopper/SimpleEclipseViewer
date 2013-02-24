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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.dnd.Transfer;


public interface IEMFTreeViewer {

	void createTableViewerColumn(String title, EAttribute eAttribute, int columWidth);

	void setInput(EReference listProperty, EObject input);

	void createTableViewerColumn(String title, EAttribute eAttribute, int columWidth, IImageProvider imageProvider);

	ISelection getSelection();

	void dispose();

	void setComparator(ViewerComparator viewerComparator);
	
	Viewer getViewer();

	void addDragSupport(int i, Transfer[] transferTypes, ViewerDragAdapter thoughtDragListener);

}
