package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class NON_ADMISSION_SUBJECT extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "NON_ADMISSION_EXAM_ID", referencedColumnName = "ID", nullable = false)})
	private NON_ADMISSION_EXAM nonAdmissionExam;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID", nullable = false)})
	private SUBJECT subject;

	public NON_ADMISSION_SUBJECT() {
	}

	public NON_ADMISSION_EXAM getNonAdmissionExam() {
		return nonAdmissionExam;
	}

	public void setNonAdmissionExam(NON_ADMISSION_EXAM nonAdmissionExam) {
		this.nonAdmissionExam = nonAdmissionExam;
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}
}
