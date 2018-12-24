package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class PDF_DOCUMENT_ACCESS_POST extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PDF_DOCUMENT_ID", referencedColumnName = "ID", nullable = false)})
    private PDF_DOCUMENT pdfDocument;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "POST_ID", referencedColumnName = "ID", nullable = false)})
    private POST post;

    public PDF_DOCUMENT getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(PDF_DOCUMENT pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public POST getPost() {
        return post;
    }

    public void setPost(POST post) {
        this.post = post;
    }

    public PDF_DOCUMENT_ACCESS_POST() {
    }

}
