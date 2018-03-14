package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_DIRECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
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
 * @created Oct 8, 2016 4:13:55 PM
 */
@Entity
public class V_SUBJECT_SELECT extends AbstractEntity {

	private static final long serialVersionUID = -1442440970696524875L;

	@FieldInfo(type = EFieldType.TEXT, order = 1, columnWidth = 100)
	@Column(name = "CODE", nullable = false)
	private String code;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "NAME_RU", nullable = false)
	private String nameRU;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 3, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_DIRECT_ID", referencedColumnName = "ID")})
    private STUDY_DIRECT studyDirect;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_CYCLE_ID", referencedColumnName = "ID")})
    private SUBJECT_CYCLE subjectCycle;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 7, required = false)
	@Column(name = "MANDATORY", nullable = false)
    private boolean mandatory;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 8, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 9)
	@Column(name = "CREDIT", nullable = false)
	private Integer credit;
	
	@FieldInfo(type = EFieldType.TEXT, order = 10)
	@Column(name = "CONTROL_TYPE_NAME", nullable = false)
	private String controlTypeName;
	
	public V_SUBJECT_SELECT() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNameRU() {
		return nameRU;
	}

	public void setNameRU(String nameRU) {
		this.nameRU = nameRU;
	}
	
	public STUDY_DIRECT getStudyDirect() {
		return studyDirect;
	}

	public void setStudyDirect(STUDY_DIRECT studyDirect) {
		this.studyDirect = studyDirect;
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

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
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
	
	public String getControlTypeName() {
		return controlTypeName;
	}

	public void setControlTypeName(String controlTypeName) {
		this.controlTypeName = controlTypeName;
	}

	@Override
	public String toString() {
		return code + " " + nameRU;
	}
}
