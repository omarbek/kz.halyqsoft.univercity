package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ATTESTATION_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * @created Sep 28, 2016 3:12:10 PM
 */
@Entity
public class ATTESTATION extends AbstractEntity {

	private static final long serialVersionUID = 5436195498756128045L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_DETAIL_ID", referencedColumnName = "ID")})
    private SCHEDULE_DETAIL scheduleDetail;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ATTESTATION_TYPE_ID", referencedColumnName = "ID")})
    private ATTESTATION_TYPE attestationType;
	
	@FieldInfo(type = EFieldType.DATE, order = 3)
	@Column(name = "ATTESTATION_DATE")
    @Temporal(TemporalType.DATE)
    private Date attestationDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 4)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 5)
	@Column(name = "FINISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false, inGrid = false, readOnlyFixed = true)
	@Column(name = "LOCKED", nullable = false)
    private boolean locked;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7, required = false, inGrid = false, readOnlyFixed = true)
	@Column(name = "LOCKED_BY", nullable = false)
    private String lockedBy;
	
	public ATTESTATION() {
	}

	public SCHEDULE_DETAIL getScheduleDetail() {
		return scheduleDetail;
	}

	public void setScheduleDetail(SCHEDULE_DETAIL scheduleDetail) {
		this.scheduleDetail = scheduleDetail;
	}

	public ATTESTATION_TYPE getAttestationType() {
		return attestationType;
	}

	public void setAttestationType(ATTESTATION_TYPE attestationType) {
		this.attestationType = attestationType;
	}

	public Date getAttestationDate() {
		return attestationDate;
	}

	public void setAttestationDate(Date attestationDate) {
		this.attestationDate = attestationDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}
}
