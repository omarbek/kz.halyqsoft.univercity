package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
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
    private String dormStatus;
    private CARD card;
    private STUDENT_DIPLOMA_TYPE studentDiplomaType;
    private GROUPS group;

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

    public String getDormStatus() {
        return dormStatus;
    }

    public void setDormStatus(String dormStatus) {
        this.dormStatus = dormStatus;
    }

    public CARD getCard() {
        return card;
    }

    public void setCard(CARD card) {
        this.card = card;
    }

    public STUDENT_DIPLOMA_TYPE getStudentDiplomaType() {
        return studentDiplomaType;
    }

    public void setStudentDiplomaType(STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        this.studentDiplomaType = studentDiplomaType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public GROUPS getGroup() {
        return group;
    }

    public void setGroup(GROUPS group) {
        this.group = group;
    }

    @Override
    public boolean hasFilter() {
        return !(code == null && firstname == null && lastname == null && studentStatus == null
                && lockReason == null && faculty == null && speciality == null && studyYear == null
                && educationType == null && dormStatus == null && card == null && studentDiplomaType == null
        && group == null);
    }
}
