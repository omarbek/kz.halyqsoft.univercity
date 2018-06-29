package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EDUCATION_MODULE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created 15.06.2018
 */
@Entity
public class CURRICULUM_AFTER_SEMESTER extends AbstractEntity {

	private static final long serialVersionUID = -1694890284738737494L;

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
			@JoinColumn(name = "EDUCATION_MODULE_TYPE_ID", referencedColumnName = "ID")})
	private EDUCATION_MODULE_TYPE educationModuleType;

	@Column(name = "CODE")
	private String code;

	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

	@Column(name = "DELETED", nullable = false)
    private boolean deleted;

	public CURRICULUM getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(CURRICULUM curriculum) {
		this.curriculum = curriculum;
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

	public EDUCATION_MODULE_TYPE getEducationModuleType() {
		return educationModuleType;
	}

	public void setEducationModuleType(EDUCATION_MODULE_TYPE educationModuleType) {
		this.educationModuleType = educationModuleType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SEMESTER getSemester() {
		return semester;
	}

	public void setSemester(SEMESTER semester) {
		this.semester = semester;
	}
}
