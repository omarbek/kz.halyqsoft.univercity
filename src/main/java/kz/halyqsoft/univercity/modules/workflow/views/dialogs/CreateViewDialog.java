package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class CreateViewDialog extends AbstractDialog implements EntityListener{
    private final String title;
    private AbstractCommonView prevView;
    private GridWidget documentSignerGW;
    private DOCUMENT document;
    private ComboBox importanceCB;
    private TextArea messageTA;
    public CreateViewDialog(AbstractCommonView prevView , String title, DOCUMENT document, List<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPosts){
        this.title = title;
        this.prevView = prevView;
        this.document = document;
        setWidth(80 ,Unit.PERCENTAGE);
        setImmediate(true);
        setResponsive(true);
        setClosable(false);

        QueryModel<DOCUMENT_IMPORTANCE> importanceQM = new QueryModel<>(DOCUMENT_IMPORTANCE.class);

        BeanItemContainer<DOCUMENT_IMPORTANCE> importanceBIC= null;
        try{
            importanceBIC = new BeanItemContainer<>(DOCUMENT_IMPORTANCE.class,SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(importanceQM));
        }catch (Exception e){
            e.printStackTrace();
        }

        importanceCB = new ComboBox();
        importanceCB.setCaption(getUILocaleUtil().getEntityLabel(DOCUMENT_IMPORTANCE.class));
        importanceCB.setSizeFull();
        importanceCB.setContainerDataSource(importanceBIC);
        getContent().addComponent(importanceCB);

        messageTA = new TextArea(getUILocaleUtil().getCaption("message"));
        messageTA.setWidth(90 , Unit.PERCENTAGE);
        getContent().addComponent(messageTA);

        if(pdfDocumentSignerPosts!=null){
            documentSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
            documentSignerGW.setImmediate(true);
            documentSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
            documentSignerGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
            documentSignerGW.addEntityListener(this);
            DBGridModel dbGridModel = (DBGridModel) documentSignerGW.getWidgetModel();
            dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , document.getId());

            getContent().addComponent(documentSignerGW);
        }

        setYesListener(new AbstractYesButtonListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                for(Entity ds : documentSignerGW.getAllEntities()){
                    if(((DOCUMENT_SIGNER)ds).getEmployee()==null || importanceCB.getValue()==null){
                        Message.showError(getUILocaleUtil().getMessage("pdf.field.empty"));
                        open();
                        break;
                    }
                }
                document.setMessage(messageTA.getValue());
                document.setDocumentImportance((DOCUMENT_IMPORTANCE) importanceCB.getValue());
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                }catch (Exception e){
                    e.printStackTrace();
                }
                close();
            }
        });

        setNoListener(new Button.ClickListener() {


            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {


                Message.showConfirm(getUILocaleUtil().getMessage("all.values.deleted"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(documentSignerGW.getAllEntities());
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(document);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        close();
                    }
                });
                open();
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public GridWidget getDocumentSignerGW() {
        return documentSignerGW;
    }

    @Override
    protected String createTitle() {
        return this.title;
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

        AddEmployeeDialog addEmployeeDialog = null;
        try{
            addEmployeeDialog = new AddEmployeeDialog(CreateViewDialog.this , this.prevView.getViewName(), entity);
            documentSignerGW.refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
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
