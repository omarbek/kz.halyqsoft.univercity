package kz.halyqsoft.univercity.modules.workflow.views.fields;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_USER_INPUT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_USER_REAL_INPUT;
import kz.halyqsoft.univercity.modules.pdf.CustomField;
import kz.halyqsoft.univercity.utils.CommonUtils;

public class CustomComponentHL extends HorizontalLayout{

    private VerticalLayout mainVL;
    private HorizontalLayout mainHL;
    private TextField valueTF;
    private TextArea descriptionTA;
    private TextArea valueTA;
    private DOCUMENT_USER_INPUT documentUserInput;

    public CustomComponentHL(DOCUMENT_USER_INPUT documentUserInput){
        this.documentUserInput = documentUserInput;
        setImmediate(true);
        setSizeFull();

        mainHL = new HorizontalLayout();
        mainHL.setImmediate(true);
        mainHL.setSizeFull();

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        valueTF = new TextField();
        valueTF.setCaption(CommonUtils.getUILocaleUtil().getEntityFieldLabel(DOCUMENT_USER_INPUT.class, "value"));
        valueTF.setValue(documentUserInput.getValue());
        valueTF.setEnabled(false);
        valueTF.setImmediate(true);
        valueTF.setSizeFull();
        valueTF.setVisible(false);

        descriptionTA = new TextArea();
        descriptionTA.setCaption(CommonUtils.getUILocaleUtil().getEntityFieldLabel(DOCUMENT_USER_INPUT.class, "description"));
        descriptionTA.setImmediate(true);
        descriptionTA.setValue(documentUserInput.getDescription());
        descriptionTA.setEnabled(false);
        descriptionTA.setSizeFull();

        valueTA = new TextArea();
        valueTA.setCaption(CommonUtils.getUILocaleUtil().getEntityFieldLabel(DOCUMENT_USER_REAL_INPUT.class, "value"));
        valueTA.setRequired(true);
        valueTA.setImmediate(true);
        valueTA.setSizeFull();

        mainHL.addComponent(descriptionTA);
        mainHL.addComponent(valueTA);

        mainVL.addComponent(valueTF);
        mainVL.addComponent(mainHL);

        mainVL.setSizeFull();

        addComponent(mainVL);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public TextArea getValueTA() {
        return valueTA;
    }

    public TextField getValueTF() {
        return valueTF;
    }

    public TextArea getDescriptionTA() {
        return descriptionTA;
    }

    public HorizontalLayout getMainHL() {
        return mainHL;
    }

    public DOCUMENT_USER_INPUT getDocumentUserInput() {
        return documentUserInput;
    }
}
