package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractStatusEntity;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractTypeEntity;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DOCUMENT_USER_INPUT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false ,order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "pdf_document_id", referencedColumnName = "ID", nullable = false)})
    private PDF_DOCUMENT pdfDocument;

    @FieldInfo(type = EFieldType.TEXT,inEdit = false,  order = 2)
    @Column(name = "value")
    private String value;

    @FieldInfo(type = EFieldType.TEXT,inEdit = false,  order = 3)
    @Column(name = "description")
    private String description;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true,  inEdit = false,  order = 4)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public DOCUMENT_USER_INPUT() {

    }

    public PDF_DOCUMENT getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(PDF_DOCUMENT pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
