package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CANDIDATE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEGREE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.QUALIFICATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import java.util.Date;

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
	@Column(name = "place_of_issue", nullable = false)
	private String placeOfIssue;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 7)
	@Column(name = "DISSERTATION_TOPIC", nullable = false)
	private String dissertationTopic;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 8)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "ID")})
	private CANDIDATE candidate;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 9)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
	private SPECIALITY speciality;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 10)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "QUALIFICATION_ID", referencedColumnName = "ID")})
	private QUALIFICATION qualification;

	@FieldInfo(type = EFieldType.DATE, max = 2099, order = 11)
	@Column(name = "ENTRANCE_YEAR", nullable = false)
	private Date entranceYear;


	public EMPLOYEE_DEGREE() {
	}

	public DEGREE getDegree() {
		return degree;
	}

	public void setDegree(DEGREE degree) {
		this.degree = degree;
	}

	public String getPlaceOfIssue() {
		return placeOfIssue;
	}

	public void setPlaceOfIssue(String placeOfIssue) {
		this.placeOfIssue = placeOfIssue;
	}

	public String getDissertationTopic() {
		return dissertationTopic;
	}

	public void setDissertationTopic(String dissertationTopic) {
		this.dissertationTopic = dissertationTopic;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public CANDIDATE getCandidate() {
		return candidate;
	}

	public void setCandidate(CANDIDATE candidate) {
		this.candidate = candidate;
	}

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
	}

	public QUALIFICATION getQualification() {
		return qualification;
	}

	public void setQualification(QUALIFICATION qualification) {
		this.qualification = qualification;
	}

	public Date getEntranceYear() {
		return entranceYear;
	}

	public void setEntranceYear(Date entranceYear) {
		this.entranceYear = entranceYear;
	}
}
