package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import org.r3a.common.entity.AbstractEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ELECTIVE_SUBJECT extends SUBJECT {

	private static final long serialVersionUID = 4490375778025449496L;

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

	@Column(name = "CREATED")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "UPDATED")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;


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


	public SEMESTER getSemester() {
		return semester;
	}

	public void setSemester(SEMESTER semester) {
		this.semester = semester;
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

}

