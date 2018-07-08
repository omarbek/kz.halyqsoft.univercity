package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.ui.Window;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class CreateViewDialog extends AbstractDialog {
    private final String title;
    private AbstractCommonView prevView;
    private GridWidget documentSignerGW;

    public CreateViewDialog(AbstractCommonView prevView , String title, DOCUMENT document, List<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPosts){
        this.title = title;
        this.prevView = prevView;
        setWidth(80 ,Unit.PERCENTAGE);
        setImmediate(true);
        setResponsive(true);

        this.addCloseListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent closeEvent) {
                Message.showInfo("CLOSED");
            }
        });

        if(pdfDocumentSignerPosts!=null){
            documentSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
            documentSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
            documentSignerGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
            DBGridModel dbGridModel = (DBGridModel) documentSignerGW.getWidgetModel();
            dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , document.getId());
            getContent().addComponent(documentSignerGW);
        }
    }



    @Override
    protected String createTitle() {
        return this.title;
    }
}
