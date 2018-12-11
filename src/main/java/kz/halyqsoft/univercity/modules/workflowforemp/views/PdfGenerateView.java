package kz.halyqsoft.univercity.modules.workflowforemp.views;

import kz.halyqsoft.univercity.modules.pdf.PdfView;
import kz.halyqsoft.univercity.modules.pdf.PdfViewContent;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;

/**
 * @author Assylkhan
 * on 11.12.2018
 * @project kz.halyqsoft.univercity
 */
public class PdfGenerateView extends BaseView{

    public PdfGenerateView(String title) {
        super(title);
        PdfViewContent pdfViewContent = new PdfViewContent(true);
        pdfViewContent.setSizeFull();
        getContent().addComponent(pdfViewContent);
    }


}
