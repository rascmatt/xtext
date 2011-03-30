/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.generator.compare;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.mwe2.runtime.Mandatory;
import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.generator.BindFactory;
import org.eclipse.xtext.generator.Binding;
import org.eclipse.xtext.generator.junit.Junit4Fragment;
import org.eclipse.xtext.generator.resourceFactory.ResourceFactoryFragment;

/**
 * @author Michael Clay - Initial contribution and API
 */
public class CompareFragment extends ResourceFactoryFragment {
	private static final Logger log = Logger.getLogger(Junit4Fragment.class);

	@Override
	public void generate(Grammar grammar, XpandExecutionContext ctx) {
		if (log.isInfoEnabled()) {
			log.info("generating Compare Framework infrastructure");
		}
		super.generate(grammar, ctx);
	}

	@Override
	public Set<Binding> getGuiceBindingsUi(Grammar grammar) {
		return new BindFactory().addTypeToType("org.eclipse.compare.IViewerCreator",
				"org.eclipse.xtext.ui.compare.DefaultViewerCreator").getBindings();
	}

	@Override
	public String[] getRequiredBundlesUi(Grammar grammar) {
		return new String[] { "org.eclipse.compare" };
	}
	
	@Override
	@Mandatory
	public void setFileExtensions(String fileExtensions) {
		super.setFileExtensions(fileExtensions);
	}
}
