package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @@author Omarbek
 * @created Nov 4, 2016 10:23:54 AM
 */
@Entity
public class V_STUDENT_SELECT extends AbstractEntity {

	private static final long serialVersionUID = -1799076309908139522L;

	@Column(name = "STUDENT_CODE")
	private String studentCode;
	
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
	
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	
	@Column(name = "FACULTY_NAME", nullable = false)
	private String facultyName;
	
	@Column(name = "SPEC_NAME", nullable = false)
	private String specialityName;
	
	public V_STUDENT_SELECT() {
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getSpecialityName() {
		return specialityName;
	}

	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}
}
