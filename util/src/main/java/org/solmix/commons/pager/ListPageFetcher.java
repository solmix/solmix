package org.solmix.commons.pager;

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


	public ListPageFetcher(List<T> data) {
		super();
		this.data = data;
	}

	@Override
	public PageList<T> getPage(PageControl control) {
		PageList<T> res = new PageList<T> ();
		int startIdx, curIdx, endIdx;

		if (this.data.size() == 0) {
			return new PageList<T> ();
		}

//		this.ensureSortOrder(control);
		res.setTotalSize(this.data.size());

		startIdx = clamp(control.getPageFirstIndex(), 0, this.data.size() - 1);
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
