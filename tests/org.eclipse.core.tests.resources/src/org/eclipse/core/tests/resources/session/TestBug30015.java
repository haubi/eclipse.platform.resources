/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.core.tests.resources.session;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.*;
import org.eclipse.core.tests.harness.WorkspaceSessionTest;

/**
 * Tests regression of bug 30015.  Due to this bug, it was impossible to restore
 * a project whose location was relative to a workspace path variable.
 */
public class TestBug30015 extends WorkspaceSessionTest {
	protected static final String PROJECT_NAME = "Project";
	protected static final String VAR_NAME = "ProjectLocatio";
	protected IPath varValue;
	protected IPath rawLocation;
	
	public TestBug30015() {
		super();
	}
	public TestBug30015(String name) {
		super(name);
	}
	/**
	 * Create and open the project
	 */
	public void test1() {
		varValue = Platform.getLocation().removeLastSegments(1);
		rawLocation = new Path(VAR_NAME).append("ProjectLocation");
		//define the variable
		try {
			getWorkspace().getPathVariableManager().setValue(VAR_NAME, varValue);
		} catch (CoreException e) {
			fail("1.0", e);
		}
		IProject project = getWorkspace().getRoot().getProject(PROJECT_NAME);
		IProjectDescription description = getWorkspace().newProjectDescription(PROJECT_NAME);
		description.setLocation(rawLocation);
		//create the project
		try {
			project.create(description, getMonitor());
			project.open(getMonitor());
		} catch (CoreException e) {
			fail("9.99", e);
		}
		//save and shutdown
		try {
			getWorkspace().save(true, getMonitor());
		} catch (CoreException e) {
			fail("9.99", e);
		}
	}
	/**
	 * See if the project was successfully restored.
	 */
	public void test2() {
		varValue = Platform.getLocation().removeLastSegments(1);
		rawLocation = new Path(VAR_NAME).append("ProjectLocation");
		IProject project = getWorkspace().getRoot().getProject(PROJECT_NAME);
		
		assertEquals("1.0", varValue, getWorkspace().getPathVariableManager().getValue(VAR_NAME));
		assertTrue("1.1", project.exists());
		assertTrue("1.2", project.isOpen());
		assertEquals("1.3", rawLocation, project.getRawLocation());
		assertEquals("1.4", varValue.append(rawLocation.lastSegment()), project.getLocation());
	}
}