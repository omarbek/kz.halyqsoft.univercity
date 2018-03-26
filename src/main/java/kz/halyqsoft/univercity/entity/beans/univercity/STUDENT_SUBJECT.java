package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
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
 * @created 05 ���. 2016 �. 15:11:29
 */
@Entity
public class STUDENT_SUBJECT extends AbstractEntity {
	
	private static final long serialVersionUID = 8844000279620928025L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID")})
    private SEMESTER_DATA semesterData;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SEMESTER_SUBJECT subject;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 4, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "REG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public STUDENT_SUBJECT() {
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public STUDENT_EDUCATION getStudentEducation() {
		return studentEducation;
	}

	public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
		this.studentEducation = studentEducation;
	}

	public SEMESTER_SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SEMESTER_SUBJECT subject) {
		this.subject = subject;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
