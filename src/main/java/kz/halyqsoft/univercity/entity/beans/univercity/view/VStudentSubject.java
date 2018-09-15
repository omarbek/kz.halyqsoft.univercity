package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

/**
 * @author Dinassil Omarbek
 * @created 14.09.2018
 */
public class VStudentSubject extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT)
	private String code;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String moduleType;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String subjectName;

	@FieldInfo(type=EFieldType.INTEGER,order = 4)
	private Integer credit;

	@FieldInfo(type=EFieldType.INTEGER,order = 5)
	private Integer ects;

	@FieldInfo(type=EFieldType.TEXT,order = 6)
	private String semester;

	@FieldInfo(type = EFieldType.TEXT, order = 7)
	private String tutor;

	@FieldInfo(type = EFieldType.TEXT, order = 8)
	private String examType;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public Integer getEcts() {
		return ects;
	}

	public void setEcts(Integer ects) {
		this.ects = ects;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getTutor() {
		return tutor;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}
}
