package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractStatusEntity;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DOCUMENT_USER_REAL_INPUT extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false ,order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT document;

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false ,order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_user_input_id", referencedColumnName = "ID", nullable = false)})
    private DOCUMENT_USER_INPUT documentUserInput;

    @FieldInfo(type = EFieldType.TEXT,inEdit = false,  order = 3)
    @Column(name = "value")
    private String value;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true,  inEdit = false,  order = 4)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public DOCUMENT_USER_REAL_INPUT() {
    }

    public DOCUMENT getDocument() {
        return document;
    }

    public void setDocument(DOCUMENT document) {
        this.document = document;
    }

    public DOCUMENT_USER_INPUT getDocumentUserInput() {
        return documentUserInput;
    }

    public void setDocumentUserInput(DOCUMENT_USER_INPUT documentUserInput) {
        this.documentUserInput = documentUserInput;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
