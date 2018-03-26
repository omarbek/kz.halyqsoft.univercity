package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SCIENTIFIC_MANAGEMENT_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.file.FileBean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Omarbek
 * @created Dec 30, 2015 2:28:41 PM
 */
@Entity
public class V_SCIENTIFIC_MANAGEMENT extends AbstractEntity {

	private static final long serialVersionUID = 4235359289463489563L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCIENTIFIC_MANAGEMENT_TYPE_ID", referencedColumnName = "ID")})
    private SCIENTIFIC_MANAGEMENT_TYPE scientificManagementType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, required = false, inEdit = false, inView = false)
    @Column(name = "SCIENTIFIC_MANAGEMENT_TYPE_NAME")
    private String scientificManagementTypeName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, isMemo = true, inGrid = false)
    @Column(name = "STUDENTS_FIO")
    @Lob
    private String studentsFIO;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, min = 1, max = 99, inGrid = false)
    @Column(name = "STUDENTS_COUNT")
    private Integer studentsCount;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, max = 64, required = false)
    @Column(name = "PROJECT_NAME")
    private String projectName;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 512, order = 7, inGrid = false)
	@Column(name = "TOPIC", nullable = false)
	private String topic;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, max = 16, required = false, inGrid = false)
    @Column(name = "RESULT_")
    private String result;
	
	@FieldInfo(type = EFieldType.TEXT, order = 9, max = 16, required = false, inGrid = false)
    @Column(name = "ACHIEVEMENT")
    private String achievement;
	
	@Transient
	@FieldInfo(type = EFieldType.FILE_LIST, order = 10, required = false, inGrid = false)
	private List<FileBean> fileList = new ArrayList<FileBean>();
	
	public V_SCIENTIFIC_MANAGEMENT() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public SCIENTIFIC_MANAGEMENT_TYPE getScientificManagementType() {
		return scientificManagementType;
	}

	public void setScientificManagementType(
			SCIENTIFIC_MANAGEMENT_TYPE scientificManagementType) {
		this.scientificManagementType = scientificManagementType;
	}

	public String getScientificManagementTypeName() {
		return scientificManagementTypeName;
	}

	public void setScientificManagementTypeName(String scientificManagementTypeName) {
		this.scientificManagementTypeName = scientificManagementTypeName;
	}

	public String getStudentsFIO() {
		return studentsFIO;
	}

	public void setStudentsFIO(String studentsFIO) {
		this.studentsFIO = studentsFIO;
	}

	public Integer getStudentsCount() {
		return studentsCount;
	}

	public void setStudentsCount(Integer studentsCount) {
		this.studentsCount = studentsCount;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}

	public List<FileBean> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileBean> fileList) {
		this.fileList = fileList;
	}
}
