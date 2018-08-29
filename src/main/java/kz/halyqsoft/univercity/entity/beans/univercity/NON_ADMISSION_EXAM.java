package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.NON_ADMISSION_CAUSE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class NON_ADMISSION_EXAM extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false)})
	private USERS student;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "NON_ADMISSION_CAUSE_ID", referencedColumnName = "ID", nullable = false)})
	private NON_ADMISSION_CAUSE nonAdmissionCause;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID", nullable = false)})
	private SEMESTER_DATA semesterData;

	@FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order = 4)
	@Column(name = "created" )
	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();

	@Override
	public String toString() {
		return  student+" " + nonAdmissionCause +
				" " + semesterData +
				" " + CommonUtils.getFormattedDate(created);
	}

	public NON_ADMISSION_EXAM() {
	}

	public USERS getStudent() {
		return student;
	}

	public void setStudent(USERS student) {
		this.student = student;
	}

	public NON_ADMISSION_CAUSE getNonAdmissionCause() {
		return nonAdmissionCause;
	}

	public void setNonAdmissionCause(NON_ADMISSION_CAUSE nonAdmissionCause) {
		this.nonAdmissionCause = nonAdmissionCause;
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
