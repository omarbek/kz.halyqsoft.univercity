package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DOCUMENT extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = true, inEdit = false, inView = true ,order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "creator_employee_id", referencedColumnName = "ID", nullable = false)})
    private EMPLOYEE creatorEmployee;


    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_status_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_STATUS documentStatus;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_importance_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_IMPORTANCE documentImportance;


    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false , order = 4 )
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "pdf_document_id", referencedColumnName = "ID", nullable = false)})
    private PDF_DOCUMENT pdfDocument;


    @FieldInfo(type = EFieldType.TEXT,inEdit = false,  order = 5)
    @Column(name = "message")
    private String message;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, order = 6)
    @Column(name = "deadline_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadlineDate;



    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true,  inEdit = false,  order = 7)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true,  inEdit = false, order=8)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @FieldInfo(type = EFieldType.BOOLEAN, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order = 9)
    @Column(name = "deleted")
    private boolean deleted;


    @FieldInfo(inGrid = false , inView = false , inEdit = false, order = 5)
    @Column(name = "file_byte")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileByte;

    public USERS getCreatorEmployee() {
        return creatorEmployee;
    }

    public void setCreatorEmployee(EMPLOYEE creatorEmployee) {
        this.creatorEmployee = creatorEmployee;
    }

    public DOCUMENT_STATUS getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DOCUMENT_STATUS documentStatus) {
        this.documentStatus = documentStatus;
    }

    public PDF_DOCUMENT getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(PDF_DOCUMENT pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public DOCUMENT_IMPORTANCE getDocumentImportance() {
        return documentImportance;
    }

    public void setDocumentImportance(DOCUMENT_IMPORTANCE documentImportance) {
        this.documentImportance = documentImportance;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public void setFileByte(byte[] fileByte) {
        this.fileByte = fileByte;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public DOCUMENT() {
    }

}
