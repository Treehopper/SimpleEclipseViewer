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

import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

public interface IDragSupport {
	int getOperations();
	Transfer[] getTransferTypes();
	DragSourceListener getListener();
}
