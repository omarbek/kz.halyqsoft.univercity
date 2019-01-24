package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_TITLE;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.SPECIALITY_CODE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CANDIDATE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEGREE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.file.FileBean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Omarbek
 * @created Dec 29, 2015 10:54:43 AM
 */
@Entity
public class V_EMPLOYEE_DEGREE extends AbstractEntity {

	private static final long serialVersionUID = 5789627256671349500L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
	private EMPLOYEE employee;

	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 2, inGrid = false)
	@Column(name = "DOCUMENT_NO", nullable = false)
	private String documentNo;

	@FieldInfo(type = EFieldType.DATE, order = 3, inGrid = false)
	@Column(name = "ISSUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date issueDate;

	@FieldInfo(type = EFieldType.DATE, order = 4, required = false, inGrid = false)
	@Column(name = "EXPIRE_DATE")
	@Temporal(TemporalType.DATE)
	private Date expireDate;


	@FieldInfo(type = EFieldType.FK_COMBO, order = 5, inGrid = false)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "DEGREE_ID", referencedColumnName = "ID")})
	private DEGREE degree;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 6, inEdit = false, inView = false)
	@Column(name = "DEGREE_NAME", nullable = false)
	private String degreeName;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 7)
	@Column(name = "place_of_issue", nullable = false)
	private String placeOfIssue;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 8, inGrid = false)
	@Column(name = "DISSERTATION_TOPIC", nullable = false)
	private String dissertationTopic;

	@FieldInfo(type = EFieldType.FK_COMBO, required = false, order = 9)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "CONDIDATE_ID", referencedColumnName = "ID")})
	private CANDIDATE candidate;

	@FieldInfo(type = EFieldType.FK_COMBO, required = false, order = 10)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
	private SPECIALITY speciality;

	@FieldInfo(type = EFieldType.FK_COMBO, required = false, order = 11)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SPECIALITY_CODE_ID", referencedColumnName = "ID")})
	private SPECIALITY_CODE specialityCode;

	@FieldInfo(type = EFieldType.FK_COMBO, required = false, order = 12)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "ACADEMIC_TITLE_ID", referencedColumnName = "ID")})
	private ACADEMIC_TITLE academicTitle;


	@FieldInfo(type = EFieldType.BOOLEAN, order = 13, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	@Transient
	@FieldInfo(type = EFieldType.FILE_LIST, order = 21, required = false, inGrid = false)
	private List<FileBean> fileList = new ArrayList<FileBean>();

	public V_EMPLOYEE_DEGREE() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public DEGREE getDegree() {
		return degree;
	}

	public void setDegree(DEGREE degree) {
		this.degree = degree;
	}

	public String getDegreeName() {
		return degreeName;
	}

	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<FileBean> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileBean> fileList) {
		this.fileList = fileList;
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
