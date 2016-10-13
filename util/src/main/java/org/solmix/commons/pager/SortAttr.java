package org.solmix.commons.pager;

public class SortAttr {

	private String columnName;
	// SQL默认为升序
	private Boolean desc;

	private SortAttr(String column, Boolean sort) {
		this.columnName = column;
		this.desc = sort;
	}

	/**
	 * 按column升序
	 * @param column
	 * @return
	 */
	public static SortAttr buildAsc(String column) {
		return new SortAttr(column, false);
	}
	
	/**
	 * 按column降序
	 * @param column
	 * @return
	 */
	public static SortAttr buildDesc(String column) {
		return new SortAttr(column, true);
	}
	/**
	 * 默认排序
	 * @param column
	 * @return
	 */
	public static SortAttr build(String column) {
		return new SortAttr(column, null);
	}
	
	public String toSql(){
		StringBuilder sb= new StringBuilder().append(columnName);
		if(desc!=null){
			sb.append("  ").append(desc?"DESC":"ASC");
		}
		return sb.toString();
	}
	@Override
	public String toString(){
		return toSql();
	}
}
