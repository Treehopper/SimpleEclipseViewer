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

import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;


public class HighlightableDecoratedCellLabelProvider extends DecoratingStyledCellLabelProvider implements ILabelProvider {

	public HighlightableDecoratedCellLabelProvider(AbstractHighlightableTreeLabelProvider labelProvider, ILabelDecorator decorator,
			IDecorationContext decorationContext) {
		super(labelProvider, decorator, decorationContext);
	}

	@Override
	public String getText(Object element) {
		return getStyledText(element).getString();
	}

	public void setPattern(String pattern) {
		AbstractHighlightableTreeLabelProvider styledStringProvider = (AbstractHighlightableTreeLabelProvider) getStyledStringProvider();
		styledStringProvider.setPattern(pattern);
	}
}