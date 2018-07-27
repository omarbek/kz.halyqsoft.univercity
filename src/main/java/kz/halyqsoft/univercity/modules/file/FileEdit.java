package kz.halyqsoft.univercity.modules.file;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.modules.pdf.PdfEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.ID;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.table.TableWidget;

/**
 * @author Omarbek
 * @created on 26.04.2018
 */
public class FileEdit extends AbstractDialog {

    public FileEdit(PDF_DOCUMENT pdfDocument, AbstractTaskView fileView)  {
        setWidth(1300,Unit.PIXELS);
        setHeight(500,Unit.PIXELS);
        center();

        PdfEdit pdfEdit = new PdfEdit(pdfDocument,  null);
        getContent().addComponent(pdfEdit);
        getContent().setComponentAlignment(pdfEdit, Alignment.MIDDLE_CENTER);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                    ((FileView)fileView).refresh();
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
