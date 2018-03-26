package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEGREE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Dec 21, 2015 9:28:22 AM
 */
@Entity
@DiscriminatorValue(value = "12")
public class EMPLOYEE_DEGREE extends USER_DOCUMENT {

	private static final long serialVersionUID = 5571702321959092699L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DEGREE_ID", referencedColumnName = "ID")})
    private DEGREE degree;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 6)
	@Column(name = "SCHOOL_NAME", nullable = false)
	private String schoolName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 7)
	@Column(name = "DISSERTATION_TOPIC", nullable = false)
	private String dissertationTopic;
	
	public EMPLOYEE_DEGREE() {
	}

	public DEGREE getDegree() {
		return degree;
	}

	public void setDegree(DEGREE degree) {
		this.degree = degree;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getDissertationTopic() {
		return dissertationTopic;
	}

	public void setDissertationTopic(String dissertationTopic) {
		this.dissertationTopic = dissertationTopic;
	}
}
