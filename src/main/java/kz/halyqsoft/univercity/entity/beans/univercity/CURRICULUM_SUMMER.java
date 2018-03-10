package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CURRICULUM_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
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
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 19, 2017 11:09:55 AM
 */
@Entity
public class CURRICULUM_SUMMER extends AbstractEntity {

	private static final long serialVersionUID = -8351188759691523669L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID", nullable = false)})
    private SEMESTER_DATA semesterData;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STATUS_ID", referencedColumnName = "ID", nullable = false)})
    private CURRICULUM_STATUS curriculumStatus;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public CURRICULUM_SUMMER() {
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public CURRICULUM_STATUS getCurriculumStatus() {
		return curriculumStatus;
	}

	public void setCurriculumStatus(CURRICULUM_STATUS curriculumStatus) {
		this.curriculumStatus = curriculumStatus;
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
}
