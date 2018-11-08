package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import kz.halyqsoft.univercity.entity.beans.univercity.PAIR_SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Feb 27, 2018 10:46:53 AM
 */
@Entity
public class SUBJECT_REQUISITE extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PAIR_SUBJECT_ID", referencedColumnName = "ID")})
    private PAIR_SUBJECT pairSubject;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 2, required = false)
    @Column(name = "PRE_REQUISITE", nullable = false)
    private boolean preRequisite;
    
	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public PAIR_SUBJECT getPairSubject() {
		return pairSubject;
	}

	public void setPairSubject(PAIR_SUBJECT pairSubject) {
		this.pairSubject = pairSubject;
	}

	public boolean isPreRequisite() {
		return preRequisite;
	}

	public void setPreRequisite(boolean preRequisite) {
		this.preRequisite = preRequisite;
	}

}
