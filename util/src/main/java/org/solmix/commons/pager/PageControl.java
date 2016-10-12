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
import java.util.Arrays;

import org.solmix.commons.util.ArrayUtils;

/**
 * A utility class to wrap up all the paging/sorting options that are frequently
 * used with finders and other methods that return lists of things.
 */
public class PageControl implements Serializable, Cloneable {

	private static final long serialVersionUID = 2663301176921992628L;

	public static final int SIZE_UNLIMITED = -1;
	public static final int DEFAULT_SORT=0;
	public enum SORT{
		UNSORTED,ASC,DESC
	}

	public static final String[] SQL_SORTS = { "", "ASC", "DESC" };

	public static final PageControl PAGE_ALL = new PageControl(0,
			SIZE_UNLIMITED, true);
	public static final PageControl PAGE_NONE = new PageControl(0, 0, true);
	public static final PageControl PAGE_MIN = new PageControl(0, 1, true);

	private int pageNum = 0;
	private int pageSize = SIZE_UNLIMITED;
	private int totalSize = SIZE_UNLIMITED;
	private SORT sortOrder = SORT.UNSORTED;
	private String[] sortAttribute;
	private boolean immutable = false;
	// Meta-data that PageLists have returned
	private Serializable metaData;

	private PageControl(int pagenum, int pagesize, boolean immutable) {
		this(pagenum, pagesize);
		this.immutable = immutable;
	}

	public PageControl() {
	}
	
	public static PageControl fromRows(int startRow,int endRow){
		int pageSize = endRow-startRow;
		int pageNum=pageSize>=0? startRow/pageSize:0;
		return new PageControl(pageNum, pageSize);
	}

	public PageControl(int pagenum, int pagesize) {
		this.pageNum = pagenum;
		this.pageSize = pagesize;
	}

	public PageControl(int pagenum, int pagesize, SORT sortorder,
			String[] sortattribute) {
		this.pageNum = pagenum;
		this.pageSize = pagesize;
		this.sortOrder = sortorder;
		this.sortAttribute = sortattribute;
	}

	public PageControl(PageControl pc) {
		pageNum = pc.getPageNum();
		pageSize = pc.getPageSize();
		sortOrder = pc.getSortOrder();
		sortAttribute = pc.getSortAttribute();
		metaData = pc.getMetaData();
	}

	public boolean isAscending() {
		return sortOrder == SORT.ASC;
	}

	public boolean isDescending() {
		return sortOrder == SORT.DESC;
	}

	/**
	 * sets the initial defaults for the PageControl. Sort attribute specifies
	 * which attribute to sort on.
	 * 
	 * @param pc
	 * @param defaultSortAttr
	 *            specifies the attribute to sort on.
	 * @return PageControl
	 */
	public static PageControl initDefaults(PageControl pc, String[] defaultSortAttr) {
		if (pc == null) {
			pc = new PageControl();
		} else {
			pc = (PageControl) pc.clone();
		}

		
		if (pc.getSortOrder() == SORT.UNSORTED) {
			pc.setSortOrder(SORT.ASC);
		}
		return pc;
	}

	/** @return The current page number (0-based) */
	public int getPageNum() {
		return pageNum;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	/**
	 * @param pagenum
	 *            Set the current page number to <code>pageNum</code>
	 */
	public void setPageNum(int pageNum) {
		if (immutable)
			throw new IllegalStateException("immutable object");
		this.pageNum = pageNum;
	}

	/** @return The current page size */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pagesize
	 *            Set the current page size to this value
	 */
	public void setPageSize(int pagesize) {
		if (immutable)
			throw new IllegalStateException("immutable object");
		this.pageSize = pagesize;
	}

	public SORT getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SORT sortOrder) {
		if (immutable)
			throw new IllegalStateException("immutable object");
		this.sortOrder = sortOrder;
	}

	public String[] getSortAttribute() {
		return sortAttribute;
	}

	/**
	 * @param attr
	 *            Set the attribute that the sort is based on.
	 */
	public void setSortAttribute(String[] attr) {
		if (immutable)
			throw new IllegalStateException("immutable object");
		sortAttribute = attr;
	}

	public Serializable getMetaData() {
		return this.metaData;
	}

	public void setMetaData(Serializable metaData) {
		if (immutable)
			throw new IllegalStateException("immutable object");
		this.metaData = metaData;
	}

	/**
	 * Get the index of the first item on the page as dictated by the page size
	 * and page number.
	 */
	public int getPageFirstIndex() {
		return pageNum * pageSize;
	}

	@Override
	public String toString() {

		// shortcuts for common cases
		if (this == PageControl.PAGE_ALL)
			return "{ALL}";
		if (this == PageControl.PAGE_NONE)
			return "{NONE}";
		if (this.equals(PageControl.PAGE_ALL))
			return "{ALL}";
		if (this.equals(PageControl.PAGE_NONE))
			return "{NONE}";

		StringBuffer s = new StringBuffer("{");
		s.append("pn=" + pageNum + " ");
		s.append("ps=" + pageSize + " ");

		s.append("so=");

		switch (sortOrder) {
		case ASC:
			s.append("asc ");
			break;
		case DESC:
			s.append("desc");
			break;
		case UNSORTED:
			s.append("unsorted ");
			break;
		default:
			s.append(' ');
		}

		s.append("sa=" + sortAttribute + " ");
		s.append("}");
		return s.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PageControl) {
			PageControl pc = (PageControl) o;
			return pageNum == pc.getPageNum() && pageSize == pc.getPageSize()
					&& sortOrder == pc.getSortOrder()
					&& sortAttribute == pc.getSortAttribute();
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashcode= (37 * pageNum) + (37 * pageSize) +sortOrder.hashCode();
		if(sortAttribute!=null&&sortAttribute.length>0){
			for(String attr:sortAttribute){
				hashcode=hashcode+attr.hashCode();
			}
		}
		return hashcode;
	}

	@Override
	public Object clone() {
		PageControl res = new PageControl(pageNum, pageSize, sortOrder,sortAttribute);
		res.metaData = metaData;
		return res;
	}
}
