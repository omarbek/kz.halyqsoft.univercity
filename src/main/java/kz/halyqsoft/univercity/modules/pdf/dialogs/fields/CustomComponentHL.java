package kz.halyqsoft.univercity.modules.pdf.dialogs.fields;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_USER_INPUT;
import kz.halyqsoft.univercity.modules.pdf.CustomField;
import kz.halyqsoft.univercity.utils.CommonUtils;

public class CustomComponentHL extends HorizontalLayout{

    private VerticalLayout mainVL;
    private CustomField customField;
    private TextField valueTF;
    private TextArea descriptionTA;

    public CustomComponentHL(CustomField customField, int order){

        setImmediate(true);
        setSizeFull();

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);
        this.customField = customField;

        valueTF = new TextField();
        valueTF.setCaption(CommonUtils.getUILocaleUtil().getEntityFieldLabel(DOCUMENT_USER_INPUT.class, "value"));
        valueTF.setValue("$userinput" + order);

        customField.getTextField().setValue(valueTF.getValue());

        valueTF.setEnabled(false);
        valueTF.setRequired(true);
        valueTF.setImmediate(true);
        valueTF.setSizeFull();

        descriptionTA = new TextArea();
        descriptionTA.setCaption(CommonUtils.getUILocaleUtil().getEntityFieldLabel(DOCUMENT_USER_INPUT.class, "description"));
        descriptionTA.setRequired(true);
        descriptionTA.setImmediate(true);
        descriptionTA.setSizeFull();

        mainVL.addComponent(valueTF);
        mainVL.addComponent(descriptionTA);

        mainVL.setSizeFull();

        addComponent(mainVL);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public CustomField getCustomField() {
        return customField;
    }

    public TextField getValueTF() {
        return valueTF;
    }

    public TextArea getDescriptionTA() {
        return descriptionTA;
    }
}
