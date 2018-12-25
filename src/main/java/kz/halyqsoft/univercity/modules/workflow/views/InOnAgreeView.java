package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ThemeResource;
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
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;

public class InOnAgreeView extends BaseView implements EntityListener{

    private USERS currentUser;
    private GridWidget inOnAgreeDocsGW;
    private DBGridModel dbGridModel;
    private Button linkedTables;

    public InOnAgreeView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        linkedTables = new Button(getUILocaleUtil().getCaption("employeesPanel"));
        linkedTables.setIcon(new ThemeResource("img/button/users.png"));
        linkedTables.setData(12);
        linkedTables.setStyleName("preview");

        linkedTables.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                AbstractDialog abstractDialog = new AbstractDialog() {

                    public void init(){
                        setWidth(50, Unit.PERCENTAGE);
                        GridWidget outMyDocsSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
                        outMyDocsSignerGW.setSizeFull();
                        outMyDocsSignerGW.setImmediate(true);

                        outMyDocsSignerGW.setResponsive(true);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);

                        DBGridModel dbGridModel = (DBGridModel) outMyDocsSignerGW.getWidgetModel();
                        dbGridModel.getFormModel().getFieldModel("documentSignerStatus").setInView(true);

                        dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , inOnAgreeDocsGW.getSelectedEntity().getId());

                        getContent().addComponent(outMyDocsSignerGW);

                    }

                    @Override
                    protected String createTitle() {
                        init();
                        return getViewName();
                    }
                };
                if(inOnAgreeDocsGW.getSelectedEntity()!=null){
                    abstractDialog.open();
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        currentUser = WorkflowCommonUtils.getCurrentUser();
        inOnAgreeDocsGW = new GridWidget(DOCUMENT.class);
        inOnAgreeDocsGW.setSizeFull();
        inOnAgreeDocsGW.getToolbarPanel().addComponent(linkedTables);
        inOnAgreeDocsGW.getToolbarPanel().setSizeUndefined();
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
                                inOnAgreeDocsGW.refresh();
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
        long userId = CommonUtils.getCurrentUser().getId().getId().longValue();
        String sql = "SELECT d.id\n" +
                "FROM\n" +
                "  document d\n" +
                "  INNER JOIN document_signer ds\n" +
                "    ON d.id = ds.document_id\n" +
                "  INNER JOIN pdf_document_signer_post pdsp\n" +
                "    ON pdsp.pdf_document_id = d.pdf_document_id\n" +
                "  INNER JOIN (SELECT\n" +
                "                min(pdsp2.id) AS id,\n" +
                "                d2.id         AS d2id\n" +
                "              FROM pdf_document pd\n" +
                "                INNER JOIN pdf_document_signer_post pdsp2\n" +
                "                  ON pd.id = pdsp2.pdf_document_id\n" +
                "                INNER JOIN document d2\n" +
                "                  ON pd.id = d2.pdf_document_id\n" +
                "                INNER JOIN document_signer s2\n" +
                "                  ON d2.id = s2.document_id\n" +
                "                INNER JOIN document d3 ON pd.id = d3.pdf_document_id\n" +
                "              WHERE s2.document_signer_status_id = 1 AND s2.post_id = pdsp2.post_id\n" +
                "              GROUP BY d2.id\n" +
                "             ) newtab\n" +
                "    ON pdsp.id = newtab.id AND newtab.d2id = d.id\n" +
                "WHERE d.document_status_id IN (1, 2)\n" +
                "      AND ds.document_signer_status_id = 1\n" +
                "      AND pdsp.post_id = ds.post_id\n" +
                "      AND ds.employee_id = "+ userId +" AND d.is_parallel = FALSE\n" +
                "UNION ALL\n" +
                "SELECT d2.id\n" +
                "FROM document d2\n" +
                "  INNER JOIN document_signer ds2 ON d2.id = ds2.document_id\n" +
                "  INNER JOIN pdf_document_signer_post pdsp2\n" +
                "    ON pdsp2.pdf_document_id = d2.pdf_document_id\n" +
                "  INNER JOIN pdf_document pd2 ON d2.pdf_document_id = pd2.id\n" +
                "WHERE d2.document_status_id IN (1, 2)\n" +
                "      AND ds2.document_signer_status_id = 1\n" +
                "      AND pdsp2.post_id = ds2.post_id\n" +
                "      AND ds2.employee_id = " + userId + " AND d2.is_parallel = TRUE;" ;

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
