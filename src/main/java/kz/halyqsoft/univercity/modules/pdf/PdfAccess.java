package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE_DEPT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT_SIGNER_POST;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.List;
import java.util.Locale;

public class PdfAccess extends AbstractCommonView implements EntityListener{

    private HorizontalSplitPanel mainHSP;

    private VerticalLayout firstVL;
    private VerticalLayout secondVL;
    private HorizontalSplitPanel innerSecondHL;
    private GridWidget pdfDocumentGW;
    private GridWidget postGW;
    private GridWidget pdfDocumentSignerPostGW;
    public PdfAccess() {
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSplitPosition(30);
        mainHSP.setSizeFull();
        mainHSP.setImmediate(true);
        mainHSP.setResponsive(true);

        firstVL = new VerticalLayout();
        firstVL.setImmediate(true);
        firstVL.setResponsive(true);
        initFirstVL();

        secondVL = new VerticalLayout();
        secondVL.setImmediate(true);
        secondVL.setResponsive(true);

        innerSecondHL = new HorizontalSplitPanel();
        innerSecondHL.setImmediate(true);
        innerSecondHL.setResponsive(true);


        mainHSP.setFirstComponent(firstVL);
        mainHSP.setSecondComponent(secondVL);

        getContent().addComponent(mainHSP);
    }

    public void initFirstVL(){

        pdfDocumentGW = new GridWidget(PDF_DOCUMENT.class);
        pdfDocumentGW.setMultiSelect(false);
        DBGridModel dbGridModel = (DBGridModel) pdfDocumentGW.getWidgetModel();
        QueryModel<PDF_DOCUMENT> pdfDocumentQM = dbGridModel.getQueryModel();
        pdfDocumentQM.addWhere("deleted" , ECriteria.EQUAL , false);
        pdfDocumentGW.addEntityListener(this);
        pdfDocumentGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON , false);
        pdfDocumentGW.setButtonVisible(IconToolbar.EDIT_BUTTON , false);
        pdfDocumentGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);

        firstVL.addComponent(pdfDocumentGW);
    }
    @Override
    public String getViewName() {
        return getUILocaleUtil().getCaption("access");
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("access");
    }

    @Override
    public void initView(boolean b) throws Exception {

    }



    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getAction()==EntityEvent.SELECTED){
            if(entityEvent.getSource().equals(pdfDocumentGW))
            {
                if( pdfDocumentGW.getSelectedEntity()!=null) {
                    innerSecondHL.removeAllComponents();

                    postGW = new GridWidget(POST.class);
                    postGW.setResponsive(true);
                    postGW.setMultiSelect(false);
                    postGW.setImmediate(true);

                    DBGridModel dbGridModel = (DBGridModel) postGW.getWidgetModel();

                    dbGridModel.getColumnModel("studyLoad").setInGrid(false);
                    dbGridModel.getColumnModel("tp").setInGrid(false);
                    dbGridModel.getColumnModel("priority").setInGrid(false);

                    QueryModel<POST> postQM = dbGridModel.getQueryModel();

                    FromItem fi = postQM.addJoin(EJoin.INNER_JOIN, "id", EMPLOYEE_DEPT.class, "post");
                    fi.addJoin(EJoin.INNER_JOIN, "employee", EMPLOYEE.class, "id");
                    postGW.addEntityListener(this);
                    postGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                    postGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                    postGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);

                    innerSecondHL.addComponent(postGW);

                    secondVL.addComponent(innerSecondHL);
                    if (pdfDocumentSignerPostGW != null) {
                        innerSecondHL.removeComponent(pdfDocumentSignerPostGW);
                    }
                    pdfDocumentSignerPostGW = new GridWidget(PDF_DOCUMENT_SIGNER_POST.class);
                    pdfDocumentSignerPostGW.setImmediate(true);
                    pdfDocumentSignerPostGW.setMultiSelect(false);
                    pdfDocumentSignerPostGW.setResponsive(true);
                    DBGridModel pdfDbGridModel = (DBGridModel) pdfDocumentSignerPostGW.getWidgetModel();
                    pdfDbGridModel.getQueryModel().addWhere("pdfDocument", ECriteria.EQUAL, pdfDocumentGW.getSelectedEntity().getId());
                    pdfDbGridModel.setRowNumberVisible(true);

                    pdfDocumentSignerPostGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                    pdfDocumentSignerPostGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                    pdfDocumentSignerPostGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);

                    innerSecondHL.addComponent(pdfDocumentSignerPostGW);

                }else {
                    secondVL.removeAllComponents();
                }
            }
        }else if(entityEvent.getAction()==EntityEvent.UNSELECTED){
            if(entityEvent.getSource().equals(pdfDocumentGW))
            {
                secondVL.removeAllComponents();
            }
        }else{
            if(pdfDocumentGW.getSelectedEntity()==null)
            {
                secondVL.removeAllComponents();
            }
        }

    }

    @Override
    public boolean preCreate(Object o, int i) {
        if(postGW.getSelectedEntity()!=null && pdfDocumentGW.getSelectedEntity()!=null){
            PDF_DOCUMENT_SIGNER_POST pdfDocumentSignerPost = new PDF_DOCUMENT_SIGNER_POST();
            pdfDocumentSignerPost.setPdfDocument((PDF_DOCUMENT) pdfDocumentGW.getSelectedEntity());
            pdfDocumentSignerPost.setPost((POST)postGW.getSelectedEntity());
            try{
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfDocumentSignerPost);
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                pdfDocumentSignerPostGW.refresh();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }else{
            Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
        }
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
        if(list!=null){
            if(list.size()>0){
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(list);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
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
