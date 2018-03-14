package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.UNIVERSITY;
import org.r3a.common.entity.AbstractEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @@author Omarbek
 * @created Jan 14, 2016 5:19:11 PM
 */
@Entity
public class V_KBTU_ENTRANTS extends AbstractEntity {

    private static final long serialVersionUID = -6041884473983223402L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "UNIVERSITY_ID", referencedColumnName = "ID")})
    private UNIVERSITY university;
	
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;
	
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID")})
    private LANGUAGE language;
	
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "CREATED_YEAR")
	private Integer createdYear;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FACULTY_ID", referencedColumnName = "ID")})
    private DEPARTMENT faculty;
	
	public V_KBTU_ENTRANTS() {
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

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getCreatedYear() {
		return createdYear;
	}

	public void setCreatedYear(Integer createdYear) {
		this.createdYear = createdYear;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
	}

	public DEPARTMENT getFaculty() {
		return faculty;
	}

	public void setFaculty(DEPARTMENT faculty) {
		this.faculty = faculty;
	}
}
