/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.scoping.impl;

import static com.google.common.collect.Iterables.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.AliasedEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.util.SimpleAttributeResolver;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class ImportedNamespaceAwareLocalScopeProvider extends AbstractGlobalScopeDelegatingScopeProvider {

	/**
	 * @author Sven Efftinge - Initial contribution and API
	 * 
	 */
	public static class ImportNormalizer {
		private final QualifiedName name;

		public ImportNormalizer(QualifiedName importedNamespace) {
			this.name = importedNamespace;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof ImportNormalizer && name.equals(((ImportNormalizer)obj).name);
		}

		public String shortToLongName(String shortName) {
			if (!name.hasWildCard()) {
				if (name.lastSegment().equals(shortName)) {
					return name.toString();
				}
			} else {
				return name.replaceWildcard(shortName).toString();
			}
			return null;
		}

		public String longToShortName(String from) {
			if (!name.hasWildCard()) {
				if (name.toString().equals(from)) {
					return name.lastSegment();
				}
			} else {
				QualifiedName fromName = new QualifiedName(from);
				List<String> fromSegments = fromName.segments();
				List<String> importSegments = name.segments();
				if (importSegments.size() < fromSegments.size()) {
					if (importSegments.equals(fromSegments.subList(0, importSegments.size()))) {
						return new QualifiedName(fromSegments.subList(importSegments.size(), fromSegments.size()))
								.toString();
					}
				}
			}
			return null;
		}
	}

	private IQualifiedNameProvider nameProvider;

	@Inject
	public void setNameProvider(IQualifiedNameProvider nameProvider) {
		this.nameProvider = nameProvider;
	}

	public IQualifiedNameProvider getNameProvider() {
		return nameProvider;
	}

	public IScope getScope(EObject context, EReference reference) {
		if (context == null)
			return getGlobalScope(context, reference);
		IScope result = null;
		if (context.eContainer() == null) {
			// global scope
			result = getGlobalScope(context, reference);
			result = getResourceScope(result, context, reference);
		} else {
			// outer scope
			result = getScope(context.eContainer(), reference);
		}

		// local scope used by the import scope
		if (hasImports(context)) {
			IScope localElements = getLocalElements(result, context, reference);
			// imports
			result = getImportedElements(result, localElements, context, reference);
		}
		// local scope
		if (nameProvider.getQualifiedName(context) != null) {
			result = getLocalElements(result, context, reference);
		}
		return result;
	}

	protected boolean hasImports(final EObject context) {
		return !getImportNormalizer(context).isEmpty();
	}

	protected IScope getResourceScope(IScope parent, final EObject context, final EReference reference) {
		if (context.eResource() == null)
			return parent;
		Iterable<EObject> contents = new Iterable<EObject>() {
			public Iterator<EObject> iterator() {
				// context can be a proxy when the iterable will be queried
				if (context.eResource() == null)
					return Collections.<EObject>emptyList().iterator();
				return EcoreUtil.getAllProperContents(context.eResource(), true);
			}
		};
		contents = Iterables.filter(contents, typeFilter(reference.getEReferenceType()));
		return Scopes.scopeFor(contents, nameProvider, parent);
	}

	private IScope getLocalElements(IScope parent, final EObject context, final EReference reference) {
		final String commonPrefix = nameProvider.getQualifiedName(context) + ".";

		Iterable<EObject> contents = new Iterable<EObject>() {
			public Iterator<EObject> iterator() {
				return EcoreUtil.getAllProperContents(context, true);
			}
		};
		// filter by type
		contents = filter(contents, typeFilter(reference.getEReferenceType()));
		// transform to IScopedElements
		Function<EObject, IEObjectDescription> descriptionComputation = new Function<EObject, IEObjectDescription>() {
			public IEObjectDescription apply(EObject from) {
				final String fqn = nameProvider.getQualifiedName(from);
				if (fqn == null)
					return null;
				String name = fqn;
				if (fqn.startsWith(commonPrefix)) {
					name = fqn.substring(commonPrefix.length());
				}
				return new EObjectDescription(name, from, Collections.<String, String>emptyMap()) {
					@Override
					public String getQualifiedName() {
						return fqn;
					}
				};
			}
		};
		return new SimpleScope(parent, Iterables.filter(Iterables.transform(contents, descriptionComputation), Predicates.notNull()));
	}

	private Predicate<EObject> typeFilter(final EClass type) {
		return new Predicate<EObject>() {

			public boolean apply(EObject input) {
				return type.isInstance(input);
			}
		};
	}

	protected Set<ImportNormalizer> getImportNormalizer(EObject context) {
		Set<ImportNormalizer> namespaceImports = new HashSet<ImportNormalizer>();
		SimpleAttributeResolver<EObject, String> importResolver = SimpleAttributeResolver.newResolver(String.class,
				"importedNamespace");
		for (EObject child : context.eContents()) {
			String value = importResolver.getValue(child);
			if (value != null) {
				namespaceImports.add(createImportNormalizer(value));
			}
		}
		return namespaceImports;
	}

	protected ImportNormalizer createImportNormalizer(final String name) {
		return new ImportNormalizer(new QualifiedName(name));
	}

	protected IScope getImportedElements(final IScope parent, final IScope localElements, final EObject context,
			final EReference reference) {
		final Set<ImportNormalizer> normalizers = getImportNormalizer(context);

		return new AbstractScope() {

			@Override
			public IEObjectDescription getContentByName(String name) {
				for (ImportNormalizer normalizer : normalizers) {
					String shortToLongName = normalizer.shortToLongName(name);
					if (shortToLongName != null) {
						IEObjectDescription element = localElements.getContentByName(shortToLongName);
						if (element != null)
							return new AliasedEObjectDescription(name, element);
					}
				}
				return getOuterScope().getContentByName(name);
			}
			
			@Override
			public IEObjectDescription getContentByEObject(EObject object) {
				IEObjectDescription candidate = localElements.getContentByEObject(object);
				for (ImportNormalizer normalizer : normalizers) {
					String longToShortName = normalizer.longToShortName(candidate.getQualifiedName());
					if (longToShortName != null) {
						IEObjectDescription element = getContentByName(longToShortName);
						if (element != null && element.getEObjectOrProxy() == object)
							return new AliasedEObjectDescription(longToShortName, candidate);
					}
				}
				return getOuterScope().getContentByEObject(object);
			}

			@Override
			protected Iterable<IEObjectDescription> internalGetContents() {
				return filter(transform(localElements.getAllContents(),
						new Function<IEObjectDescription, IEObjectDescription>() {

							public IEObjectDescription apply(final IEObjectDescription input) {
								for (ImportNormalizer normalizer : normalizers) {
									final String newName = normalizer.longToShortName(input.getName());
									if (newName != null) {
										return new AliasedEObjectDescription(newName, input);
									}
								}
								return null;
							}
						}), Predicates.notNull());
			}

			public IScope getOuterScope() {
				return parent;
			}

		};
	}

}
