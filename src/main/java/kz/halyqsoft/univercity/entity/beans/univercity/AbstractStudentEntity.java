package kz.halyqsoft.univercity.entity.beans.univercity;

import javax.persistence.MappedSuperclass;

/**
 * @@author Omarbek
 * @created Dec 28, 2016 3:46:19 PM
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class AbstractStudentEntity extends AbstractSpiderEntity {

	public abstract String getFacultyName();

	public abstract void setFacultyName(String facultyName);
	
	public abstract String getFacultyCode();

	public abstract void setFacultyCode(String facultyCode);
	
	public abstract String getFacultyShortName();

	public abstract void setFacultyShortName(String facultyShortName);
	
	public abstract String getChairName();

	public abstract void setChairName(String chairName);
	
	public abstract String getSpecialityName();

	public abstract void setSpecialityName(String specialtyName);
	
	public abstract String getSpecialityCode();

	public abstract void setSpecialityCode(String specialtyCode);
}
