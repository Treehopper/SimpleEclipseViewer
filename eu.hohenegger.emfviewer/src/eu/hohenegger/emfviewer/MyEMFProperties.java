/*******************************************************************************
 * Copyright (c) 2012 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Max Hohenegger - initial implementation
 ******************************************************************************/
package eu.hohenegger.emfviewer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.IEMFListProperty;
import org.eclipse.emf.databinding.internal.EMFListProperty;
import org.eclipse.emf.databinding.internal.EMFListPropertyDecorator;
import org.eclipse.emf.databinding.internal.EMFObservableListDecorator;
import org.eclipse.emf.ecore.EStructuralFeature;

public class MyEMFProperties extends EMFProperties {
	public static IEMFListProperty list(final EStructuralFeature feature) {
		final IListProperty property;
		property = new EMFListProperty(feature);
		return new EMFListPropertyDecorator(property, feature) {

			@Override
			public IObservableList observe(Object source) {
				return observe(Realm.getDefault(), source);
			}

			@Override
			public IObservableList observe(Realm realm, Object source) {
				return new EMFObservableListDecorator(property.observe(realm, source), feature) {
					@Override
					public int hashCode() {
						if (isDisposed()) {
							return -1;
						}
						return super.hashCode();
					}
				};
			}
		};
	}
}
