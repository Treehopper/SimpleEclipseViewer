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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.jface.viewers.TreeViewer;

public class TreeContentChangeListener extends EContentAdapter {

	private TreeViewer treeViewer;

	public TreeContentChangeListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/**
	 * By default, all cross document references are followed. Usually this is
	 * not a great idea so this class can be subclassed to customize.
	 * 
	 * @param feature
	 *            a cross document reference
	 * @return whether the adapter should follow it
	 */
	protected boolean shouldAdapt(EStructuralFeature feature) {
		return true;
	}

	@Override
	protected void setTarget(EObject target) {
		super.setTarget(target);
		for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			Notifier notifier = featureIterator.next();
			EStructuralFeature feature = featureIterator.feature();
			if (shouldAdapt(feature)) {
				addAdapter(notifier);
			}
		}
	}

	@Override
	protected void unsetTarget(EObject target) {
		super.unsetTarget(target);
		for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			Notifier notifier = featureIterator.next();
			EStructuralFeature feature = featureIterator.feature();
			if (shouldAdapt(feature)) {
				removeAdapter(notifier);
			}
		}
	}

	@Override
	protected void selfAdapt(Notification notification) {
		super.selfAdapt(notification);
		if (!(notification.getNotifier() instanceof EObject)) {
			return;
		}
		if (!(notification.getFeature() instanceof EReference)) {
			return;
		}
		EReference eReference = (EReference) notification.getFeature();
		if (eReference.isContainment() || !shouldAdapt(eReference)) {
			return;
		}
		handleContainment(notification);
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		
		if (treeViewer == null || treeViewer.getTree().isDisposed()) {
			return;
		}
		
		int eventType = notification.getEventType();
		switch (eventType) {
		case Notification.SET:
			treeViewer.refresh(notification.getNotifier());	
			break;
			
		case Notification.ADD:
			//FIXME: remove commented code
//			treeViewer.refresh(notification.getNewValue());
//			treeViewer.setExpandedState(notification.getNotifier(), true);
			break;

		default:
			break;
		}

	}
}