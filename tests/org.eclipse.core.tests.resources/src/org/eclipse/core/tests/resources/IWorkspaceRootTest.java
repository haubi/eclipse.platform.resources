/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.resources;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

public class IWorkspaceRootTest extends ResourceTest {
	public IWorkspaceRootTest() {
		super();
	}

	public IWorkspaceRootTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(IWorkspaceRootTest.class);
	}

	protected void tearDown() throws Exception {
		IProject[] projects = getWorkspace().getRoot().getProjects();
		getWorkspace().delete(projects, true, null);
	}

	/**
	 * Tests the API method findContainersForLocation.
	 */
	public void testFindContainersForLocation() {
		// TODO
	}

	/**
	 * Tests the API method findFilesForLocation.
	 */
	public void testFindFilesForLocation() {
		// TODO
	}

	/**
	 * Tests the API method getContainerForLocation.
	 */
	public void testGetContainerForLocation() {
		IWorkspaceRoot root = getWorkspace().getRoot();
		assertEquals("1.0", root, root.getContainerForLocation(root.getLocation()));

	}

	/**
	 * Tests the API method getFileForLocation
	 */
	public void testGetFileForLocation() {
		IWorkspaceRoot root = getWorkspace().getRoot();
		assertTrue("1.0", root.getFileForLocation(root.getLocation()) == null);
	}

	public void testPersistentProperty() {
		IWorkspaceRoot root = getWorkspace().getRoot();
		String value = "this is a test property value";
		QualifiedName name = new QualifiedName("test", "testProperty");
		try {
			root.setPersistentProperty(name, value);
		} catch (CoreException e) {
			fail("1.0", e);
		}

		try {
			String storedValue = root.getPersistentProperty(name);
			assertEquals("2.0", value, storedValue);
		} catch (CoreException e) {
			fail("2.1", e);
		}

		try {
			name = new QualifiedName("test", "testNonProperty");
			String storedValue = root.getPersistentProperty(name);
			assertEquals("3.0", null, storedValue);
		} catch (CoreException e) {
			fail("3.1", e);
		}
	}

	/**
	 * Since reading a persistent property doesn't normally happen inside an
	 * operation, try it inside an operation to make sure it still works.
	 * (See bug 14179).
	 */
	public void testPersistentPropertyInRunnable() {
		final IWorkspaceRoot root = getWorkspace().getRoot();
		final String value = "this is a test property value";
		final QualifiedName name = new QualifiedName("test", "testProperty");
		try {
			getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					root.setPersistentProperty(name, value);
				}
			}, getMonitor());
		} catch (CoreException e) {
			fail("1.0", e);
		}

		try {
			final String[] storedValue = new String[1];
			getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					storedValue[0] = root.getPersistentProperty(name);
				}
			}, getMonitor());
			assertEquals("2.0", value, storedValue[0]);
		} catch (CoreException e) {
			fail("2.1", e);
		}

		try {
			final QualifiedName name2 = new QualifiedName("test", "testNonProperty");
			final String[] storedValue = new String[1];
			getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					storedValue[0] = root.getPersistentProperty(name2);
				}
			}, getMonitor());
			assertEquals("3.0", null, storedValue[0]);
		} catch (CoreException e) {
			fail("3.1", e);
		}
	}

	public void testRefreshLocal() {
		IWorkspaceRoot root = getWorkspace().getRoot();
		IProject project = root.getProject("Project");
		ensureExistsInWorkspace(project, true);
		try {
			project.close(getMonitor());
		} catch (CoreException e) {
			fail("1.0", e);
		}
		//refreshing the root shouldn't fail
		try {
			root.refreshLocal(IResource.DEPTH_INFINITE, getMonitor());
		} catch (CoreException e) {
			fail("1.1", e);
		}
	}
}