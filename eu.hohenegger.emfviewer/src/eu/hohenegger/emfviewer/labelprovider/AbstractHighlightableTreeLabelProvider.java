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

import java.util.Arrays;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractHighlightableTreeLabelProvider extends StyledCellLabelProvider implements IStyledLabelProvider {

	private Display display;
	private String pattern;
	private Font boldFont;

	public AbstractHighlightableTreeLabelProvider(TreeViewer treeViewer) {
		this.display = treeViewer.getTree().getDisplay();
		
		FontData[] boldFontData = restyleFontData(treeViewer.getControl().getFont().getFontData(), SWT.BOLD);
		boldFont = new Font(display, boldFontData);
	}

	protected Display getDisplay() {
		return display;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern.toLowerCase();
	}

	public StyledString highlight(StyledString styledString) {
		if (pattern == null || pattern.isEmpty() || styledString == null) {
			return styledString;
		}
		int indexOf = styledString.getString().toLowerCase().indexOf(pattern);

		if (indexOf != -1) {
			styledString.setStyle(indexOf, pattern.length(), new StyledString.Styler() {
				@Override
				public void applyStyles(TextStyle textStyle) {
					textStyle.font = boldFont;
//					textStyle.background = display.getSystemColor(SWT.COLOR_YELLOW);
				}
			});
		}

		return styledString;
	}

	private static FontData[] restyleFontData(FontData[] originalData, int additionalStyle) {
		FontData[] result = Arrays.copyOf(originalData, originalData.length);
		for (FontData fontData : result) {
			fontData.setStyle(fontData.getStyle() | additionalStyle);
		}
		return result;
	}

	@Override
	public StyledString getStyledText(Object element) {
		return highlight(new StyledString(getText(element)));
	}
	
	protected abstract String getText(Object element);

	@Override
	public void dispose() {
		super.dispose();
		boldFont.dispose();
	}
}
