package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DOCUMENT_SIGNER extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inEdit = false, inView = false , inGrid = false , order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_id", referencedColumnName = "ID")})
    private DOCUMENT document;

    @FieldInfo(type = EFieldType.FK_COMBO, readOnlyFixed = true, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "post_id", referencedColumnName = "ID")})
    private POST post;


    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "employee_id", referencedColumnName = "ID")})
    private EMPLOYEE employee;

    @FieldInfo(type = EFieldType.FK_COMBO,inGrid = false, inEdit = false ,required = false , order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_signer_status_id", referencedColumnName = "ID")})
    private DOCUMENT_SIGNER_STATUS documentSignerStatus;


    @FieldInfo(type = EFieldType.TEXT, inEdit = false , inView = false , inGrid = false , required = false, order = 5)
    @Column(name = "message")
    private String message;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order=6)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order=7)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @FieldInfo(type = EFieldType.BOOLEAN, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order = 8)
    @Column(name = "deleted")
    private boolean deleted;

    public DOCUMENT getDocument() {
        return document;
    }

    public void setDocument(DOCUMENT document) {
        this.document = document;
    }

    public DOCUMENT_SIGNER_STATUS getDocumentSignerStatus() {
        return documentSignerStatus;
    }

    public void setDocumentSignerStatus(DOCUMENT_SIGNER_STATUS documentSignerStatus) {
        this.documentSignerStatus = documentSignerStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public USERS getEmployee() {
        return employee;
    }

    public void setEmployee(EMPLOYEE employee) {
        this.employee = employee;
    }

    public POST getPost() {
        return post;
    }

    public void setPost(POST post) {
        this.post = post;
    }

    public DOCUMENT_SIGNER() {
    }

}
