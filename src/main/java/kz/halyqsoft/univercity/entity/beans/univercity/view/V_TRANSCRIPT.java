package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
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
 * @author Kairat A. Gatiyatov
 * @created 04 ���� 2016 �. 15:11:29
 */
@Entity
public class V_TRANSCRIPT extends AbstractEntity {

	private static final long serialVersionUID = 4106005634713902468L;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = false, inView = false, inGrid = false)
	@Column(name = "LAST_NAME")
	private String secondName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, inEdit = false, inView = false, inGrid = false)
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5, required = false, inEdit = false, inView = false, inGrid = false)
	@Column(name = "MIDDLE_NAME")
	private String middleName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, inEdit = false, inView = false, inGrid = false)
	@Column(name = "LAST_NAME_EN")
	private String secondNameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7, inEdit = false, inView = false, inGrid = false)
	@Column(name = "FIRST_NAME_EN")
	private String firstNameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, required = false, inEdit = false, inView = false, inGrid = false)
	@Column(name = "MIDDLE_NAME_EN")
	private String middleNameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 9, inEdit = false, inView = false, inGrid = false)
	@Column(name = "CODE_STUDENT")
	private String studentCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 10, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.TEXT, order = 11, required = false, inEdit = false, inView = false, inGrid = false)
	@Column(name = "PERIOD_NAME")
	private String periodName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 12, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR year;
	
	@FieldInfo(type = EFieldType.TEXT, order = 13, required = false, inEdit = false, inView = false, inGrid = false)
	@Column(name = "ENTRANCE_YEAR")
	private String entranceYear;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 14, inGrid = false, inEdit = false, columnWidth = 130)
	@Column(name = "BEGIN_YEAR", nullable = false)
    private Integer beginYear;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 15, inGrid = false,inEdit = false, columnWidth = 130)
	@Column(name = "END_YEAR", nullable = false)
    private Integer endYear;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 16, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
	
	@FieldInfo(type = EFieldType.TEXT, order = 17, inEdit = false, inView = false, inGrid = true)
	@Column(name = "CODE")
	private String code;
	
	@FieldInfo(type = EFieldType.TEXT, order = 18, inEdit = false, inView = false, inGrid = true)
	@Column(name = "NAME_RU")
	private String nameRu;
	
	@FieldInfo(type = EFieldType.TEXT, order = 19, required = false, inEdit = false, inView = false, inGrid = false)
	@Column(name = "NAME_EN")
	private String nameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 20, required = false, inEdit = false, inView = false, inGrid = false)
	@Column(name = "NAME_KZ")
	private String nameKz;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 21, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;

	@FieldInfo(type = EFieldType.INTEGER, order = 22, inEdit = false, columnWidth = 130)
	@Column(name = "CREDIT", nullable = false)
    private Integer credit;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 23, inEdit = false, columnWidth = 130)
	@Column(name = "GRADE", nullable = false)
    private Integer grade;
	
	@FieldInfo(type = EFieldType.TEXT, order = 24, columnWidth = 130)
	@Column(name = "ALPHA_GRADE")
	private String alphaGrade;
	
    @FieldInfo(type = EFieldType.DOUBLE, order = 25, inEdit = false, columnWidth = 130)
	@Column(name = "DIGIT_GRADE", nullable = false)
	private Double digitalGrade;
    
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 26)
	@Column(name = "USE_GPA", nullable = false)
    private boolean useGpa;
	
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

	public boolean isUseGpa() {
		return useGpa;
	}

	public void setUseGpa(boolean useGpa) {
		this.useGpa = useGpa;
	}

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSecondNameEn() {
		return secondNameEn;
	}

	public void setSecondNameEn(String secondNameEn) {
		this.secondNameEn = secondNameEn;
	}

	public String getFirstNameEn() {
		return firstNameEn;
	}

	public void setFirstNameEn(String firstNameEn) {
		this.firstNameEn = firstNameEn;
	}

	public String getMiddleNameEn() {
		return middleNameEn;
	}

	public void setMiddleNameEn(String middleNameEn) {
		this.middleNameEn = middleNameEn;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	public ENTRANCE_YEAR getYear() {
		return year;
	}

	public void setYear(ENTRANCE_YEAR year) {
		this.year = year;
	}

	public String getEntranceYear() {
		return entranceYear;
	}

	public void setEntranceYear(String entranceYear) {
		this.entranceYear = entranceYear;
	}

	public Integer getBeginYear() {
		return beginYear;
	}

	public void setBeginYear(Integer beginYear) {
		this.beginYear = beginYear;
	}

	public Integer getEndYear() {
		return endYear;
	}

	public void setEndYear(Integer endYear) {
		this.endYear = endYear;
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNameRu() {
		return nameRu;
	}

	public void setNameRu(String nameRu) {
		this.nameRu = nameRu;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameKz() {
		return nameKz;
	}

	public void setNameKz(String nameKz) {
		this.nameKz = nameKz;
	}

	public CREDITABILITY getCreditability() {
		return creditability;
	}

	public void setCreditability(CREDITABILITY creditability) {
		this.creditability = creditability;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public STUDENT getStudent() {
		return student;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
