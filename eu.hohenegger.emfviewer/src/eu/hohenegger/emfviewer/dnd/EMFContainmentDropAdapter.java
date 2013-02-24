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

import java.util.Iterator;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;

public final class EMFContainmentDropAdapter extends EffectDropAdapter {
	private EditingDomain editingDomain;

	public EMFContainmentDropAdapter(TreeViewer viewer, EditingDomain editingDomain) {
		super(viewer);
		this.editingDomain = editingDomain;
	}

	protected boolean isTargetRelatedTofSource(Object target, StructuredSelection treeSelection) {
		EObject eObject = (EObject) target;
		
		@SuppressWarnings("unchecked")
		Iterator<EObject> iterator = treeSelection.iterator();
		while (iterator.hasNext()) {
			EObject nextSource = iterator.next();
			if(EcoreUtil.isAncestor(nextSource, eObject)) {
				return true;
			}
		}
		
		return false;
	}

	protected boolean isTargetPartOfSource(Object target, StructuredSelection treeSelection) {
		@SuppressWarnings("unchecked")
		Iterator<EObject> iterator = treeSelection.iterator();
		while (iterator.hasNext()) {
			if (target.equals(iterator.next())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isTargetEqualSourceParent(Object target, StructuredSelection treeSelection) {
		Iterator<?> iterator = treeSelection.iterator();
		while (iterator.hasNext()) {
			Object next = (EObject) iterator.next();
			if (!(next instanceof EObject)) {
				continue;
			}
			EObject eObject = (EObject) next;
			if (target.equals(eObject.eContainer())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		TreeItem treeItem = (TreeItem) event.item;
		if (treeItem == null) {
			setTarget(getRoot());
		} else {
			setTarget(treeItem.getData());
		}
		
		super.dropAccept(event);
	}

	@Override
	protected void performDrop(Object target, StructuredSelection sourceSelection) {
		CompoundCommand compoundCommand = new CompoundCommand();

		@SuppressWarnings("unchecked")
		Iterator<EObject> iterator = sourceSelection.iterator();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			EObject eTarget = (EObject) target;

			AddCommand command = new AddCommand(editingDomain, (EList<?>) eTarget.eGet(eObject.eContainmentFeature()), eObject); //This always throws an exception.
			compoundCommand.append(command);

			// Alternative w/o Command:
			// eObject.eSet(eObject.eContainmentFeature().getEOpposite(), target);
		}
		
		editingDomain.getCommandStack().execute(compoundCommand);
		
	}
}
