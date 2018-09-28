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

@Entity
public class ATTESTATION_DETAIL extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ATTESTATION_ID", referencedColumnName = "ID")})
    private ATTESTATION attestation;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_EDUCATION_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
	private EMPLOYEE employee;

	@FieldInfo(type = EFieldType.DOUBLE, min = 0, order = 4)
	@Column(name = "GRADE", nullable = false)
	private Double grade;

    @FieldInfo(type = EFieldType.DATETIME, order = 5)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

	@FieldInfo(type = EFieldType.DATETIME, order = 6)
	@Column(name = "CREATED")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();
	
	public ATTESTATION_DETAIL() {
	}

	public ATTESTATION getAttestation() {
		return attestation;
	}

	public void setAttestation(ATTESTATION attestation) {
		this.attestation = attestation;
	}

	public STUDENT_EDUCATION getStudentEducation() {
		return studentEducation;
	}

	public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
		this.studentEducation = studentEducation;
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
