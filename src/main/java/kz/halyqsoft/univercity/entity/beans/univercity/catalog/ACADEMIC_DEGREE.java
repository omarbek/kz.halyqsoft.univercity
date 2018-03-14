package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

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
 * Created Feb 22, 2016 12:09:45 PM
 */
@Entity
public class ACADEMIC_DEGREE extends AbstractEntity {

	private static final long serialVersionUID = -3065981346747173605L;

	@FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 2)
	@Column(name = "DEGREE_NAME", nullable = false)
	private String degreeName;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 4, order = 3)
	@Column(name = "STUDY_PERIOD", nullable = false)
	private Integer studyPeriod;
	
	public ACADEMIC_DEGREE() {
	}

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
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
