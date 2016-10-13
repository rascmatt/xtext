/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.web.example.jetty;

import com.google.inject.Provider;
import java.util.concurrent.ExecutorService;
import org.eclipse.xtext.web.server.DefaultWebModule;

/**
 * Manual modifications go to {@link StatemachineWebModule}.
 */
@SuppressWarnings("all")
public abstract class AbstractStatemachineWebModule extends DefaultWebModule {

	public AbstractStatemachineWebModule() {
		super();
	}

	public AbstractStatemachineWebModule(Provider<ExecutorService> executorServiceProvider) {
		super(executorServiceProvider);
	}
	
}
