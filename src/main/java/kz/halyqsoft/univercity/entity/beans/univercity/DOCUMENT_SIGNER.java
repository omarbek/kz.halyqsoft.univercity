package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DOCUMENT_SIGNER extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT document;


    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "employee_id", referencedColumnName = "ID", nullable = false)})
    private EMPLOYEE signerEmployee;


    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_signer_status_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_SIGNER_STATUS documentSignerStatus;


    @Column(name = "order", nullable = false)
    private int order;

    @FieldInfo(type = EFieldType.BOOLEAN, required = false, inGrid = false)
    @Column(name = "signed", nullable = false)
    private boolean signed;

    @FieldInfo(type = EFieldType.TEXT, required = false, inGrid = false)
    @Column(name = "comment", nullable = false)
    private boolean comment;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public DOCUMENT getDocument() {
        return document;
    }

    public void setDocument(DOCUMENT document) {
        this.document = document;
    }

    public EMPLOYEE getSignerEmployee() {
        return signerEmployee;
    }

    public void setSignerEmployee(EMPLOYEE signerEmployee) {
        this.signerEmployee = signerEmployee;
    }

    public DOCUMENT_SIGNER_STATUS getDocumentSignerStatus() {
        return documentSignerStatus;
    }

    public void setDocumentSignerStatus(DOCUMENT_SIGNER_STATUS documentSignerStatus) {
        this.documentSignerStatus = documentSignerStatus;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
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

    public DOCUMENT_SIGNER() {
    }

}
