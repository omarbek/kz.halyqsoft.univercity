package kz.halyqsoft.univercity.entity.beans.univercity.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

/**
 * @author Omarbek
 * @created Mar 28, 2017 11:55:40 AM
 */
public final class VStudent extends AbstractEntity {

	private static final long serialVersionUID = -1387856179967027804L;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String code;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String fio;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 4)
	private String category;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private String status;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private String faculty;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 7)
	private String specialty;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 8)
	private String lockReason;

	@JsonIgnore
	@FieldInfo(type = EFieldType.INTEGER, order = 9, inGrid = false)
	private int studyYear;

	@JsonIgnore
	@FieldInfo(type = EFieldType.TEXT, order = 10, inGrid = false)
	private String languageName;
	
	public VStudent() {
	}

	@JsonIgnore
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getLockReason() {
		return lockReason;
	}

	public void setLockReason(String lockReason) {
		this.lockReason = lockReason;
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
}
