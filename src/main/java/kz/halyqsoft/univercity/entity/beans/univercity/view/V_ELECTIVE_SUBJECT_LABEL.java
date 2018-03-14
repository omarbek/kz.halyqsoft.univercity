package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Feb 24, 2016 3:44:11 PM
 */
@Entity
public class V_ELECTIVE_SUBJECT_LABEL extends AbstractEntity {

	private static final long serialVersionUID = 2295638253876147733L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1, fieldWidth = 500)
	@Column(name = "NAME_KZ", nullable = false)
	private String nameKZ;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 64, order = 2, fieldWidth = 500)
	@Column(name = "NAME_EN", nullable = false)
	private String nameEN;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, fieldWidth = 500)
	@Column(name = "NAME_RU", nullable = false)
	private String nameRU;
	
	@FieldInfo(type = EFieldType.TEXT, max = 13, order = 4, fieldWidth = 500, required = false, readOnlyFixed = true)
	@Column(name = "CODE", nullable = false)
	private String code;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 5, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_DIRECT_ID", referencedColumnName = "ID")})
    private STUDY_DIRECT studyDirect;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, required = false, order = 6, inGrid = false, fieldWidth = 500)
	@Column(name = "DESCR")
	private String descr;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 8, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 9, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 10, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_CYCLE_ID", referencedColumnName = "ID")})
    private SUBJECT_CYCLE subjectCycle;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 14, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 15, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ACADEMIC_FORMULA_ID", referencedColumnName = "ID")})
    private ACADEMIC_FORMULA academicFormula;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 16, required = false, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUP_LEC_ID", referencedColumnName = "ID")})
    private GROUP_SIZE_LECTURE groupSizeLecture;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 17, required = false, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUP_PRAC_ID", referencedColumnName = "ID")})
    private GROUP_SIZE_PRAC groupSizePrac;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 18, required = false, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUP_LAB_ID", referencedColumnName = "ID")})
    private GROUP_SIZE_LAB groupSizeLab;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 19, inGrid = false, fieldWidth = 500)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CONTROL_TYPE_ID", referencedColumnName = "ID")})
    private CONTROL_TYPE controlType;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 21, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 24, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "ELECTIVE", nullable = false)
    private boolean elective;
	
	public V_ELECTIVE_SUBJECT_LABEL() {
	}

	public String getNameKZ() {
		return nameKZ;
	}

	public void setNameKZ(String nameKZ) {
		this.nameKZ = nameKZ;
	}

	public String getNameEN() {
		return nameEN;
	}

	public void setNameEN(String nameEN) {
		this.nameEN = nameEN;
	}

	public String getNameRU() {
		return nameRU;
	}

	public void setNameRU(String nameRU) {
		this.nameRU = nameRU;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public STUDY_DIRECT getStudyDirect() {
		return studyDirect;
	}

	public void setStudyDirect(STUDY_DIRECT studyDirect) {
		this.studyDirect = studyDirect;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
	}

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public SUBJECT_CYCLE getSubjectCycle() {
		return subjectCycle;
	}

	public void setSubjectCycle(SUBJECT_CYCLE subjectCycle) {
		this.subjectCycle = subjectCycle;
	}

	public CREDITABILITY getCreditability() {
		return creditability;
	}

	public void setCreditability(CREDITABILITY creditability) {
		this.creditability = creditability;
	}

	public ACADEMIC_FORMULA getAcademicFormula() {
		return academicFormula;
	}

	public void setAcademicFormula(ACADEMIC_FORMULA academicFormula) {
		this.academicFormula = academicFormula;
	}

	public GROUP_SIZE_LECTURE getGroupSizeLecture() {
		return groupSizeLecture;
	}

	public void setGroupSizeLecture(GROUP_SIZE_LECTURE groupSizeLecture) {
		this.groupSizeLecture = groupSizeLecture;
	}

	public GROUP_SIZE_PRAC getGroupSizePrac() {
		return groupSizePrac;
	}

	public void setGroupSizePrac(GROUP_SIZE_PRAC groupSizePrac) {
		this.groupSizePrac = groupSizePrac;
	}

	public GROUP_SIZE_LAB getGroupSizeLab() {
		return groupSizeLab;
	}

	public void setGroupSizeLab(GROUP_SIZE_LAB groupSizeLab) {
		this.groupSizeLab = groupSizeLab;
	}

	public CONTROL_TYPE getControlType() {
		return controlType;
	}

	public void setControlType(CONTROL_TYPE controlType) {
		this.controlType = controlType;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isElective() {
		return elective;
	}

	public void setElective(boolean elective) {
		this.elective = elective;
	}

	@Override
	public String toString() {
		return nameRU;
	}
}
