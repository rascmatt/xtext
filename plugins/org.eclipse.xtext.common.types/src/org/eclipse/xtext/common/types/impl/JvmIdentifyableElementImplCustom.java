/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.common.types.impl;

public abstract class JvmIdentifyableElementImplCustom extends JvmIdentifyableElementImpl {

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(eClass().getName());
		result.append(": ");
		if (eIsProxy()) {
			result.append(" (eProxyURI: ");
			result.append(eProxyURI());
			result.append(')');
		} else {
			result.append(getCanonicalName());
		}
		return result.toString();
	}
}
