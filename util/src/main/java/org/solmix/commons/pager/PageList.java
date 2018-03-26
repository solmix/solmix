/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.pager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A utility class that contains all a "page" of data that is viewable <br>
 * this list may or may not contain the entire list of information. generally
 * this list contains a subset of data.
 * 
 */
public class PageList<T> extends ArrayList<T> implements Serializable {

	private static final long serialVersionUID = -9015051022202954954L;
	private int totalSize = 0;
	private boolean isUnbounded; // Is the total size of the list known?
	private Serializable metaData;

	public PageList(int size) {
	    super(size);
	    this.isUnbounded = false;
	}
	public PageList() {
		super();
		this.isUnbounded = false;
	}

	public PageList(Collection<T> c, int totalSize) {
		super(c);
		this.totalSize = totalSize;
		this.isUnbounded = false;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer("{").append("totalSize=")
				.append(totalSize).append(" ").append("}");
		return super.toString() + s.toString();

	}

	/**
	 * returns the total size of the "masterlist" that this page is a subset of.
	 * 
	 * @return Value of property listSize.
	 */
	public int getTotalSize() {
		return Math.max(this.size(), this.totalSize);
	}

	/**
	 * Sets the total size of the "masterlist" that this page is a subset of.
	 * 
	 * @param totalSize
	 *            New value of property totalSize.
	 *
	 */
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public void setMetaData(Serializable metaData) {
		this.metaData = metaData;
	}

	public Serializable getMetaData() {
		return this.metaData;
	}

	public boolean isUnbounded() {
		return this.isUnbounded;
	}

	public void setUnbounded(boolean isUnbounded) {
		this.isUnbounded = isUnbounded;
	}
}
