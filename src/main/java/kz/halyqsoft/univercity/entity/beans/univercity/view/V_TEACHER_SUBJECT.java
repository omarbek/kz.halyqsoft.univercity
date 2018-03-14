package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
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
 * @created Dec 26, 2015 12:11:06 PM
 */
@Entity
public class V_TEACHER_SUBJECT extends AbstractEntity {

    private static final long serialVersionUID = 3570162232421096031L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, inEdit = false, inGrid = true)
	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 8, order = 4, inEdit = false, inView = false, inGrid = true, columnWidth = 120)
	@Column(name = "SUBJECT_CODE", nullable = false)
	private String subjectCode;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, columnWidth = 120)
	@Column(name = "GROUP_LEC_COUNT", nullable = false)
    private int groupLecCount;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, required = false, inEdit = false, inView = false, columnWidth = 120)
	@Column(name = "GROUP_LEC_SIZE")
	private String groupLecSize;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 7, columnWidth = 125)
	@Column(name = "GROUP_LAB_COUNT", nullable = false)
	private int groupLabCount;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, required = false, inEdit = false, inView = false, columnWidth = 120)
	@Column(name = "GROUP_LAB_SIZE")
	private String groupLabSize;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 9, columnWidth = 120)
	@Column(name = "GROUP_PRAC_COUNT", nullable = false)
	private int groupPracCount;
	
	@FieldInfo(type = EFieldType.TEXT, required = false, order = 10, inEdit = false, inView = false, columnWidth = 125)
	@Column(name = "GROUP_PRAC_SIZE")
	private String groupPracSize;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 11, columnWidth = 100)
	@Column(name = "FALL", nullable = false)
    private boolean fall;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 12, columnWidth = 112)
	@Column(name = "SPRING", nullable = false)
    private boolean spring;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 13, columnWidth = 100)
	@Column(name = "SUMMER", nullable = false)
    private boolean summer;
	
	@Column(name = "LOAD_PER_HOURS", nullable = false)
    private boolean loadPerHours;
	
	public V_TEACHER_SUBJECT() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public int getGroupLecCount() {
		return groupLecCount;
	}

	public void setGroupLecCount(int groupLecCount) {
		this.groupLecCount = groupLecCount;
	}

	public String getGroupLecSize() {
		return groupLecSize;
	}

	public void setGroupLecSize(String groupLecSize) {
		this.groupLecSize = groupLecSize;
	}

	public int getGroupLabCount() {
		return groupLabCount;
	}

	public void setGroupLabCount(int groupLabCount) {
		this.groupLabCount = groupLabCount;
	}

	public String getGroupLabSize() {
		return groupLabSize;
	}

	public void setGroupLabSize(String groupLabSize) {
		this.groupLabSize = groupLabSize;
	}

	public int getGroupPracCount() {
		return groupPracCount;
	}

	public void setGroupPracCount(int groupPracCount) {
		this.groupPracCount = groupPracCount;
	}

	public String getGroupPracSize() {
		return groupPracSize;
	}

	public void setGroupPracSize(String groupPracSize) {
		this.groupPracSize = groupPracSize;
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
