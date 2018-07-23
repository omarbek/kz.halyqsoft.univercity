package kz.halyqsoft.univercity.modules.workflow.views;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.SignDocumentViewDialog;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class InOnSignView extends BaseView implements EntityListener{

    private USERS currentUser;
    private GridWidget inOnSignDocsGW;

    public InOnSignView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        currentUser = WorkflowCommonUtils.getCurrentUser();
        inOnSignDocsGW = new GridWidget(DOCUMENT.class);
        inOnSignDocsGW.setSizeFull();
        inOnSignDocsGW.setImmediate(true);
        inOnSignDocsGW.setResponsive(true);
        inOnSignDocsGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        inOnSignDocsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        inOnSignDocsGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        inOnSignDocsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        inOnSignDocsGW.addEntityListener(this);

        List<ID> ids = new ArrayList<>();
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS).getId());
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());
        DBGridModel dbGridModel = (DBGridModel) inOnSignDocsGW.getWidgetModel();
        QueryModel inOnSignDocsQM = dbGridModel.getQueryModel();
        inOnSignDocsQM.addJoin(EJoin.INNER_JOIN, "id", DOCUMENT_SIGNER.class , "document");
        inOnSignDocsQM.addWhereIn("documentStatus" , ids);

        getContent().addComponent(inOnSignDocsGW);

    }

    public GridWidget getInOnSignDocsGW() {
        return inOnSignDocsGW;
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(inOnSignDocsGW)){
            if(entityEvent.getAction() == EntityEvent.SELECTED){
                SignDocumentViewDialog signDocumentViewDialog = new SignDocumentViewDialog( this, getViewName(), (DOCUMENT) entityEvent.getEntities().iterator().next());
                signDocumentViewDialog.open();
            }
        }
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
