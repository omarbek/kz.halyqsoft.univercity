package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.OpenPdfDialog;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.SignDocumentViewDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class InOnAgreeView extends BaseView{

    private USERS currentUser;
    private GridWidget inOnAgreeDocsGW;

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
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS).getId());
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());
        DBGridModel dbGridModel = (DBGridModel) inOnAgreeDocsGW.getWidgetModel();
        QueryModel inOnSignDocsQM = dbGridModel.getQueryModel();
        FromItem fi = inOnSignDocsQM.addJoin(EJoin.INNER_JOIN, "id", DOCUMENT_SIGNER.class , "document");
        inOnSignDocsQM.addWhereIn("documentStatus" , ids);
        inOnSignDocsQM.addWhereAnd(fi , "employee", ECriteria.EQUAL , CommonUtils.getCurrentUser().getId());

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button previewBtn = new Button(getUILocaleUtil().getCaption("preview"));
        previewBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(inOnAgreeDocsGW.getSelectedEntity()!=null){
                    OpenPdfDialog openPdfDialog = new OpenPdfDialog((DOCUMENT) inOnAgreeDocsGW.getSelectedEntity(),700,700);
                    openPdfDialog.addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent closeEvent) {
                            try{
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

}
