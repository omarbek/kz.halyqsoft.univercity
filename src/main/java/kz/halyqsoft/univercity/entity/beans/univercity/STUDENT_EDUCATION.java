package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_EDUCATION_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Dec 21, 2015 10:58:55 AM
 */
@Entity
public class STUDENT_EDUCATION extends AbstractEntity {

	private static final long serialVersionUID = 2009779400229209500L;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FACULTY_ID", referencedColumnName = "ID")})
    private DEPARTMENT faculty;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;


	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "GROUPS_ID", referencedColumnName = "ID")})
	private GROUPS groups;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, readOnlyFixed = true)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 7)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID")})
    private LANGUAGE language;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 8)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EDUCATION_TYPE_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION_TYPE educationType;
	
	@FieldInfo(type = EFieldType.DATE, order = 9, required = false)
	@Column(name = "ENTRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 10, required = false)
	@Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 11)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_STATUS_ID", referencedColumnName = "ID")})
    private STUDENT_STATUS status;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 12, required = false, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHILD_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION child;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 13, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 14, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "studentEducation")
	private Set<STUDENT_SCHEDULE> studentSchedules;

	public STUDENT_EDUCATION() {
		studentSchedules = new HashSet<>();
	}

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public DEPARTMENT getFaculty() {
		return faculty;
	}

	public void setFaculty(DEPARTMENT faculty) {
		this.faculty = faculty;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
	}

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
	}
	
	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public STUDENT_EDUCATION_TYPE getEducationType() {
		return educationType;
	}

	public void setEducationType(STUDENT_EDUCATION_TYPE educationType) {
		this.educationType = educationType;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public STUDENT_STATUS getStatus() {
		return status;
	}

	public void setStatus(STUDENT_STATUS status) {
		this.status = status;
	}

	public STUDENT_EDUCATION getChild() {
		return child;
	}

	public void setChild(STUDENT_EDUCATION child) {
		this.child = child;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Set<STUDENT_SCHEDULE> getStudentSchedules() {
		return studentSchedules;
	}

	public void setStudentSchedules(Set<STUDENT_SCHEDULE> studentSchedules) {
		this.studentSchedules = studentSchedules;
	}
}
