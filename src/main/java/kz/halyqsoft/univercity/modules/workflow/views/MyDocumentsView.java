package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.List;

public class MyDocumentsView extends BaseView implements EntityListener{
    private USERS currentUser;
    private GridWidget myDocsGW;
    public MyDocumentsView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);
        currentUser = WorkflowCommonUtils.getCurrentUser();
        myDocsGW = new GridWidget(DOCUMENT.class);
        myDocsGW.setSizeFull();
        myDocsGW.setImmediate(true);
        myDocsGW.setResponsive(true);
        myDocsGW.addEntityListener(this);
        myDocsGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        myDocsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);


        DBGridModel dbGridModel = (DBGridModel) myDocsGW.getWidgetModel();
        dbGridModel.getQueryModel().addWhere("creatorEmployee" , ECriteria.EQUAL , currentUser.getId());

        getContent().addComponent(myDocsGW);
    }


    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(myDocsGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                AbstractDialog abstractDialog = new AbstractDialog() {

                    public void init(){
                        GridWidget myDocsSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
                        myDocsSignerGW.setSizeFull();
                        myDocsSignerGW.setImmediate(true);
                        myDocsSignerGW.setResponsive(true);
                        myDocsSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);

                        DBGridModel dbGridModel = (DBGridModel) myDocsSignerGW.getWidgetModel();
                        dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , myDocsGW.getSelectedEntity().getId());

                        getContent().addComponent(myDocsSignerGW);
                    }

                    @Override
                    protected String createTitle() {
                        init();
                        return getViewName();
                    }
                };
                if(myDocsGW.getSelectedEntity()!=null){
                    abstractDialog.open();
                }

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
