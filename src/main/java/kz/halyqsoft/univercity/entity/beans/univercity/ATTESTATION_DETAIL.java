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
 * @created Sep 28, 2016 3:18:50 PM
 */
@Entity
public class ATTESTATION_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -8155763510599218976L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ATTESTATION_ID", referencedColumnName = "ID")})
    private ATTESTATION attestation;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;
    
	@FieldInfo(type = EFieldType.INTEGER, min = 1, max = 3, order = 4)
	@Column(name = "ATTENDANCE_MARK", nullable = false)
    private Integer attendanceMark;
	
	@FieldInfo(type = EFieldType.DOUBLE, min = 0, max = 99, order = 4)
	@Column(name = "GRADE", nullable = false)
	private Double grade;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, inEdit = true, order = 7)
	@Column(name = "USE_GPA", nullable = false)
    private boolean useGpa;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "COMMENTS")
	private String comments;
    
    @FieldInfo(type = EFieldType.DATETIME, order = 6)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    
    @FieldInfo(type = EFieldType.TEXT, max = 32, order = 7)
	@Column(name = "UPDATED_BY")
	private String updatedBy;
    
    @Column(name = "OLD_ID")
	private String oldId;
	
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

	public Integer getAttendanceMark() {
		return attendanceMark;
	}

	public void setAttendanceMark(Integer attendanceMark) {
		this.attendanceMark = attendanceMark;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public boolean isUseGpa() {
		return useGpa;
	}

	public void setUseGpa(boolean useGpa) {
		this.useGpa = useGpa;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}
