package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.GROUP_SIZE_LAB;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.GROUP_SIZE_LECTURE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.GROUP_SIZE_PRAC;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
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
 * @created Dec 24, 2015 4:13:13 PM
 */
@Entity
public class TEACHER_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = 656623766838019754L;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private V_SUBJECT_SELECT subject;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 3, fieldWidth = 30)
	@Column(name = "GROUP_LEC_COUNT", nullable = false)
    private int groupLecCount;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUP_LEC_ID", referencedColumnName = "ID", nullable = true)})
    private GROUP_SIZE_LECTURE groupSizeLecture;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, fieldWidth = 30)
	@Column(name = "GROUP_LAB_COUNT", nullable = false)
	private int groupLabCount;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUP_LAB_ID", referencedColumnName = "ID", nullable = true)})
    private GROUP_SIZE_LAB groupSizeLab;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 7, fieldWidth = 30)
	@Column(name = "GROUP_PRAC_COUNT", nullable = false)
	private int groupPracCount;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 8, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUP_PRAC_ID", referencedColumnName = "ID", nullable = true)})
    private GROUP_SIZE_PRAC groupSizePrac;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 9, required = false)
	@Column(name = "FALL", nullable = false)
    private boolean fall;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 10, required = false)
	@Column(name = "SPRING", nullable = false)
    private boolean spring;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 11, required = false)
	@Column(name = "SUMMER", nullable = false)
    private boolean summer;
	
	@Column(name = "LOAD_PER_HOURS", nullable = false)
    private boolean loadPerHours;
	
	public TEACHER_SUBJECT() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public V_SUBJECT_SELECT getSubject() {
		return subject;
	}

	public void setSubject(V_SUBJECT_SELECT subject) {
		this.subject = subject;
	}

	public int getGroupLecCount() {
		return groupLecCount;
	}

	public void setGroupLecCount(int groupLecCount) {
		this.groupLecCount = groupLecCount;
	}

	public GROUP_SIZE_LECTURE getGroupSizeLecture() {
		return groupSizeLecture;
	}

	public void setGroupSizeLecture(GROUP_SIZE_LECTURE groupSizeLecture) {
		this.groupSizeLecture = groupSizeLecture;
	}

	public int getGroupLabCount() {
		return groupLabCount;
	}

	public void setGroupLabCount(int groupLabCount) {
		this.groupLabCount = groupLabCount;
	}

	public GROUP_SIZE_LAB getGroupSizeLab() {
		return groupSizeLab;
	}

	public void setGroupSizeLab(GROUP_SIZE_LAB groupSizeLab) {
		this.groupSizeLab = groupSizeLab;
	}

	public int getGroupPracCount() {
		return groupPracCount;
	}

	public void setGroupPracCount(int groupPracCount) {
		this.groupPracCount = groupPracCount;
	}

	public GROUP_SIZE_PRAC getGroupSizePrac() {
		return groupSizePrac;
	}

	public void setGroupSizePrac(GROUP_SIZE_PRAC groupSizePrac) {
		this.groupSizePrac = groupSizePrac;
	}

	public boolean isFall() {
		return fall;
	}

	public void setFall(boolean fall) {
		this.fall = fall;
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}
	
	public boolean isSummer() {
		return summer;
	}

	public void setSummer(boolean summer) {
		this.summer = summer;
	}

	public boolean isLoadPerHours() {
		return loadPerHours;
	}

	public void setLoadPerHours(boolean loadPerHours) {
		this.loadPerHours = loadPerHours;
	}
}
