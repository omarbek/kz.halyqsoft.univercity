package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalSplitPanel;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;

public class PdfGenerationPart {



    private PDF_DOCUMENT pdfDocument;
    private HorizontalSplitPanel pdfHSP;
    private Embedded pdfEmbedded;
    private String loading;
    public PdfGenerationPart(PDF_DOCUMENT pdfDocument,  String loading){
        this.pdfDocument = pdfDocument;
        this.loading = loading;
        pdfHSP = new HorizontalSplitPanel();
        pdfHSP.setSizeFull();
        pdfHSP.setResponsive(true);
        pdfHSP.setImmediate(true);
        pdfHSP.setSplitPosition(80);
        init();
    }

    private void init(){


        PdfEdit pdfEdit = new PdfEdit(this.pdfDocument, PdfGenerationPart.this);

        pdfHSP.setFirstComponent(pdfEdit);

    }

    public void createEmbedded(){
        pdfEmbedded = new Embedded();
        pdfEmbedded.setImmediate(true);
        pdfEmbedded.setMimeType("application/pdf");
        pdfEmbedded.setType(2);
        pdfEmbedded.setSizeFull();
        pdfEmbedded.setHeight(700, Sizeable.Unit.PIXELS);
        pdfHSP.setSecondComponent(pdfEmbedded);
    }

    public Embedded getPdfEmbedded() {
        return pdfEmbedded;
    }

    public HorizontalSplitPanel getPdfHSP() {
        return pdfHSP;
    }
}
