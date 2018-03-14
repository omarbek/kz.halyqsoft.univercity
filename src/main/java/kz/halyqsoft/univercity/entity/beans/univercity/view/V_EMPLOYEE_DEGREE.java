package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEGREE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.file.FileBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @@author Omarbek
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
	@Column(name = "SCHOOL_NAME", nullable = false)
	private String schoolName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 8, inGrid = false)
	@Column(name = "DISSERTATION_TOPIC", nullable = false)
	private String dissertationTopic;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 20, required = false, inEdit = false, inGrid = false, inView = false)
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
}
