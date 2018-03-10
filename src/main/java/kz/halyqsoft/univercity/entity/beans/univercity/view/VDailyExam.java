package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Dinassil Omarbek
 * @created Apr 19, 2017 12:27:36 PM
 */
public class VDailyExam extends AbstractEntity {

	private static final long serialVersionUID = -2348574322994827920L;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String date;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String room;

	@FieldInfo(type = EFieldType.INTEGER, order = 4)
	private Integer contingent;

	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private String subject;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private String faculty;

	@FieldInfo(type = EFieldType.TEXT, order = 7)
	private String specialty;

	@FieldInfo(type = EFieldType.TEXT, order = 8)
	private String examiner;

	@FieldInfo(type = EFieldType.TEXT, order = 9)
	private String cathedra;

	@FieldInfo(type = EFieldType.TEXT, order = 10)
	private String proctor;

	public VDailyExam() {
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Integer getContingent() {
		return contingent;
	}

	public void setContingent(Integer contingent) {
		this.contingent = contingent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getExaminer() {
		return examiner;
	}

	public void setExaminer(String examiner) {
		this.examiner = examiner;
	}

	public String getCathedra() {
		return cathedra;
	}

	public void setCathedra(String cathedra) {
		this.cathedra = cathedra;
	}

	public String getProctor() {
		return proctor;
	}

	public void setProctor(String proctor) {
		this.proctor = proctor;
	}

}
