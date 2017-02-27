package org.solmix.test.bean;

import java.io.IOException;

public interface IComplexBean {
	String getName();

	void setName(String name);

	IComplexBean getSpouse();

	void setSpouse(IComplexBean spouse);

	IComplexBean[] getSpouses();

	String[] getStringArray();

	void setStringArray(String[] stringArray);

	Integer[][] getNestedIntegerArray();

	Integer[] getSomeIntegerArray();

	void setSomeIntegerArray(Integer[] someIntegerArray);

	void setNestedIntegerArray(Integer[][] nestedIntegerArray);

	int[] getSomeIntArray();

	void setSomeIntArray(int[] someIntArray);

	int[][] getNestedIntArray();

	void setNestedIntArray(int[][] someNestedArray);

	/**
	 * Throws a given (non-null) exception.
	 */
	void exceptional(Throwable t) throws Throwable;

	Object returnsThis();

	INestedTestBean getDoctor();

	INestedTestBean getLawyer();


	/**
	 * Increment the age by one.
	 * @return the previous age
	 */
	int haveBirthday();

	void unreliableFileOperation() throws IOException;
}
