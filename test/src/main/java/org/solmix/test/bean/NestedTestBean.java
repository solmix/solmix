package org.solmix.test.bean;

public class NestedTestBean implements INestedTestBean {
	private String someString = "";

	public NestedTestBean() {
	}

	public NestedTestBean(String someString) {
		setSomeString(someString);
	}

	public void setSomeString(String someString) {
		this.someString = (someString != null ? someString : "");
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NestedTestBean)) {
			return false;
		}
		NestedTestBean ntb = (NestedTestBean) obj;
		return this.someString.equals(ntb.someString);
	}

	@Override
	public int hashCode() {
		return this.someString.hashCode();
	}

	@Override
	public String toString() {
		return "NestedTestBean: " + this.someString;
	}

	@Override
	public String getSomeString() {
		return someString;
	}

}
