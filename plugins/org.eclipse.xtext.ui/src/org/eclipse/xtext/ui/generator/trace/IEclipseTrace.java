/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.generator.trace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.trace.ILocationInResource;
import org.eclipse.xtext.generator.trace.ITrace;
import org.eclipse.xtext.util.ITextRegion;

/**
 * Eclipse specific extension to the trace information.
 * 
 * @author Sebastian Zarnekow - Initial contribution and API
 * @since 2.9
 */
public interface IEclipseTrace extends ITrace {

	/**
	 * Returns the source project. Never <code>null</code>.
	 * @return the source project. Never <code>null</code>.
	 */
	IProject getLocalProject();
	
	/**
	 * Returns the storage that is associated with this trace.
	 * @return the associated storage. Never <code>null</code>.
	 */
	IStorage getLocalStorage();

	/**
	 * Returns the best {@link ILocationInResource location} that matches the given
	 * {@code sourceRegion} in the {@code targetResource}.
	 * If the region does not match a single location in the target, the following strategy applies:
	 * <ul>
	 * <li>
	 * 		The merged region of all matching locations in the {@code targetResource}
	 *      is returned. 
	 * </li>
	 * </ul> 
	 * If no location data is available or the {@code sourceRegion} does not yield
	 * a location in {@code targetResource}, returns <code>null</code>.
	 * @param localRegion the region in the current resource. May not be <code>null</code>.
	 * @param associatedStorage the expected target resource. May not be <code>null</code>.
	 * @return the best associated location or <code>null</code> if none.
	 */
	/* @Nullable */ ILocationInEclipseResource getBestAssociatedLocation(ITextRegion localRegion, IStorage associatedStorage);
	
	/**
	 * Returns all individual {@link ILocationInResource locations} that match the given {@code sourceRegion}
	 * for the expected {@code targetResource}.
	 * @param localRegion the region in the current resource. May not be <code>null</code>.
	 * @param associatedStorage the expected target resource. May not be <code>null</code>.
	 * @return all associated locations. Never <code>null</code>. 
	 */
	Iterable<? extends ILocationInEclipseResource> getAllAssociatedLocations(ITextRegion localRegion, IStorage associatedStorage);
	
	/**
	 * Returns all known {@link ILocationInResource locations} that were produced from the associated resource
	 * in the given {@code targetResource}.
	 * @param associatedStorage the expected target resource. May not be <code>null</code>.
	 * @return all locations. Never <code>null</code>. 
	 */
	Iterable<? extends ILocationInEclipseResource> getAllAssociatedLocations(IStorage associatedStorage);

	@Override
	ILocationInEclipseResource getBestAssociatedLocation(ITextRegion localRegion);

	@Override
	Iterable<? extends ILocationInEclipseResource> getAllAssociatedLocations(ITextRegion localRegion);

	@Override
	Iterable<? extends ILocationInEclipseResource> getAllAssociatedLocations();

	@Override
	Iterable<? extends IEclipseTrace> getAllInverseTraces();

	@Override
	IEclipseTrace getInverseTrace(URI uri);

	@Override
	ILocationInEclipseResource getBestAssociatedLocation(ITextRegion localRegion, URI uri);

	@Override
	Iterable<? extends ILocationInEclipseResource> getAllAssociatedLocations(ITextRegion localRegion, URI uri);

	@Override
	Iterable<? extends ILocationInEclipseResource> getAllAssociatedLocations(URI uri);


}
