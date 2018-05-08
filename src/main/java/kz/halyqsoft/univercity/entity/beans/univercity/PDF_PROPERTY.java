package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;

@Entity
public class PDF_PROPERTY extends AbstractEntity{

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "pdf_document_id", referencedColumnName = "id")})
    private PDF_DOCUMENT pdfDocument;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "x", nullable = false)
    private float x;

    @Column(name = "y", nullable = false)
    private float y;

    @Column(name = "font", nullable = false)
    private String font;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "order_number", nullable = false)
    private double orderNumber;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public PDF_DOCUMENT getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(PDF_DOCUMENT pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public double getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(double orderNumber) {
        this.orderNumber = orderNumber;
    }
}
