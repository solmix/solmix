package org.solmix.runtime.proxy.support;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.solmix.commons.util.Assert;


public class MethodMatcherHelper {
	
	public static MethodMatcher union(MethodMatcher mm1, MethodMatcher mm2) {
		return new UnionMethodMatcher(mm1, mm2);
	}


	/**
	 * Match all methods that <i>both</i> of the given MethodMatcherHelper match.
	 * @param mm1 the first MethodMatcher
	 * @param mm2 the second MethodMatcher
	 * @return a distinct MethodMatcher that matches all methods that both
	 * of the given MethodMatcherHelper match
	 */
	public static MethodMatcher intersection(MethodMatcher mm1, MethodMatcher mm2) {
		return new IntersectionMethodMatcher(mm1, mm2);
	}

	/**
	 * Apply the given MethodMatcher to the given Method, supporting an
	 * (if applicable).
	 * @param mm the MethodMatcher to apply (may be an IntroductionAwareMethodMatcher)
	 * @param method the candidate method
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the candidate class must be taken to be the method's declaring class)
	 * @param hasIntroductions {@code true} if the object on whose behalf we are
	 * asking is the subject on one or more introductions; {@code false} otherwise
	 * @return whether or not this method matches statically
	 */
	public static boolean matches(MethodMatcher mm, Method method, Class<?> targetClass, boolean hasIntroductions) {
		Assert.isNotNull(mm, "MethodMatcher must not be null");
		return mm.matches(method, targetClass);
	}


	/**
	 * MethodMatcher implementation for a union of two given MethodMatcherHelper.
	 */
	@SuppressWarnings("serial")
	private static class UnionMethodMatcher implements MethodMatcher, Serializable {

		private final MethodMatcher mm1;

		private final MethodMatcher mm2;

		public UnionMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
			Assert.isNotNull(mm1, "First MethodMatcher must not be null");
			Assert.isNotNull(mm2, "Second MethodMatcher must not be null");
			this.mm1 = mm1;
			this.mm2 = mm2;
		}

		@Override
		public boolean matches(Method method, Class<?> targetClass) {
			return (matchesClass1(targetClass) && this.mm1.matches(method, targetClass)) ||
					(matchesClass2(targetClass) && this.mm2.matches(method, targetClass));
		}

		protected boolean matchesClass1(Class<?> targetClass) {
			return true;
		}

		protected boolean matchesClass2(Class<?> targetClass) {
			return true;
		}

		@Override
		public boolean isRuntime() {
			return this.mm1.isRuntime() || this.mm2.isRuntime();
		}

		@Override
		public boolean matches(Method method, Class<?> targetClass, Object[] args) {
			return this.mm1.matches(method, targetClass, args) || this.mm2.matches(method, targetClass, args);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof UnionMethodMatcher)) {
				return false;
			}
			UnionMethodMatcher that = (UnionMethodMatcher) obj;
			return (this.mm1.equals(that.mm1) && this.mm2.equals(that.mm2));
		}

		@Override
		public int hashCode() {
			int hashCode = 17;
			hashCode = 37 * hashCode + this.mm1.hashCode();
			hashCode = 37 * hashCode + this.mm2.hashCode();
			return hashCode;
		}
	}


	/**
	 * MethodMatcher implementation for an intersection of two given MethodMatcherHelper.
	 */
	@SuppressWarnings("serial")
	private static class IntersectionMethodMatcher implements MethodMatcher, Serializable {

		private final MethodMatcher mm1;

		private final MethodMatcher mm2;

		public IntersectionMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
			Assert.isNotNull(mm1, "First MethodMatcher must not be null");
			Assert.isNotNull(mm2, "Second MethodMatcher must not be null");
			this.mm1 = mm1;
			this.mm2 = mm2;
		}

		@Override
		public boolean matches(Method method, Class<?> targetClass) {
			return this.mm1.matches(method, targetClass) && this.mm2.matches(method, targetClass);
		}

		@Override
		public boolean isRuntime() {
			return this.mm1.isRuntime() || this.mm2.isRuntime();
		}

		@Override
		public boolean matches(Method method, Class<?> targetClass, Object[] args) {
			// Because a dynamic intersection may be composed of a static and dynamic part,
			// we must avoid calling the 3-arg matches method on a dynamic matcher, as
			// it will probably be an unsupported operation.
			boolean aMatches = this.mm1.isRuntime() ?
					this.mm1.matches(method, targetClass, args) : this.mm1.matches(method, targetClass);
			boolean bMatches = this.mm2.isRuntime() ?
					this.mm2.matches(method, targetClass, args) : this.mm2.matches(method, targetClass);
			return aMatches && bMatches;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof IntersectionMethodMatcher)) {
				return false;
			}
			IntersectionMethodMatcher that = (IntersectionMethodMatcher) other;
			return (this.mm1.equals(that.mm1) && this.mm2.equals(that.mm2));
		}

		@Override
		public int hashCode() {
			int hashCode = 17;
			hashCode = 37 * hashCode + this.mm1.hashCode();
			hashCode = 37 * hashCode + this.mm2.hashCode();
			return hashCode;
		}
	}
}
