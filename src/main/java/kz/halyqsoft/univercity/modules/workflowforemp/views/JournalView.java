package kz.halyqsoft.univercity.modules.workflowforemp.views;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.modules.workflow.views.InOnAgreeView;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.OpenPdfDialog;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

/**
 * @author Assylkhan
 * on 06.12.2018
 * @project kz.halyqsoft.univercity
 */
public class JournalView extends BaseView{
    private GridWidget documentGW;
    public JournalView(String title) {
        super(title);

        Button previewBtn = new Button(getUILocaleUtil().getCaption("preview"));
        previewBtn.setIcon(new ThemeResource("img/button/preview.png"));
        previewBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(documentGW.getSelectedEntity()!=null){
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

                            dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , documentGW.getSelectedEntity().getId());

                            getContent().addComponent(outMyDocsSignerGW);

                        }

                        @Override
                        protected String createTitle() {
                            init();
                            return getViewName();
                        }
                    };
                    abstractDialog.open();
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        documentGW = new GridWidget(DOCUMENT.class);
        documentGW.getToolbarPanel().setSizeUndefined();
        documentGW.setButtonVisible(IconToolbar.DELETE_BUTTON,false);
        documentGW.setButtonVisible(IconToolbar.EDIT_BUTTON,false);
        documentGW.setButtonVisible(IconToolbar.ADD_BUTTON,false);
        documentGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON,false);
        documentGW.setButtonVisible(IconToolbar.REFRESH_BUTTON,false);
        documentGW.getToolbarPanel().addComponent(previewBtn);

        DBGridModel documentGM = (DBGridModel) documentGW.getWidgetModel();
        FromItem fi = documentGM.getQueryModel().addJoin(EJoin.INNER_JOIN , "pdf_document_id" , PDF_DOCUMENT.class , "id");
        documentGM.getQueryModel().addWhere("documentStatus", ECriteria.EQUAL , DOCUMENT_STATUS.ACCEPTED_ID);
        documentGM.getQueryModel().addWhereAnd(fi,"forHumanResourceDepartment", ECriteria.EQUAL , true);
        documentGM.getFormModel().getFieldModel("deleted").setInView(true);
        documentGM.getFormModel().getFieldModel("deleted").setInEdit(true);
        documentGM.getColumnModel("deleted").setInGrid(true);


        getContent().addComponent(documentGW);
    }
}
