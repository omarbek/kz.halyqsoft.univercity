package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Feb 18, 2016 10:01:59 AM
 */
@Entity
public class CURRICULUM_DETAIL extends AbstractEntity {

    private static final long serialVersionUID = -4720228895492227308L;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID", nullable = false)})
    private SEMESTER_DATA semesterData;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_CYCLE_ID", referencedColumnName = "ID")})
    private SUBJECT_CYCLE subjectCycle;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "EDUCATION_MODULE_TYPE_ID", referencedColumnName = "ID")})
    private EDUCATION_MODULE_TYPE educationModuleType;

    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "RECOMMENDED_SEMESTER")
    private String recommendedSemester;

    @Column(name = "CONSIDER_CREDIT", nullable = false)
    private boolean considerCredit;

    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public CURRICULUM_DETAIL() {
    }

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }

    public SEMESTER_DATA getSemesterData() {
        return semesterData;
    }

    public void setSemesterData(SEMESTER_DATA semesterData) {
        this.semesterData = semesterData;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public SUBJECT_CYCLE getSubjectCycle() {
        return subjectCycle;
    }

    public void setSubjectCycle(SUBJECT_CYCLE subjectCycle) {
        this.subjectCycle = subjectCycle;
    }

    public String getRecommendedSemester() {
        return recommendedSemester;
    }

    public void setRecommendedSemester(String recommendedSemester) {
        this.recommendedSemester = recommendedSemester;
    }

    public boolean isConsiderCredit() {
        return considerCredit;
    }

    public void setConsiderCredit(boolean considerCredit) {
        this.considerCredit = considerCredit;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EDUCATION_MODULE_TYPE getEducationModuleType() {
        return educationModuleType;
    }

    public void setEducationModuleType(EDUCATION_MODULE_TYPE educationModuleType) {
        this.educationModuleType = educationModuleType;
    }
}
