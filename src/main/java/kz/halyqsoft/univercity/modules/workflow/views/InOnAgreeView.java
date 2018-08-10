package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.event.MouseEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartmentInfo;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.OpenPdfDialog;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.SignDocumentViewDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
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
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;

public class InOnAgreeView extends BaseView implements EntityListener{

    private USERS currentUser;
    private GridWidget inOnAgreeDocsGW;
    private DBGridModel dbGridModel;

    public InOnAgreeView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        currentUser = WorkflowCommonUtils.getCurrentUser();
        inOnAgreeDocsGW = new GridWidget(DOCUMENT.class);
        inOnAgreeDocsGW.setSizeFull();

        inOnAgreeDocsGW.setImmediate(true);
        inOnAgreeDocsGW.setResponsive(true);
        inOnAgreeDocsGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        inOnAgreeDocsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        inOnAgreeDocsGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        inOnAgreeDocsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);

        List<ID> ids = new ArrayList<>();
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS).getId());

        dbGridModel = (DBGridModel) inOnAgreeDocsGW.getWidgetModel();
        dbGridModel.setRefreshType(ERefreshType.MANUAL);
        dbGridModel.setEntities(getList());

        QueryModel inOnAgreeDocsQM = dbGridModel.getQueryModel();

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button previewBtn = new Button(getUILocaleUtil().getCaption("preview"));
        previewBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(inOnAgreeDocsGW.getSelectedEntity()!=null){
                    OpenPdfDialog openPdfDialog = new OpenPdfDialog((DOCUMENT) inOnAgreeDocsGW.getSelectedEntity(), InOnAgreeView.this,700,700);
                    openPdfDialog.addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent closeEvent) {
                            try{
                                dbGridModel.setEntities(getList());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        buttonsPanel.addComponent(previewBtn);
        buttonsPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        getContent().addComponent(buttonsPanel);
        getContent().setComponentAlignment(buttonsPanel,Alignment.MIDDLE_CENTER);
        getContent().addComponent(inOnAgreeDocsGW);

    }


    public List<DOCUMENT> getList() {

        List<DOCUMENT> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();

        String sql = "SELECT d.id  FROM\n" +
                "  document d\n" +
                "INNER JOIN document_signer ds\n" +
                "    ON d.id = ds.document_id\n" +
                "INNER JOIN pdf_document_signer_post pdsp\n" +
                "    ON pdsp.pdf_document_id=d.pdf_document_id\n" +
                "WHERE d.document_status_id in (1,2)\n" +
                "      AND ds.document_signer_status_id = 1\n" +
                "      AND pdsp.post_id = ds.post_id\n" +
                "      AND ds.employee_id = "+CommonUtils.getCurrentUser().getId().getId().longValue()+"\n" +
                "      AND pdsp.id = (\n" +
                "  select min(pdsp2.id) as id from pdf_document pd\n" +
                "    INNER JOIN pdf_document_signer_post pdsp2\n" +
                "      ON pd.id = pdsp2.pdf_document_id\n" +
                "    INNER JOIN document d2\n" +
                "      ON pd.id = d2.pdf_document_id\n" +
                "    INNER JOIN document_signer s2\n" +
                "      ON d2.id = s2.document_id\n" +
                "    INNER JOIN document d3 ON pd.id = d3.pdf_document_id\n" +
                "  WHERE d2.id = d.id and s2.document_signer_status_id = 1 and s2.post_id=pdsp2.post_id" +
                ");" ;

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    ID id = ID.valueOf((Long)o);
                    try {
                        DOCUMENT document = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(DOCUMENT.class, id);
                        list.add(document);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }

        return list;
    }

    public GridWidget getInOnAgreeDocsGW() {
        return inOnAgreeDocsGW;
    }

    public DBGridModel getDbGridModel() {
        return dbGridModel;
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
        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {
        dbGridModel.setEntities(getList());
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
