package kz.halyqsoft.univercity.modules.pdf.dialogs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_USER_INPUT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.modules.pdf.CustomField;
import kz.halyqsoft.univercity.modules.pdf.dialogs.fields.CustomComponentHL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.FieldValidator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomFieldsView extends AbstractDialog{
    private String title;
    private List<CustomComponentHL> customComponentHLS = new ArrayList<>();
    public CustomFieldsView(String title, List<CustomField> customFields, PDF_DOCUMENT fileDoc){
        setClosable(false);
        this.title = title;

        for(int i = 0 ; i < customFields.size(); i++){
            CustomComponentHL customComponentHL = new CustomComponentHL(customFields.get(i), i);

        }

        for(int i = 0 ; i < customFields.size(); i++){
            CustomComponentHL customComponentHL = new CustomComponentHL(customFields.get(i), i);
            customComponentHLS.add(customComponentHL);
            getContent().addComponent(customComponentHL);
            getContent().setComponentAlignment(customComponentHL, Alignment.MIDDLE_CENTER);
        }


        Button saveBtn = CommonUtils.createSaveButton();
        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                for(CustomComponentHL customComponentHL : customComponentHLS){
                    if(!FieldValidator.isNotEmpty(customComponentHL.getDescriptionTA().getValue()) && FieldValidator.isNotEmpty(customComponentHL.getValueTF().getValue())){

                        Message.showError(getUILocaleUtil().getMessage("pdf.field.empty"));
                        return;
                    }
                }

                List<DOCUMENT_USER_INPUT> documentUserInputs = new ArrayList<>();
                for(CustomComponentHL customComponentHL : customComponentHLS){

                        DOCUMENT_USER_INPUT documentUserInput = new DOCUMENT_USER_INPUT();
                        documentUserInput.setDescription(customComponentHL.getDescriptionTA().getValue());
                        documentUserInput.setValue(customComponentHL.getValueTF().getValue());
                        documentUserInput.setPdfDocument(fileDoc);
                        documentUserInput.setCreated(new Date());
                        documentUserInputs.add(documentUserInput);

                }

                QueryModel<DOCUMENT_USER_INPUT> documentUserInputQM = new QueryModel<>(DOCUMENT_USER_INPUT.class);
                documentUserInputQM.addWhere("pdfDocument", ECriteria.EQUAL , fileDoc.getId());

                try{
                    List<DOCUMENT_USER_INPUT> documentUserInputList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentUserInputQM);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(documentUserInputList);
                }catch (NoResultException nre){
                    System.out.println(nre.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }

                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(documentUserInputs);
                }catch (Exception e){
                    e.printStackTrace();
                }
                CommonUtils.showSavedNotification();
                close();
            }
        });
        getContent().addComponent(saveBtn);
        getContent().setComponentAlignment(saveBtn , Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return this.title;
    }
}
