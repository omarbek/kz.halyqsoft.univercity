package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.UNIVERSITY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 24, 2015 11:46:38 AM
 */
@Entity
public class V_ENTRANT_SPECIALITY extends AbstractEntity {
	
	private static final long serialVersionUID = -2118148193832114157L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "UNIVERSITY_ID", referencedColumnName = "ID")})
    private UNIVERSITY university;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 3, inEdit = false, inView = false)
	@Column(name = "UNIVERSITY_NAME", nullable = false)
	private String universityName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private V_SPECIALITY speciality;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 5, inEdit = false, inView = false, columnWidth = 300)
	@Column(name = "SPECIALITY_NAME", nullable = false)
	private String specialityName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID")})
    private LANGUAGE language;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 7, inGrid = false, inEdit = false, inView = false, columnWidth = 150)
	@Column(name = "LANGUAGE_NAME", nullable = false)
	private String languageName;

	public V_ENTRANT_SPECIALITY() {
	}

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public UNIVERSITY getUniversity() {
		return university;
	}

	public void setUniversity(UNIVERSITY university) {
		this.university = university;
	}

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	public V_SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(V_SPECIALITY speciality) {
		this.speciality = speciality;
	}

	public String getSpecialityName() {
		return specialityName;
	}

	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
}
