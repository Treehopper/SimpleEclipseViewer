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

import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.swt.graphics.Image;

public abstract class TreeLabelDecorator extends LabelDecorator {
	private Image decoratedImage;

	@Override
	public void dispose() {
		if (decoratedImage != null) {
			decoratedImage.dispose();
			decoratedImage = null;
		}
	}

	@Override
	public String decorateText(String text, Object element, IDecorationContext context) {
		return null;
	}

	@Override
	public boolean prepareDecoration(Object element, String originalText, IDecorationContext context) {
		return true; // if false is returned, update will not happen
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		return decorateImage(image, element, null);
	}

	@Override
	public String decorateText(String text, Object element) {
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}