package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Feb 27, 2017 10:46:53 AM
 */
@Entity
public class SUBJECT_REQUISITE extends AbstractEntity {

    private static final long serialVersionUID = 3345556205305957038L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "REQUISITE_ID", referencedColumnName = "ID")})
    private SUBJECT requisite;
	
    @Column(name = "PRE_REQUISITE", nullable = false)
    private boolean preRequisite;
    
	public SUBJECT_REQUISITE() {
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public SUBJECT getRequisite() {
		return requisite;
	}

	public void setRequisite(SUBJECT requisite) {
		this.requisite = requisite;
	}

	public boolean isPreRequisite() {
		return preRequisite;
	}

	public void setPreRequisite(boolean preRequisite) {
		this.preRequisite = preRequisite;
	}
}
