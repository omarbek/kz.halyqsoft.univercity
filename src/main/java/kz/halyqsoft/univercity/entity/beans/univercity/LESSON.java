package kz.halyqsoft.univercity.entity.beans.univercity;

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
 * @created Jun 15, 2016 11:44:06 AM
 */
@Entity
public class LESSON extends AbstractEntity {

	private static final long serialVersionUID = -624426864961074660L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_DETAIL_ID", referencedColumnName = "ID")})
    private SCHEDULE_DETAIL scheduleDetail;
	
	@FieldInfo(type = EFieldType.DATE, order = 2)
	@Column(name = "LESSON_DATE")
    @Temporal(TemporalType.DATE)
    private Date lessonDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 3)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 4)
	@Column(name = "FINISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 5)
	@Column(name = "CANCELED", nullable = false)
    private boolean canceled;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, order = 6)
	@Column(name = "CANCEL_REASON")
	private String cancelReason;
	
	public LESSON() {
	}

	public SCHEDULE_DETAIL getScheduleDetail() {
		return scheduleDetail;
	}

	public void setScheduleDetail(SCHEDULE_DETAIL scheduleDetail) {
		this.scheduleDetail = scheduleDetail;
	}

	public Date getLessonDate() {
		return lessonDate;
	}

	public void setLessonDate(Date lessonDate) {
		this.lessonDate = lessonDate;
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

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
}
