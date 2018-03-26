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
 * @created Jun 28, 2016 5:23:18 PM
 */
@Entity
public class EXAM_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 560524914964946146L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EXAM_ID", referencedColumnName = "ID")})
    private EXAM exam;
	
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
    
	@FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = true, inView = false, inGrid = false)
	@Column(name = "ALPHA_GRADE")
	private String alphaGrade;
	
    @FieldInfo(type = EFieldType.DOUBLE , min = 0, max = 99, order = 6)
	@Column(name = "DIGIT_GRADE")
	private Double digitalGrade;
    
    @FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = true, inView = false, inGrid = false)
	@Column(name = "COMMENTS")
	private String comments;
    
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, inEdit = true, order = 7)
	@Column(name = "USE_GPA", nullable = false)
    private boolean useGpa;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 11, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = true, inView = false, inGrid = false)
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "OLD_ID")
	private String oldId;
    
	public EXAM_DETAIL() {
	}

	public EXAM getExam() {
		return exam;
	}

	public void setExam(EXAM exam) {
		this.exam = exam;
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

	public String getAlphaGrade() {
		return alphaGrade;
	}

	public void setAlphaGrade(String alphaGrade) {
		this.alphaGrade = alphaGrade;
	}

	public Double getDigitalGrade() {
		return digitalGrade;
	}

	public void setDigitalGrade(Double digitalGrade) {
		this.digitalGrade = digitalGrade;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isUseGpa() {
		return useGpa;
	}

	public void setUseGpa(boolean useGpa) {
		this.useGpa = useGpa;
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
