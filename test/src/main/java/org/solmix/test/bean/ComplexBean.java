package org.solmix.test.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ComplexBean implements IComplexBean{

	private String beanName;

	private String country;


	private boolean postProcessed;

	private String name;

	private String sex;

	private int age;

	private boolean jedi;

	protected IComplexBean[] spouses;

	private String touchy;

	private String[] stringArray;

	private Integer[] someIntegerArray;

	private Integer[][] nestedIntegerArray;

	private int[] someIntArray;

	private int[][] nestedIntArray;

	private Date date = new Date();

	private Float myFloat = new Float(0.0);

	private Collection<? super Object> friends = new LinkedList<>();

	private Set<?> someSet = new HashSet<>();

	private Map<?, ?> someMap = new HashMap<>();

	private List<?> someList = new ArrayList<>();

	private Properties someProperties = new Properties();

	private INestedTestBean doctor = new NestedTestBean();

	private INestedTestBean lawyer = new NestedTestBean();


	private boolean destroyed;

	private Number someNumber;


	private Boolean someBoolean;

	private List<?> otherColours;

	private List<?> pets;


	public ComplexBean() {
	}

	public ComplexBean(String name) {
		this.name = name;
	}

	public ComplexBean(IComplexBean spouse) {
		this.spouses = new IComplexBean[] {spouse};
	}

	public ComplexBean(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public ComplexBean(IComplexBean spouse, Properties someProperties) {
		this.spouses = new IComplexBean[] {spouse};
		this.someProperties = someProperties;
	}

	public ComplexBean(List<?> someList) {
		this.someList = someList;
	}

	public ComplexBean(Set<?> someSet) {
		this.someSet = someSet;
	}

	public ComplexBean(Map<?, ?> someMap) {
		this.someMap = someMap;
	}

	public ComplexBean(Properties someProperties) {
		this.someProperties = someProperties;
	}


	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getBeanName() {
		return beanName;
	}


	public void setPostProcessed(boolean postProcessed) {
		this.postProcessed = postProcessed;
	}

	public boolean isPostProcessed() {
		return postProcessed;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
		if (this.name == null) {
			this.name = sex;
		}
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public void setAge(int age) {
		this.age = age;
	}

	public boolean isJedi() {
		return jedi;
	}

	public void setJedi(boolean jedi) {
		this.jedi = jedi;
	}

	
	@Override
	public IComplexBean getSpouse() {
		return (spouses != null ? spouses[0] : null);
	}

	
	@Override
	public void setSpouse(IComplexBean spouse) {
		this.spouses = new IComplexBean[] {spouse};
	}

	
	@Override
	public IComplexBean[] getSpouses() {
		return spouses;
	}

	public String getTouchy() {
		return touchy;
	}

	public void setTouchy(String touchy) throws Exception {
		if (touchy.indexOf('.') != -1) {
			throw new Exception("Can't contain a .");
		}
		if (touchy.indexOf(',') != -1) {
			throw new NumberFormatException("Number format exception: contains a ,");
		}
		this.touchy = touchy;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String[] getStringArray() {
		return stringArray;
	}

	@Override
	public void setStringArray(String[] stringArray) {
		this.stringArray = stringArray;
	}

	@Override
	public Integer[] getSomeIntegerArray() {
		return someIntegerArray;
	}

	@Override
	public void setSomeIntegerArray(Integer[] someIntegerArray) {
		this.someIntegerArray = someIntegerArray;
	}

	@Override
	public Integer[][] getNestedIntegerArray() {
		return nestedIntegerArray;
	}

	@Override
	public void setNestedIntegerArray(Integer[][] nestedIntegerArray) {
		this.nestedIntegerArray = nestedIntegerArray;
	}

	
	@Override
	public int[] getSomeIntArray() {
		return someIntArray;
	}

	
	@Override
	public void setSomeIntArray(int[] someIntArray) {
		this.someIntArray = someIntArray;
	}

	
	@Override
	public int[][] getNestedIntArray() {
		return nestedIntArray;
	}

	
	@Override
	public void setNestedIntArray(int[][] nestedIntArray) {
		this.nestedIntArray = nestedIntArray;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Float getMyFloat() {
		return myFloat;
	}

	public void setMyFloat(Float myFloat) {
		this.myFloat = myFloat;
	}

	public Collection<? super Object> getFriends() {
		return friends;
	}

	public void setFriends(Collection<? super Object> friends) {
		this.friends = friends;
	}

	public Set<?> getSomeSet() {
		return someSet;
	}

	public void setSomeSet(Set<?> someSet) {
		this.someSet = someSet;
	}

	public Map<?, ?> getSomeMap() {
		return someMap;
	}

	public void setSomeMap(Map<?, ?> someMap) {
		this.someMap = someMap;
	}

	public List<?> getSomeList() {
		return someList;
	}

	public void setSomeList(List<?> someList) {
		this.someList = someList;
	}

	public Properties getSomeProperties() {
		return someProperties;
	}

	public void setSomeProperties(Properties someProperties) {
		this.someProperties = someProperties;
	}

	
	@Override
	public INestedTestBean getDoctor() {
		return doctor;
	}

	public void setDoctor(INestedTestBean doctor) {
		this.doctor = doctor;
	}

	
	@Override
	public INestedTestBean getLawyer() {
		return lawyer;
	}

	public void setLawyer(INestedTestBean lawyer) {
		this.lawyer = lawyer;
	}

	public Number getSomeNumber() {
		return someNumber;
	}

	public void setSomeNumber(Number someNumber) {
		this.someNumber = someNumber;
	}

	public Boolean getSomeBoolean() {
		return someBoolean;
	}

	public void setSomeBoolean(Boolean someBoolean) {
		this.someBoolean = someBoolean;
	}

	public List<?> getOtherColours() {
		return otherColours;
	}

	public void setOtherColours(List<?> otherColours) {
		this.otherColours = otherColours;
	}

	public List<?> getPets() {
		return pets;
	}

	public void setPets(List<?> pets) {
		this.pets = pets;
	}


	/**
	 * @see org.IComplexBean.tests.sample.beans.ITestBean#exceptional(Throwable)
	 */
	
	@Override
	public void exceptional(Throwable t) throws Throwable {
		if (t != null) {
			throw t;
		}
	}

	
	@Override
	public void unreliableFileOperation() throws IOException {
		throw new IOException();
	}
	/**
	 * @see org.IComplexBean.tests.sample.beans.ITestBean#returnsThis()
	 */
	
	@Override
	public Object returnsThis() {
		return this;
	}

	public void absquatulate() {
	}

	
	@Override
	public int haveBirthday() {
		return age++;
	}


	public void destroy() {
		this.destroyed = true;
	}

	public boolean wasDestroyed() {
		return destroyed;
	}


	
	@Override
	public int hashCode() {
		return this.age;
	}

	
	public int compareTo(Object other) {
		if (this.name != null && other instanceof ComplexBean) {
			return this.name.compareTo(((ComplexBean) other).getName());
		}
		else {
			return 1;
		}
	}

	
	@Override
	public String toString() {
		return this.name;
	}
}
