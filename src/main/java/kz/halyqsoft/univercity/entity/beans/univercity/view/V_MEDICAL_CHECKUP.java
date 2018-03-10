package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.USER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MEDICAL_CHECKUP_TYPE;
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
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 20, 2015 4:31:17 PM
 */
@Entity
public class V_MEDICAL_CHECKUP extends AbstractEntity {

	private static final long serialVersionUID = 5389115473346404914L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USER user;
	
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
        @JoinColumn(name = "CHECKUP_TYPE_ID", referencedColumnName = "ID")})
    private MEDICAL_CHECKUP_TYPE checkupType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 6, inEdit = false, inView = false)
	@Column(name = "CHECKUP_TYPE_NAME")
	private String checkupTypeName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 7, inGrid = false)
	@Column(name = "ISSUER_NAME", nullable = false)
	private String issuerName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 8, required = false, columnWidth = 200)
	@Column(name = "ALLOW_DORM", nullable = true)
    private boolean allowDorm;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 9, required = false, columnWidth = 200)
	@Column(name = "ALLOW_STUDY", nullable = true)
    private boolean allowStudy;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 10, required = false, columnWidth = 200)
	@Column(name = "ALLOW_WORK", nullable = true)
    private boolean allowWork;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 20, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@Transient
	@FieldInfo(type = EFieldType.FILE_LIST, order = 21, required = false, inGrid = false)
	private List<FileBean> fileList = new ArrayList<FileBean>();

	public V_MEDICAL_CHECKUP() {
	}

	public USER getUser() {
		return user;
	}

	public void setUser(USER user) {
		this.user = user;
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

	public MEDICAL_CHECKUP_TYPE getCheckupType() {
		return checkupType;
	}

	public void setCheckupType(MEDICAL_CHECKUP_TYPE checkupType) {
		this.checkupType = checkupType;
	}

	public String getCheckupTypeName() {
		return checkupTypeName;
	}

	public void setCheckupTypeName(String checkupTypeName) {
		this.checkupTypeName = checkupTypeName;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public boolean isAllowDorm() {
		return allowDorm;
	}

	public void setAllowDorm(boolean allowDorm) {
		this.allowDorm = allowDorm;
	}

	public boolean isAllowStudy() {
		return allowStudy;
	}

	public void setAllowStudy(boolean allowStudy) {
		this.allowStudy = allowStudy;
	}
	
	public boolean isAllowWork() {
		return allowWork;
	}

	public void setAllowWork(boolean allowWork) {
		this.allowWork = allowWork;
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
