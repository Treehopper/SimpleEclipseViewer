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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.TreeItem;

public abstract class EffectDropAdapter extends TreeDropTargetEffect {
	private Object target;
	private Object containmentRoot;

	public EffectDropAdapter(TreeViewer viewer) {
		super(viewer.getTree());
	}

	abstract protected boolean isTargetRelatedTofSource(Object target, StructuredSelection treeSelection);

	abstract protected boolean isTargetPartOfSource(Object target, StructuredSelection treeSelection);
	
	abstract protected boolean isTargetEqualSourceParent(Object target, StructuredSelection treeSelection);

	@Override
	public void drop(DropTargetEvent event) {
		StructuredSelection sourceSelection = (StructuredSelection) event.data;
		
		// Dropped into empty space
		if (target == null) {
			target = containmentRoot;
			if (isTargetEqualSourceParent(target, sourceSelection)) {
				event.detail = DND.DROP_NONE;
			}
		}
		if (isTargetPartOfSource(target, sourceSelection) 
				|| isTargetRelatedTofSource(target, sourceSelection)) {
			event.detail = DND.DROP_NONE;
		}

		if (event.detail != DND.DROP_NONE) {
			performDrop(target, sourceSelection);
		}
		
		super.drop(event);
	}

	abstract protected void performDrop(Object target, StructuredSelection sourceSelection);

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = event.feedback | DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
		super.dragOver(event);
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		TreeItem treeItem = (TreeItem) event.item;
		if (treeItem == null) {
			target = getRoot();
		} else {
			target = treeItem.getData();
		}
		
		super.dropAccept(event);
	}

	
	
	protected void setTarget(Object target) {
		this.target = target;
	}

	public void setRoot(Object containmentRoot) {
		this.containmentRoot = containmentRoot;
	}

	protected Object getRoot() {
		return containmentRoot;
	}
}
