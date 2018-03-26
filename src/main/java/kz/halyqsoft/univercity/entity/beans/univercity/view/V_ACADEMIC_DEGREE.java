package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Feb 22, 2016 1:14:46 PM
 */
@Entity
public class V_ACADEMIC_DEGREE extends AbstractEntity {
	
	private static final long serialVersionUID = -113995281203120702L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1, inGrid = false, columnWidth = 80)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 2, inEdit = false, inView = false)
	@Column(name = "SPEC_NAME", nullable = false)
	private String specialityName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, columnWidth = 200)
	@Column(name = "DEGREE_NAME", nullable = false)
	private String degreeName;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 4, order = 4, columnWidth = 120)
	@Column(name = "STUDY_PERIOD", nullable = false)
	private Integer studyPeriod;

	public V_ACADEMIC_DEGREE() {
	}

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
	}

	public String getSpecialityName() {
		return specialityName;
	}

	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}

	public String getDegreeName() {
		return degreeName;
	}

	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
	}

	public Integer getStudyPeriod() {
		return studyPeriod;
	}

	public void setStudyPeriod(Integer studyPeriod) {
		this.studyPeriod = studyPeriod;
	}
}
