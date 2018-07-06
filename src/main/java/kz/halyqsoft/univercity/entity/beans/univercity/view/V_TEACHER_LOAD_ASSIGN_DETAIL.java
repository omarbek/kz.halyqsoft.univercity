package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
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
import javax.persistence.Transient;

/**
 * @author Omarbek
 * @created Mar 24, 2016 11:43:21 AM
 */
@Entity
public class V_TEACHER_LOAD_ASSIGN_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -6088012426860605550L;

	@FieldInfo(type = EFieldType.FK_DIALOG, inGrid = false, columnWidth = 80)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
	private SUBJECT subject;

	@FieldInfo(type = EFieldType.TEXT, max = 10,order = 2)
	@Column(name = "SUBJECT_NAME")
	private String subjectName;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 3, inGrid = false, columnWidth = 80)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "STREAM_ID", referencedColumnName = "ID")})
	private STREAM stream;

	@FieldInfo(type = EFieldType.TEXT, order = 4)
	@Column(name = "STREAM_NAME")
	private String streamName;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 5, inGrid = false, columnWidth = 80)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
	private SEMESTER semester;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	@Column(name = "SEMESTER_NAME")
	private String semesterName;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 7)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
	private SEMESTER_PERIOD semesterPeriod;

	@FieldInfo(type = EFieldType.TEXT, order = 8)
	@Column(name = "SEMESTER_PERIOD_NAME")
	private String semesterPeriodName;

	@FieldInfo(type = EFieldType.INTEGER, order = 9)
	@Column(name = "STUDENT_COUNT")
	private Integer studentCount;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 6, order = 10)
	@Column(name = "CREDIT")
	private Integer credit;
	
	@FieldInfo(type = EFieldType.TEXT, max = 10, order = 11)
	@Column(name = "FORMULA")
	private String formula;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 12)
	@Column(name = "LC_HOUR")
	private Double lcHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 13)
	@Column(name = "LB_HOUR")
	private Double lbHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 14)
	@Column(name = "PR_HOUR")
	private Double prHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 15)
	@Column(name = "WITH_TEACHER_HOUR")
	private Double withTeacherHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 16)
	@Column(name = "RATING_HOUR")
	private Double ratingHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 17)
	@Column(name = "EXAMHOUR")
	private Double examHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 18)
	@Column(name = "CONTROL_HOUR")
	private Double controlHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 19)
	@Column(name = "COURSE_WORK_HOUR")
	private Double courseWorkHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 20)
	@Column(name = "DIPLOMA_HOUR")
	private Double diplomaHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 21)
	@Column(name = "PRACTICE_HOUR")
	private Double practiceHour;

	@FieldInfo(type = EFieldType.DOUBLE, order = 22)
	@Column(name = "MEK")
	private Double mek;

	@FieldInfo(type = EFieldType.DOUBLE, order = 23)
	@Column(name = "PROTECT_DIPLOMA_HOUR")
	private Double protectDiplomaHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 24)
	@Column(name = "TOTAL_HOUR")
	private Double totalHour;

	@Transient
	private String speciality;

	@Transient
	private String language;

	@Transient
	private String studyYear;
	
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

	public Double getLcHour() {
		return lcHour;
	}

	public void setLcHour(Double lcHour) {
		this.lcHour = lcHour;
	}

	public Double getLbHour() {
		return lbHour;
	}

	public void setLbHour(Double lbHour) {
		this.lbHour = lbHour;
	}

	public Double getPrHour() {
		return prHour;
	}

	public void setPrHour(Double prHour) {
		this.prHour = prHour;
	}

	public Double getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(Double totalHour) {
		this.totalHour = totalHour;
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public STREAM getStream() {
		return stream;
	}

	public void setStream(STREAM stream) {
		this.stream = stream;
	}

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public SEMESTER getSemester() {
		return semester;
	}

	public void setSemester(SEMESTER semester) {
		this.semester = semester;
	}

	public String getSemesterName() {
		return semesterName;
	}

	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}

	public Double getWithTeacherHour() {
		return withTeacherHour;
	}

	public void setWithTeacherHour(Double withTeacherHour) {
		this.withTeacherHour = withTeacherHour;
	}

	public Double getRatingHour() {
		return ratingHour;
	}

	public void setRatingHour(Double ratingHour) {
		this.ratingHour = ratingHour;
	}

	public Double getExamHour() {
		return examHour;
	}

	public void setExamHour(Double examHour) {
		this.examHour = examHour;
	}

	public Double getControlHour() {
		return controlHour;
	}

	public void setControlHour(Double controlHour) {
		this.controlHour = controlHour;
	}

	public Double getCourseWorkHour() {
		return courseWorkHour;
	}

	public void setCourseWorkHour(Double courseWorkHour) {
		this.courseWorkHour = courseWorkHour;
	}

	public Double getDiplomaHour() {
		return diplomaHour;
	}

	public void setDiplomaHour(Double diplomaHour) {
		this.diplomaHour = diplomaHour;
	}

	public Double getPracticeHour() {
		return practiceHour;
	}

	public void setPracticeHour(Double practiceHour) {
		this.practiceHour = practiceHour;
	}

	public Double getMek() {
		return mek;
	}

	public void setMek(Double mek) {
		this.mek = mek;
	}

	public Double getProtectDiplomaHour() {
		return protectDiplomaHour;
	}

	public void setProtectDiplomaHour(Double protectDiplomaHour) {
		this.protectDiplomaHour = protectDiplomaHour;
	}
}
