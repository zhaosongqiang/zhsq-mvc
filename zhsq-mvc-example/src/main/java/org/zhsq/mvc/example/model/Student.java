/**
 * 
 */
package org.zhsq.mvc.example.model;

import java.util.Date;

/**
 * @author zhsq 
 * @date 2018年5月19日
 * @since 1.0
 */
public class Student {

	private String name;
	
	private boolean b;
	
	private Boolean bs;
	
	private int i;
	
	private Integer is;
	
	private float f;
	
	private Float fs;
	
	private double d;
	
	private Double ds;
	
	private Teacher teacher;
	
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
	 * @return the b
	 */
	public boolean isB() {
		return b;
	}

	/**
	 * @param b the b to set
	 */
	public void setB(boolean b) {
		this.b = b;
	}

	/**
	 * @return the bs
	 */
	public Boolean getBs() {
		return bs;
	}

	/**
	 * @param bs the bs to set
	 */
	public void setBs(Boolean bs) {
		this.bs = bs;
	}

	/**
	 * @return the i
	 */
	public int getI() {
		return i;
	}

	/**
	 * @param i the i to set
	 */
	public void setI(int i) {
		this.i = i;
	}

	/**
	 * @return the is
	 */
	public Integer getIs() {
		return is;
	}

	/**
	 * @param is the is to set
	 */
	public void setIs(Integer is) {
		this.is = is;
	}

	/**
	 * @return the f
	 */
	public float getF() {
		return f;
	}

	/**
	 * @param f the f to set
	 */
	public void setF(float f) {
		this.f = f;
	}

	/**
	 * @return the fs
	 */
	public Float getFs() {
		return fs;
	}

	/**
	 * @param fs the fs to set
	 */
	public void setFs(Float fs) {
		this.fs = fs;
	}

	/**
	 * @return the d
	 */
	public double getD() {
		return d;
	}

	/**
	 * @param d the d to set
	 */
	public void setD(double d) {
		this.d = d;
	}

	/**
	 * @return the ds
	 */
	public Double getDs() {
		return ds;
	}

	/**
	 * @param ds the ds to set
	 */
	public void setDs(Double ds) {
		this.ds = ds;
	}

	/**
	 * @return the teacher
	 */
	public Teacher getTeacher() {
		return teacher;
	}

	/**
	 * @param teacher the teacher to set
	 */
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	@Override
	public String toString() {
		return "Student [name=" + name + ", b=" + b + ", bs=" + bs + ", i=" + i + ", is=" + is + ", f=" + f + ", fs="
				+ fs + ", d=" + d + ", ds=" + ds + ", teacher=" + teacher + "]";
	}

}
