/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer.table;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class CellLabelProvider extends ObservableMapCellLabelProvider {

	private IImageProvider imageProvider;

	public CellLabelProvider(IObservableMap attributeMap, IImageProvider imageProvider) {
		super(attributeMap);
		this.imageProvider = imageProvider;
	}

	public CellLabelProvider(IObservableMap attributeMap) {
		this(attributeMap, null);
	}

	@Override
	public void update(ViewerCell cell) {
		super.update(cell);
		if (imageProvider == null) {
			return;
		}
		cell.setImage(imageProvider.getImage((EObject) cell.getElement()));
	}
}