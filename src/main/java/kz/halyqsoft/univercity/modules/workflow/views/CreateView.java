package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.filter.FPdfDocumentFilter;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.PdfDocumentFilterPanel;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.CreateViewDialog;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.FacultyChangeListener;
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
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;
import java.util.Calendar;

public class CreateView extends BaseView implements EntityListener, FilterPanelListener{

    private HorizontalSplitPanel mainHSP;
    private PdfDocumentFilterPanel pdfDocumentFilterPanel;
    private VerticalLayout firstVL;
    private VerticalLayout secondVL;

    private GridWidget pdfDocumentGW;
    private GridWidget pdfDocSignerPostGW;
    private Button btnCreate;

    public CreateView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSplitPosition(72);
        mainHSP.setSizeFull();
        mainHSP.setResponsive(true);
        mainHSP.setImmediate(true);

        firstVL = new VerticalLayout();
        firstVL.setImmediate(true);
        firstVL.setSizeFull();
        secondVL = new VerticalLayout();
        secondVL.setImmediate(true);
        secondVL.setSizeFull();

        pdfDocumentGW = new GridWidget(PDF_DOCUMENT.class);
        pdfDocumentGW.addEntityListener(this);
        pdfDocumentGW.setImmediate(true);
        pdfDocumentGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);

        DBGridModel pdfDocumentGM = (DBGridModel) pdfDocumentGW.getWidgetModel();
        pdfDocumentGM.setRefreshType(ERefreshType.MANUAL);
        pdfDocumentGM.setRowNumberVisible(true);
        pdfDocumentGM.setRowNumberWidth(100);

        btnCreate = new Button(getUILocaleUtil().getCaption("create"));
        btnCreate.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PDF_DOCUMENT pdfDocument= (PDF_DOCUMENT) pdfDocumentGW.getSelectedEntity();
                if(pdfDocument!=null){
                    USERS user = WorkflowCommonUtils.getCurrentUser();
                    DOCUMENT_STATUS documentStatus = WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED);
                    DOCUMENT_IMPORTANCE documentImportance = WorkflowCommonUtils.getDocumentImportanceByValue(DOCUMENT_IMPORTANCE.IMPORTANCE_1);
                    DOCUMENT document = new DOCUMENT();

                    Calendar deadlineDate = Calendar.getInstance();
                    deadlineDate.setTime(new Date());
                    deadlineDate.add(Calendar.DATE , pdfDocument.getPeriod());

                    document.setCreated(new Date());
                    document.setCreatorEmployee((EMPLOYEE) user);
                    document.setDocumentImportance(documentImportance);
                    document.setPdfDocument(pdfDocument);
                    document.setDocumentStatus(documentStatus);
                    document.setDeadlineDate(deadlineDate.getTime());
                    document.setDeleted(false);
                    EmployeePdfCreator.createResourceWithReloadingResource(document).getStreamSource().getStream();

                    try{
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(document);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Message.showError(e.getMessage());
                    }
                    List<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPosts = null;

                    if(document.getId()!=null){

                        try{
                            QueryModel<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPostQM = new QueryModel<>(PDF_DOCUMENT_SIGNER_POST.class);
                            pdfDocumentSignerPostQM.addWhere("pdfDocument" ,ECriteria.EQUAL , document.getPdfDocument().getId());
                            pdfDocumentSignerPosts = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(pdfDocumentSignerPostQM);
                            if(pdfDocumentSignerPosts.size()>0){
                                for(PDF_DOCUMENT_SIGNER_POST pdfDocumentSignerPost : pdfDocumentSignerPosts){
                                    DOCUMENT_SIGNER documentSigner = new DOCUMENT_SIGNER();
                                    documentSigner.setCreated(new Date());
                                    documentSigner.setDeleted(false);
                                    documentSigner.setPost(pdfDocumentSignerPost.getPost());
                                    documentSigner.setDocument(document);
                                    DOCUMENT_SIGNER_STATUS documentSignerStatus = WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.CREATED);
                                    documentSigner.setDocumentSignerStatus(documentSignerStatus);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(documentSigner);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        CommonUtils.showSavedNotification();
                        CreateViewDialog createViewDialog = new CreateViewDialog(CreateView.this , getViewName(), document, pdfDocumentSignerPosts);
                        createViewDialog.open();
                    }

                }else{
                    Message.showError(getUILocaleUtil().getMessage("choose.document"));
                }
            }
        });
        firstVL.addComponent(btnCreate);
        firstVL.setComponentAlignment(btnCreate, Alignment.MIDDLE_CENTER);

        try {
            pdfDocumentFilterPanel = createPdfDocumentFilterPanel();
            firstVL.addComponent(pdfDocumentFilterPanel);
            firstVL.setComponentAlignment(pdfDocumentFilterPanel, Alignment.TOP_CENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        firstVL.addComponent(pdfDocumentGW);

        mainHSP.setFirstComponent(firstVL);
        mainHSP.setSecondComponent(secondVL);
        getContent().addComponent(mainHSP);
        doFilter(pdfDocumentFilterPanel.getFilterBean());
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(pdfDocumentGW)) {
            PDF_DOCUMENT pdfDocument = (PDF_DOCUMENT) pdfDocumentGW.getSelectedEntity();
            secondVL.removeAllComponents();
            if(pdfDocument!=null){

                pdfDocSignerPostGW = new GridWidget(PDF_DOCUMENT_SIGNER_POST.class);
                DBGridModel pdfDocSignerPostGM = (DBGridModel) pdfDocSignerPostGW.getWidgetModel();
                pdfDocSignerPostGM.getQueryModel().addWhere("pdfDocument", ECriteria.EQUAL , pdfDocument.getId());
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);

                secondVL.addComponent(pdfDocSignerPostGW);
            }
        }
    }

    public PdfDocumentFilterPanel createPdfDocumentFilterPanel() {
        PdfDocumentFilterPanel pdfDocumentFilterPanel = new PdfDocumentFilterPanel(new FPdfDocumentFilter());
        pdfDocumentFilterPanel.addFilterPanelListener(this);
        pdfDocumentFilterPanel.setImmediate(true);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        pdfDocumentFilterPanel.addFilterComponent("fileName", tf);

        List<PDF_DOCUMENT_TYPE> pdfDocumentTypes = null;
        try{
            QueryModel<PDF_DOCUMENT_TYPE>documentTypeQueryModel = new QueryModel<>(PDF_DOCUMENT_TYPE.class);
            pdfDocumentTypes = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentTypeQueryModel);
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog(e.getMessage(), e);
        }
        ComboBox documentTypesCB = new ComboBox();
        BeanItemContainer bic = new BeanItemContainer(PDF_DOCUMENT_TYPE.class,pdfDocumentTypes);
        documentTypesCB.setContainerDataSource(bic);
        pdfDocumentFilterPanel.addFilterComponent("pdfDocumentType", documentTypesCB);

        return pdfDocumentFilterPanel;
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

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FPdfDocumentFilter sf = (FPdfDocumentFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        List<PDF_DOCUMENT> list = new ArrayList<>();
        String sql = "select id, user_id, title, file_name, deleted, period, created, for_students from pdf_document where deleted = false " ;
        if(((FPdfDocumentFilter) abstractFilterBean).getFileName()!=null && !((FPdfDocumentFilter) abstractFilterBean).getFileName().trim().equals("")){
            sql += " and file_name ilike '" + sf.getFileName().trim() + "%' ";
        }
        if(((FPdfDocumentFilter) abstractFilterBean).getPdfDocumentType()!=null){
            sql += " and PDF_DOCUMENT_TYPE_ID = " + sf.getPdfDocumentType().getId().getId().longValue() + " ";
        }
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupItemsList(
                            sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    PDF_DOCUMENT pdfDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(PDF_DOCUMENT.class, ID.valueOf((long) oo[0]));
                    list.add(pdfDoc);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load pdf_document list", ex);
        }

        ((DBGridModel) pdfDocumentGW.getWidgetModel()).setEntities(list);
        try {
            pdfDocumentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh pdf_document grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        doFilter(pdfDocumentFilterPanel.getFilterBean());
    }
}
