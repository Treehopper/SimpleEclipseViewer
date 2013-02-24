/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer.tree;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

final public class EMFTreePatternFilter extends PatternFilter {
	@Override
	protected boolean isLeafMatch(final Viewer viewer, final Object element) {
		final TreeViewer treeViewer = (TreeViewer) viewer;
		int numberOfColumns = treeViewer.getTree().getColumnCount();
		boolean isMatch = false;
		for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
			ILabelProvider labelProvider = (ILabelProvider) treeViewer.getLabelProvider(columnIndex);
			String labelText = labelProvider.getText(element);
			isMatch |= wordMatches(labelText);
		}
		
		if (isMatch) {
			treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					while (treeViewer.isBusy()) {
						try {
							wait(100);
						} catch (InterruptedException e) {
						}
					}
					treeViewer.expandAll();
				}
			});
		}
		
		return isMatch;
	}

}