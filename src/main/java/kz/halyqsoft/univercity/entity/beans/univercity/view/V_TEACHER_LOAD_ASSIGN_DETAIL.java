package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Omarbek
 * @created Mar 24, 2016 11:43:21 AM
 */
@Entity
public class V_TEACHER_LOAD_ASSIGN_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -6088012426860605550L;

	@FieldInfo(type = EFieldType.TEXT, max = 10, order = 8)
	@Column(name = "SUBJECT_NAME")
	private String subjectName;
	
	@Transient
	private String speciality;
	
	@Transient
	private String language;
	
	@Transient
	private String studyYear;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 6, order = 11)
	@Column(name = "CREDIT")
	private Integer credit;
	
	@FieldInfo(type = EFieldType.TEXT, max = 10, order = 8)
	@Column(name = "FORMULA")
	private String formula;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 15)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
	private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.TEXT, order = 16)
	@Column(name = "SEMESTER_PERIOD_NAME")
	private String semesterPeriodName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 17)
	@Column(name = "STUDENT_COUNT")
	private Integer studentCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 18)
	@Column(name = "LC_COUNT")
	private Integer lcCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 19)
	@Column(name = "LB_COUNT")
	private Integer lbCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 20)
	@Column(name = "PR_COUNT")
	private Integer prCount;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 21)
	@Column(name = "LC_HOUR")
	private Double lcHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 22)
	@Column(name = "LC_HOUR_TOTAL")
	private Double lcHourTotal;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 23)
	@Column(name = "LB_HOUR")
	private Double lbHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 24)
	@Column(name = "LB_HOUR_TOTAL")
	private Double lbHourTotal;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 25)
	@Column(name = "PR_HOUR")
	private Double prHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 26)
	@Column(name = "PR_HOUR_TOTAL")
	private Double prHourTotal;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 27)
	@Column(name = "TOTAL_HOUR")
	private Double totalHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 28)
	@Column(name = "TOTAL_CREDIT")
	private Double totalCredit;
	
	public V_TEACHER_LOAD_ASSIGN_DETAIL() {
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(String studyYear) {
		this.studyYear = studyYear;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public String getSemesterPeriodName() {
		return semesterPeriodName;
	}

	public void setSemesterPeriodName(String semesterPeriodName) {
		this.semesterPeriodName = semesterPeriodName;
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
