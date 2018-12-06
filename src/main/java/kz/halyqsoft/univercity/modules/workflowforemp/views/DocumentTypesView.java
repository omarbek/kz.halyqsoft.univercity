package kz.halyqsoft.univercity.modules.workflowforemp.views;

import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DOCUMENT_TYPE;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.OpenPdfDialog;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

/**
 * @author Assylkhan
 * on 06.12.2018
 * @project kz.halyqsoft.univercity
 */
public class DocumentTypesView extends BaseView{
    private GridWidget documentTypesGW;
    public DocumentTypesView(String title) {
        super(title);
        documentTypesGW = new GridWidget(DOCUMENT_TYPE.class);
        documentTypesGW.setButtonVisible(IconToolbar.DELETE_BUTTON,false);
        getContent().addComponent(documentTypesGW);
    }
}
