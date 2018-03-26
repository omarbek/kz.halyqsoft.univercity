package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Mar 24, 2016 10:59:00 AM
 */
@Entity
public class TEACHER_LOAD_ASSIGN_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -1179682577968099408L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_LOAD_ASSIGN_ID", referencedColumnName = "ID")})
    private TEACHER_LOAD_ASSIGN teacherLoadAssign;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 4)
	@Column(name = "LC_CREDIT")
	private Integer lcCredit;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5)
	@Column(name = "LB_CREDIT")
	private Integer lbCredit;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 6)
	@Column(name = "PR_CREDIT")
	private Integer prCredit;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 7)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
	private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 9)
	@Column(name = "STUDENT_COUNT")
	private Integer studentCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 10)
	@Column(name = "LC_COUNT")
	private Integer lcCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 11)
	@Column(name = "LB_COUNT")
	private Integer lbCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 12)
	@Column(name = "PR_COUNT")
	private Integer prCount;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 13)
	@Column(name = "LC_HOUR")
	private Double lcHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 14)
	@Column(name = "LC_HOUR_TOTAL")
	private Double lcHourTotal;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 15)
	@Column(name = "LB_HOUR")
	private Double lbHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 16)
	@Column(name = "LB_HOUR_TOTAL")
	private Double lbHourTotal;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 17)
	@Column(name = "PR_HOUR")
	private Double prHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 18)
	@Column(name = "PR_HOUR_TOTAL")
	private Double prHourTotal;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 19)
	@Column(name = "TOTAL_HOUR")
	private Double totalHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 20)
	@Column(name = "TOTAL_CREDIT")
	private Double totalCredit;
	
	public TEACHER_LOAD_ASSIGN_DETAIL() {
	}

	public TEACHER_LOAD_ASSIGN getTeacherLoadAssign() {
		return teacherLoadAssign;
	}

	public void setTeacherLoadAssign(TEACHER_LOAD_ASSIGN teacherLoadAssign) {
		this.teacherLoadAssign = teacherLoadAssign;
	}

	public EMPLOYEE getTeacher() {
		return teacher;
	}

	public void setTeacher(EMPLOYEE teacher) {
		this.teacher = teacher;
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public Integer getLcCredit() {
		return lcCredit;
	}

	public void setLcCredit(Integer lcCredit) {
		this.lcCredit = lcCredit;
	}

	public Integer getLbCredit() {
		return lbCredit;
	}

	public void setLbCredit(Integer lbCredit) {
		this.lbCredit = lbCredit;
	}

	public Integer getPrCredit() {
		return prCredit;
	}

	public void setPrCredit(Integer prCredit) {
		this.prCredit = prCredit;
	}
	
	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public Integer getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Integer studentCount) {
		this.studentCount = studentCount;
	}

	public Integer getLcCount() {
		return lcCount;
	}

	public void setLcCount(Integer lcCount) {
		this.lcCount = lcCount;
	}

	public Integer getLbCount() {
		return lbCount;
	}

	public void setLbCount(Integer lbCount) {
		this.lbCount = lbCount;
	}

	public Integer getPrCount() {
		return prCount;
	}

	public void setPrCount(Integer prCount) {
		this.prCount = prCount;
	}

	public Double getLcHour() {
		return lcHour;
	}

	public void setLcHour(Double lcHour) {
		this.lcHour = lcHour;
	}

	public Double getLcHourTotal() {
		return lcHourTotal;
	}

	public void setLcHourTotal(Double lcHourTotal) {
		this.lcHourTotal = lcHourTotal;
	}

	public Double getLbHour() {
		return lbHour;
	}

	public void setLbHour(Double lbHour) {
		this.lbHour = lbHour;
	}

	public Double getLbHourTotal() {
		return lbHourTotal;
	}

	public void setLbHourTotal(Double lbHourTotal) {
		this.lbHourTotal = lbHourTotal;
	}

	public Double getPrHour() {
		return prHour;
	}

	public void setPrHour(Double prHour) {
		this.prHour = prHour;
	}

	public Double getPrHourTotal() {
		return prHourTotal;
	}

	public void setPrHourTotal(Double prHourTotal) {
		this.prHourTotal = prHourTotal;
	}

	public Double getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(Double totalHour) {
		this.totalHour = totalHour;
	}

	public Double getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(Double totalCredit) {
		this.totalCredit = totalCredit;
	}
}
