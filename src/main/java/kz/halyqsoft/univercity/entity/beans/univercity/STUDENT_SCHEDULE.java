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
 * @created 5 ��� 2016 �. 15:11:29
 */

@Entity
public class STUDENT_SCHEDULE extends AbstractEntity {
	
	private static final long serialVersionUID = -4773756118809728203L;

	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_DETAIL_ID", referencedColumnName = "ID")})
    private SCHEDULE_DETAIL scheduleDetail;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 3, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "VIRTUAL_STUDENT", nullable = false)
    private boolean virtualStudent;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 4, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 5, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	public STUDENT_SCHEDULE() {
	}

	public STUDENT_EDUCATION getStudentEducation() {
		return studentEducation;
	}

	public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
		this.studentEducation = studentEducation;
	}

	public SCHEDULE_DETAIL getScheduleDetail() {
		return scheduleDetail;
	}

	public void setScheduleDetail(SCHEDULE_DETAIL scheduleDetail) {
		this.scheduleDetail = scheduleDetail;
	}

	public boolean isVirtualStudent() {
		return virtualStudent;
	}

	public void setVirtualStudent(boolean virtualStudent) {
		this.virtualStudent = virtualStudent;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
