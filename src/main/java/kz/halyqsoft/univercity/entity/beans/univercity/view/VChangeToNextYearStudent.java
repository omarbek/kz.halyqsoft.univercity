package kz.halyqsoft.univercity.entity.beans.univercity.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;


public final class VChangeToNextYearStudent extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT, order = 2 , inView = false, inGrid = false)
	private String code;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String fio;

	@FieldInfo(type = EFieldType.TEXT, order = 4)
	private String groupName;

	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private String status;

	@FieldInfo(type = EFieldType.TEXT, order = 6, inGrid = false, inView = false)
	private String facultyName;

	@FieldInfo(type = EFieldType.TEXT, order = 7)
	private String speciality;

	@FieldInfo(type = EFieldType.INTEGER, order = 8)
	private int studyYear;

	@FieldInfo(type = EFieldType.TEXT, order = 9)
	private String languageName;

	public VChangeToNextYearStudent() {
	}

	@Override
	public ID getId() {
		return super.getId();
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public int getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(int studyYear) {
		this.studyYear = studyYear;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
