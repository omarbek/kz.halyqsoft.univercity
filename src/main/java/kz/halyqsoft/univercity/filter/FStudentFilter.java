package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LOCK_REASON;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_EDUCATION_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Mar 27, 2017 4:54:32 PM
 */
public final class FStudentFilter extends AbstractFilterBean {

	private static final long serialVersionUID = 5338142998400760891L;
	
	private String code;
	private String firstname;
	private String lastname;
	private STUDENT_STATUS studentStatus;
	private LOCK_REASON lockReason;
	private DEPARTMENT faculty;
	private SPECIALITY speciality;
	private STUDY_YEAR studyYear;
	private STUDENT_EDUCATION_TYPE educationType;
	
	public FStudentFilter() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public STUDENT_STATUS getStudentStatus() {
		return studentStatus;
	}

	public void setStudentStatus(STUDENT_STATUS studentStatus) {
		this.studentStatus = studentStatus;
	}

	public LOCK_REASON getLockReason() {
		return lockReason;
	}

	public void setLockReason(LOCK_REASON lockReason) {
		this.lockReason = lockReason;
	}

	public DEPARTMENT getFaculty() {
		return faculty;
	}

	public void setFaculty(DEPARTMENT faculty) {
		this.faculty = faculty;
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

	public STUDENT_EDUCATION_TYPE getEducationType() {
		return educationType;
	}

	public void setEducationType(STUDENT_EDUCATION_TYPE educationType) {
		this.educationType = educationType;
	}

	@Override
	public boolean hasFilter() {
		return (!(code == null && firstname == null && lastname == null && studentStatus == null && lockReason == null && faculty == null && speciality == null && studyYear == null && educationType == null));
	}
}
