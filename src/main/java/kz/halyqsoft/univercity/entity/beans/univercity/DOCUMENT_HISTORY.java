package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DOCUMENT_HISTORY extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_signer_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_SIGNER documentSigner;


    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_activity_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_ACTIVITY documentActivity;


    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_signer_status_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_SIGNER_STATUS documentSignerStatus;


    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public DOCUMENT_SIGNER_STATUS getDocumentSignerStatus() {
        return documentSignerStatus;
    }

    public void setDocumentSignerStatus(DOCUMENT_SIGNER_STATUS documentSignerStatus) {
        this.documentSignerStatus = documentSignerStatus;
    }

    public DOCUMENT_SIGNER getDocumentSigner() {
        return documentSigner;
    }

    public void setDocumentSigner(DOCUMENT_SIGNER documentSigner) {
        this.documentSigner = documentSigner;
    }

    public DOCUMENT_ACTIVITY getDocumentActivity() {
        return documentActivity;
    }

    public void setDocumentActivity(DOCUMENT_ACTIVITY documentActivity) {
        this.documentActivity = documentActivity;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public DOCUMENT_HISTORY() {
    }

}
