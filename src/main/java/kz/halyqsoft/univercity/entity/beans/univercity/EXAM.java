package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * @created Jun 28, 2016 5:12:29 PM
 */
@Entity
public class EXAM extends AbstractEntity {

	private static final long serialVersionUID = 2450357512021076821L;

	@FieldInfo(type = EFieldType.DATE, order = 1)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 2)
	@Column(name = "FINISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 3, required = false, inGrid = false, readOnlyFixed = true)
	@Column(name = "LOCKED", nullable = false)
    private boolean locked;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, required = false, inGrid = false, readOnlyFixed = true)
	@Column(name = "LOCKED_BY", nullable = false)
    private String lockedBy;
	
	public EXAM() {
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
