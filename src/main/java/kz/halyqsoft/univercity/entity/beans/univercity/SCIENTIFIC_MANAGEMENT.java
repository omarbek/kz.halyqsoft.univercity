package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SCIENTIFIC_MANAGEMENT_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Dec 21, 2015 4:53:58 PM
 */
@Entity
@DiscriminatorValue(value = "3")
public class SCIENTIFIC_MANAGEMENT extends EMPLOYEE_SCIENTIFIC {

	private static final long serialVersionUID = 454394247376258471L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCIENTIFIC_MANAGEMENT_TYPE_ID", referencedColumnName = "ID")})
    private SCIENTIFIC_MANAGEMENT_TYPE scientificManagementType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, isMemo = true, required = false, inGrid = false)
    @Column(name = "STUDENTS_FIO")
    @Lob
    private String studentsFIO;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, max = 99, required = false, inGrid = false)
	@Column(name = "STUDENTS_COUNT")
    private Integer studentsCount;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, max = 64, required = false)
    @Column(name = "PROJECT_NAME")
    private String projectName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7, max = 16, required = false, inGrid = false)
    @Column(name = "RESULT_")
    private String result;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, max = 16, required = false, inGrid = false)
    @Column(name = "ACHIEVEMENT")
    private String achievement;
	
	public SCIENTIFIC_MANAGEMENT() {
	}

	public SCIENTIFIC_MANAGEMENT_TYPE getScientificManagementType() {
		return scientificManagementType;
	}

	public void setScientificManagementType(SCIENTIFIC_MANAGEMENT_TYPE scientificManagementType) {
		this.scientificManagementType = scientificManagementType;
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
}
