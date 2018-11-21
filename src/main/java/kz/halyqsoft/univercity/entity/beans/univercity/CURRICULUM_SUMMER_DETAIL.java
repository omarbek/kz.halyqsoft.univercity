package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Apr 19, 2018 11:36:03 AM
 */
@Entity
public class CURRICULUM_SUMMER_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 5872068668333214691L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID", nullable = false)})
    private CURRICULUM_SUMMER curriculum;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID", nullable = false)})
    private SUBJECT subject;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_CYCLE_ID", referencedColumnName = "ID")})
    private SUBJECT_CYCLE subjectCycle;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public CURRICULUM_SUMMER_DETAIL() {
	}

	public CURRICULUM_SUMMER getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(CURRICULUM_SUMMER curriculum) {
		this.curriculum = curriculum;
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
