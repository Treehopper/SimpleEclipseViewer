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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDragSourceEffect;

public class LocalDragSupport implements IDragSupport {
	
	private DragSourceListener viewerDragAdapter;

	public LocalDragSupport(final TreeViewer viewer) {
		viewerDragAdapter = new TreeDragSourceEffect(viewer.getTree()) {
			@Override
			public void dragSetData(DragSourceEvent event) {
				
				if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
					LocalSelectionTransfer.getTransfer().setSelection(viewer.getSelection());
				}
				
			}
		};
	}

	@Override
	public Transfer[] getTransferTypes() {
		return new Transfer[] { LocalSelectionTransfer.getTransfer() };
	}
	
	@Override
	public int getOperations() {
		return DND.DROP_MOVE | DND.DROP_COPY;
	}
	
	@Override
	public DragSourceListener getListener() {
		return viewerDragAdapter;
	}
};