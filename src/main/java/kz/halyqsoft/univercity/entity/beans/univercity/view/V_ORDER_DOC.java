package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.USER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORDER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
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
 * @created Jul 4, 2016 6:37:05 PM
 */
@Entity
public class V_ORDER_DOC extends AbstractEntity {

	private static final long serialVersionUID = 1435931238667577050L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USER user;
	
	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 2)
	@Column(name = "DOCUMENT_NO", nullable = false)
	private String documentNo;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ORDER_TYPE_ID", referencedColumnName = "ID")})
    private ORDER_TYPE orderType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 4, inEdit = false, inView = false)
	@Column(name = "ORDER_TYPE_NAME", nullable = false)
	private String orderTypeName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, required = false, inGrid = false)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 4, order = 6, inEdit = false, inView = false)
	@Column(name = "STUDY_YEAR", nullable = false)
	private Integer studyYearYear;
	
	@FieldInfo(type = EFieldType.DATE, order = 7)
	@Column(name = "ISSUE_DATE")
    @Temporal(TemporalType.DATE)
    private Date issueDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 8, required = false, inGrid = false)
	@Column(name = "EXPIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date expireDate;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 9, inGrid = false)
	@Column(name = "DESCR")
	private String descr;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 10, required = false)
	@Column(name = "HIDE_TRANSCRIPT")
    private boolean hideTranscript;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 11, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@Transient
	@FieldInfo(type = EFieldType.FILE_LIST, order = 12, required = false, inGrid = false)
	private List<FileBean> fileList = new ArrayList<FileBean>();
	
	public V_ORDER_DOC() {
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

	public ORDER_TYPE getOrderType() {
		return orderType;
	}

	public void setOrderType(ORDER_TYPE orderType) {
		this.orderType = orderType;
	}

	public String getOrderTypeName() {
		return orderTypeName;
	}

	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}

	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}

	public Integer getStudyYearYear() {
		return studyYearYear;
	}

	public void setStudyYearYear(Integer studyYearYear) {
		this.studyYearYear = studyYearYear;
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

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public boolean isHideTranscript() {
		return hideTranscript;
	}

	public void setHideTranscript(boolean hideTranscript) {
		this.hideTranscript = hideTranscript;
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
