/**
 * Copyright 2013 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.runtime.identity;

import java.util.List;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public interface IIDFactory {

    /**
     * Add the given Namespace to our table of available Namespaces
     * 
     * @param n the Namespace to add
     * @return Namespace the namespace already in table (null if Namespace not
     *         previously in table)
     * @exception SecurityException thrown if caller does not have appropriate
     *            NamespacePermission for given namespace
     */
    Namespace addNamespace(Namespace namespace) throws SecurityException;

    /**
     * Check whether table contains given Namespace instance
     * 
     * @param n the Namespace to look for
     * @return true if table does contain given Namespace, false otherwise
     * @exception SecurityException thrown if caller does not have appropriate
     *            NamespacePermission for given namespace
     */
    boolean containsNamespace(Namespace namespace) throws SecurityException;

    /**
     * Get a list of the current Namespace instances exposed by this factory.
     * 
     * @return List<Namespace> of Namespace instances
     * @exception SecurityException thrown if caller does not have appropriate
     *            NamespacePermission for given namespace
     */
    List<Namespace> getNamespaces() throws SecurityException;

    /**
     * Get the given Namespace instance from table
     * 
     * @param n the Namespace to look for
     * @return Namespace
     * @exception SecurityException thrown if caller does not have appropriate
     *            NamespacePermission for given namespace
     */
    Namespace getNamespace(Namespace namespace) throws SecurityException;

    /**
     * Get a Namespace instance by its string name.
     * 
     * @param name the name to use for lookup
     * @return Namespace instance. Null if not found.
     * @exception SecurityException thrown if caller does not have appropriate
     *            NamespacePermission for given namespace
     */
    Namespace getNamespaceByName(String name) throws SecurityException;

    /**
     * Make a GUID using SHA-1 hash algorithm and a default of 16bits of data
     * length. The value is Base64Utils encoded to allow for easy display.
     * 
     * @return new ID instance
     * @throws IDCreateException if ID cannot be constructed
     */
    ID createGUID() throws IDCreateException;

    /**
     * Make a GUID using SHA-1 hash algorithm and a default of 16bits of data
     * length. The value is Base64Utils encoded to allow for easy display.
     * 
     * @param length the byte-length of data used to create a GUID
     * @return new ID instance
     * @throws IDCreateException if ID cannot be constructed
     */
    ID createGUID(int length) throws IDCreateException;

    /**
     * Make a new identity. Given a Namespace, and an array of instance
     * constructor parameters, return a new instance of an ID belonging to the
     * given Namespace
     * 
     * @param n the Namespace to which the ID will belong
     * @param args an Object [] of the parameters for the ID instance
     *        constructor
     * @exception IDCreateException thrown if class for instantiator or instance
     *            can't be loaded, if something goes wrong during instance
     *            construction
     */
    ID createID(Namespace n, Object[] args) throws IDCreateException;

    /**
     * Make a new identity. Given a Namespace name, and an array of instance
     * constructor parameters, return a new instance of an ID belonging to the
     * given Namespace
     * 
     * @param namespaceName the name of the Namespace to which the ID will
     *        belong
     * @param args an Object [] of the parameters for the ID instance
     *        constructor
     * @exception IDCreateException thrown if class for instantiator or ID
     *            instance can't be loaded, if something goes wrong during
     *            instance construction
     */
    ID createID(String namespaceName, Object[] args) throws IDCreateException;

    /**
     * Make a new identity instance from a namespace and String.
     * 
     * @param namespace the namespace to use to create the ID
     * @param uri the String uri to use to create the ID
     * @exception IDCreateException thrown if class for instantiator or ID
     *            instance can't be loaded, if something goes wrong during
     *            instance construction
     */
    ID createID(Namespace namespace, String uri) throws IDCreateException;

    /**
     * Make a new identity instance from a namespaceName and idValue. The
     * namespaceName is first used to lookup the namespace with
     * {@link #getNamespaceByName(String)}, and then the result is passed into
     * {@link #createID(Namespace,String)}.
     * 
     * @param namespaceName the name of the namespace that should be used to
     *        create the ID
     * @param idValue the String value to use to create the ID
     * @exception IDCreateException thrown if class for instantiator or ID
     *            instance can't be loaded, if something goes wrong during
     *            instance construction
     */
    ID createID(String namespaceName, String idValue) throws IDCreateException;

    /**
     * Make a an ID from a String
     * 
     * @param idString the String to use as this ID's unique value. Note: It is
     *        incumbent upon the caller of this method to be sure that the given
     *        string allows the resulting ID to satisfy the ID contract for
     *        global uniqueness within the associated Namespace.
     * 
     * @return valid ID instance
     * @throws IDCreateException thrown if class for instantiator or ID instance
     *         can't be loaded, if something goes wrong during instance
     *         construction
     */
    ID createStringID(String idString) throws IDCreateException;

    /**
     * Make a an ID from a long
     * 
     * @param l the long to use as this ID's unique value. Note: It is incumbent
     *        upon the caller of this method to be sure that the given long
     *        allows the resulting ID to satisfy the ID contract for global
     *        uniqueness within the associated Namespace.
     * 
     * @return valid ID instance
     * @throws IDCreateException thrown if class for instantiator or ID instance
     *         can't be loaded, if something goes wrong during instance
     *         construction
     */
    ID createLongID(long l) throws IDCreateException;

    /**
     * Remove the given Namespace from our table of available Namespaces
     * 
     * @param n the Namespace to remove
     * @return Namespace the namespace already in table (null if Namespace not
     *         previously in table)
     * @exception SecurityException thrown if caller does not have appropriate
     *            NamespacePermission for given namespace
     */
    Namespace removeNamespace(Namespace n) throws SecurityException;
}
