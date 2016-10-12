package org.solmix.commons.pager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * A PageFetcher which works with a pre-fetched list as the data backing the
 * fetcher.
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ListPageFetcher<T> extends PageFetcher<T> {

	private List data;

	private int sortOrder;

	public ListPageFetcher(List<T> data) {
		super();
		this.data = data;
		this.sortOrder = PageControl.SORT_UNSORTED;
	}

	@Override
	public PageList<T> getPage(PageControl control) {
		PageList<T> res = new PageList<T> ();
		int startIdx, curIdx, endIdx;

		if (this.data.size() == 0) {
			return new PageList<T> ();
		}

		this.ensureSortOrder(control);
		res.setTotalSize(this.data.size());

		startIdx = clamp(control.getPageEntityIndex(), 0, this.data.size() - 1);
		curIdx = startIdx;

		if (control.getPageSize() == PageControl.SIZE_UNLIMITED) {
			endIdx = this.data.size();
		} else {
			endIdx = clamp(startIdx + control.getPageSize(), startIdx,
					this.data.size());
		}

		for (ListIterator<T>  i = this.data.listIterator(startIdx); i.hasNext()
				&& curIdx < endIdx; curIdx++) {
			res.add(i.next());
		}
		return res;
	}

	private class DescSorter implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			return -((Comparable) o1).compareTo(o2);
		}

		@Override
		public boolean equals(Object other) {
			return false;
		}
	}

	
	private void ensureSortOrder(PageControl control) {
		if (control.getSortOrder() == this.sortOrder)
			return;

		this.sortOrder = control.getSortOrder();
		if (this.sortOrder == PageControl.SORT_UNSORTED) {
			return;
		} else if (this.sortOrder == PageControl.SORT_ASC) {
			Collections.sort(data);
		} else if (this.sortOrder == PageControl.SORT_DESC) {
			Collections.sort(data, new DescSorter());
		} else {
			throw new IllegalStateException("Unknown control sorting type: "
					+ this.sortOrder);
		}
	}

	public static long clamp(long val, long min, long max) {
		if (val < min)
			return min;

		if (val > max)
			return max;

		return val;
	}

	/**
	 * Clamp a value to a range. If the passed value is less than the minimum,
	 * return the minimum. If it is greater than the maximum, assign the
	 * maximum. else return the passed value.
	 */
	public static int clamp(int val, int min, int max) {
		return (int) clamp((long) val, (long) min, (long) max);
	}
}
