package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FChairFilter extends AbstractFilterBean {

    private DEPARTMENT chair;
    private STUDENT_DIPLOMA_TYPE studentDiplomaType;
    private STUDY_YEAR studyYear;
    private SEMESTER_PERIOD semesterPeriod;

    public DEPARTMENT getChair() {
        return chair;
    }

    public void setChair(DEPARTMENT chair) {
        this.chair = chair;
    }

    public STUDENT_DIPLOMA_TYPE getStudentDiplomaType() {
        return studentDiplomaType;
    }

    public void setStudentDiplomaType(STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        this.studentDiplomaType = studentDiplomaType;
    }

    public STUDY_YEAR getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(STUDY_YEAR studyYear) {
        this.studyYear = studyYear;
    }

    public SEMESTER_PERIOD getSemesterPeriod() {
        return semesterPeriod;
    }

    public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
        this.semesterPeriod = semesterPeriod;
    }

    @Override
    public boolean hasFilter() {
        return !(chair == null && studentDiplomaType == null && studyYear == null);
    }
}
