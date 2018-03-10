package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 6, 2017 3:49:55 PM
 */
public final class VEmployee extends AbstractEntity {

	private static final long serialVersionUID = -5652828598742599696L;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String code;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String fio;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4)
	private String deptName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private String postName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 6, inGrid = false)
	private boolean lecture;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 7, inGrid = false)
	private boolean laboratory;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 8, inGrid = false)
	private boolean practice;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 9, inGrid = false)
	private boolean fall;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 10, inGrid = false)
	private boolean spring;
	
	public VEmployee() {
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public boolean isLecture() {
		return lecture;
	}

	public void setLecture(boolean lecture) {
		this.lecture = lecture;
	}

	public boolean isLaboratory() {
		return laboratory;
	}

	public void setLaboratory(boolean laboratory) {
		this.laboratory = laboratory;
	}

	public boolean isPractice() {
		return practice;
	}

	public void setPractice(boolean practice) {
		this.practice = practice;
	}

	public boolean isFall() {
		return fall;
	}

	public void setFall(boolean fall) {
		this.fall = fall;
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}
}
