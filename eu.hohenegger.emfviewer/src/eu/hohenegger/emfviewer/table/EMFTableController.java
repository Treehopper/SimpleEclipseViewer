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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class EMFTableController {

	private IEMFTableViewer contextTableViewer;
	private EObject actionable;
	private EReference actionableContexts;

	public void setInput(EReference actionableContexts, EObject actionable) {
		this.actionableContexts = actionableContexts;
		this.actionable = actionable;
		if (contextTableViewer == null) {
			return;
		}
		contextTableViewer.setInput(actionableContexts, actionable);
	}

	public void setView(IEMFTableViewer contextTableViewer) {
		this.contextTableViewer = contextTableViewer;
		if (contextTableViewer == null) {
			return;
		}
		setInput(actionableContexts, actionable);
	}

}
