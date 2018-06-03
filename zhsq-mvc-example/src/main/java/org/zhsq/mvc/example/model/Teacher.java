/**
 * 
 */
package org.zhsq.mvc.example.model;

/**
 * @author zhsq 
 * @date 2018年5月19日
 * @since 1.0
 */
public class Teacher {
	
	private String name;
	
	private int age;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Teacher [name=" + name + ", age=" + age + "]";
	}
	
}
