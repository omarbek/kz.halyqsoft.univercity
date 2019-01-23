package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CANDIDATE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEGREE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

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
			@JoinColumn(name = "CONDIDATE_ID", referencedColumnName = "ID")})
	private CANDIDATE candidate;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 9)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
	private SPECIALITY speciality;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 10)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SPECIALITY_CODE_ID", referencedColumnName = "ID")})
	private SPECIALITY_CODE specialityCode;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 11)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "ACADEMIC_TITLE_ID", referencedColumnName = "ID")})
	private ACADEMIC_TITLE academicTitle;



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

	public SPECIALITY_CODE getSpecialityCode() {
		return specialityCode;
	}

	public void setSpecialityCode(SPECIALITY_CODE specialityCode) {
		this.specialityCode = specialityCode;
	}

	public ACADEMIC_TITLE getAcademicTitle() {
		return academicTitle;
	}

	public void setAcademicTitle(ACADEMIC_TITLE academicTitle) {
		this.academicTitle = academicTitle;
	}
}
