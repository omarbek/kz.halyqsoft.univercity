package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.modules.pdf.tabs.SearchTabContainer;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;




public class MyFileEdit extends AbstractDialog {

    public MyFileEdit(PDF_DOCUMENT pdfDocument, AbstractCommonView fileView)  {
        setWidth(1300,Unit.PIXELS);
        setHeight(500,Unit.PIXELS);
        center();

        PdfEdit pdfEdit = new PdfEdit(pdfDocument, null,false);
        getContent().addComponent(pdfEdit);
        getContent().setComponentAlignment(pdfEdit, Alignment.MIDDLE_CENTER);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                    ((SearchTabContainer)fileView).refresh();
                } catch (Exception e) {
                   CommonUtils.showMessageAndWriteLog("Unable to refresh fileView", e);
                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return "Document";
    }
}
