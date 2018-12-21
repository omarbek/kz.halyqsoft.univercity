package kz.halyqsoft.univercity.modules.workflowforemp.views;

import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DOCUMENT_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PDF_DOCUMENT_TYPE;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.OpenPdfDialog;
import kz.halyqsoft.univercity.modules.workflowforemp.GridWidgetDialog;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.List;

/**
 * @author Assylkhan
 * on 06.12.2018
 * @project kz.halyqsoft.univercity
 */
public class DocumentTypesView extends BaseView implements EntityListener{
    private GridWidget documentTypesGW;
    public DocumentTypesView(String title) {
        super(title);
        documentTypesGW = new GridWidget(PDF_DOCUMENT_TYPE.class);
        documentTypesGW.setButtonVisible(IconToolbar.DELETE_BUTTON,false);
        documentTypesGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        documentTypesGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        documentTypesGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);
        documentTypesGW.addEntityListener(this);
        getContent().addComponent(documentTypesGW);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        if(entity != null){
            if(documentTypesGW.getSelectedEntity()!=null){
                GridWidgetDialog gridWidgetDialog = new GridWidgetDialog(getUILocaleUtil().getEntityLabel(PDF_DOCUMENT.class),PDF_DOCUMENT.class);
                gridWidgetDialog.getGenericGM().getQueryModel().addWhere("pdfDocumentType" , ECriteria.EQUAL, entity.getId());
                gridWidgetDialog.getGenericGM().getQueryModel().addWhereAnd("deleted" , ECriteria.EQUAL, false);
                gridWidgetDialog.getGenericGW().showToolbar(false);
                AbstractWebUI.getInstance().addWindow(gridWidgetDialog);
            }else{
                Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
            }
        }

        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }
}
