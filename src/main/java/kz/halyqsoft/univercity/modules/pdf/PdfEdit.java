package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.Property;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PDF_DOCUMENT_TYPE;
import kz.halyqsoft.univercity.modules.pdf.dialogs.CustomFieldsView;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.FieldValidator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.Calendar;

public class PdfEdit extends AbstractCommonView {

    private StreamResource myResource;
    private ArrayList<CustomField> customFieldList = new ArrayList<>();
    private StreamResource.StreamSource streamSource;
    private BrowserWindowOpener opener;
    private Button openPdfButton = new Button(getUILocaleUtil().getCaption("open"));
    private Embedded pdfEmbedded;
    private Object prevClassWithEmbedded;
    private PDF_DOCUMENT mainFile;
    public PdfEdit(PDF_DOCUMENT file, Object prevClassWithEmbedded) {
        openPdfButton.setEnabled(false);
        this.prevClassWithEmbedded = prevClassWithEmbedded;
        try {
            addOrEdit(file);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    private void addOrEdit(PDF_DOCUMENT fileDoc) throws Exception {
        mainFile = fileDoc;
        CustomField cf = new CustomField();
        Button addComponentButton = new Button("+");
        Button createDbButton = CommonUtils.createSaveButton();
        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setResponsive(true);
        mainHL.setImmediate(true);
        mainHL.setSpacing(true);

        VerticalLayout itemsVL = new VerticalLayout();
        itemsVL.setResponsive(true);
        itemsVL.setImmediate(true);

        CustomField customField = new CustomField();
        HorizontalLayout activityHL = new HorizontalLayout();

        myResource = createResource(cf);
        myResource.setMIMEType("application/pdf");
        opener = new BrowserWindowOpener(myResource);
        opener.extend(openPdfButton);

        final double[] order = {1};

        if(fileDoc.getId() != null){
            QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
            propertyQM.addWhere("pdfDocument", ECriteria.EQUAL, fileDoc.getId());
            propertyQM.addSelect("orderNumber", EAggregate.MAX);
            try {
                Object pdfProperty = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookupItems(propertyQM);
                order[0] = Double.parseDouble(pdfProperty.toString());
                customField.getOrder().setValue(Double.toString(order[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else{
            order[0] = 1;
        }


        cf.pdfTitle.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });
        mainHL.addComponent(cf.pdfTitle);
        cf.title.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });

        cf.deadlineDays.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });

        cf.forStudentsCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });

        cf.pdfDocumentTypeComboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });

        mainHL.addComponent(cf.title);
        mainHL.addComponent(cf.deadlineDays);
        mainHL.addComponent(cf.pdfDocumentTypeComboBox);
        mainHL.addComponent(cf.forStudentsCheckBox);
        itemsVL.addComponent(mainHL);
        itemsVL.setComponentAlignment(mainHL, Alignment.MIDDLE_CENTER);

        addComponentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                CustomField customField = new CustomField();

                HorizontalLayout textHL = new HorizontalLayout();
                textHL.setImmediate(true);
                HorizontalLayout textAreaHL = new HorizontalLayout();
                textAreaHL.setImmediate(true);

                Button deleteHLButton = new Button("-");
                TextArea textArea = customField.getTextField();
                setTextArea(textArea, textAreaHL);

                ComboBox fontComboBox = customField.getFontComboBox();
                fontComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });
                fontComboBox.setValue("Normal");
                textHL.addComponent(fontComboBox);

                ComboBox xComboBox = customField.getxComboBox();
                xComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });
                xComboBox.setValue(6);

                textHL.addComponent(xComboBox);
                ComboBox yComboBox = customField.getyComboBox();
                yComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });
                yComboBox.setValue(0);

                textHL.addComponent(yComboBox);
                ComboBox textSizeComboBox = customField.getTextSizeComboBox();
                textSizeComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });
                textSizeComboBox.setValue(12);
                textHL.addComponent(textSizeComboBox);

                setTextField(customField.getOrder(),textHL);

                CheckBox centerCheckBox = customField.getCenterCheckBox();
                CheckBox rightCheckBox = customField.getRightCheckBox();
                CheckBox customCheckBox = customField.getCustomCheckBox();

                customCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        if(customCheckBox.getValue()){
                            if(FieldValidator.isNotEmpty(textArea.getValue()))
                            {
                                if(!textArea.getValue().toLowerCase().startsWith("$userinput")){
                                    textArea.setValue("userinput");
                                }
                            }else{
                                textArea.setValue("userinput");
                            }
                            textArea.setEnabled(false);
                        }else{
                            textArea.setValue("");
                            textArea.setEnabled(true);
                        }
                    }
                });

                centerCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                        if(centerCheckBox.getValue()){
                            xComboBox.setValue(0);
                            xComboBox.setEnabled(false);
                            rightCheckBox.setValue(false);
                        }
                        else{
                            xComboBox.setValue(6);
                            xComboBox.setEnabled(true);}

                    }
                });

                rightCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                        if(rightCheckBox.getValue()){
                            xComboBox.setValue(0);
                            xComboBox.setEnabled(false);
                            centerCheckBox.setValue(false);
                        }
                        else{
                            xComboBox.setValue(6);
                            xComboBox.setEnabled(true);}
                    }
                });

                deleteHLButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                        if(customFieldList.size()>1){
                            itemsVL.removeComponent(textHL);
                            itemsVL.removeComponent(textAreaHL);
                            customFieldList.remove(customField);
                            HorizontalLayout tempHL = (HorizontalLayout) itemsVL.getComponent(itemsVL.getComponentCount()-1);
                            if(tempHL.getComponentIndex(addComponentButton)<0){
                                tempHL.addComponent(addComponentButton);
                                tempHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);
                            }
                        }
                        refresh(cf);
                    }
                });


                textHL.addComponent(centerCheckBox);
                textHL.addComponent(rightCheckBox);
                textHL.addComponent(customCheckBox);

                textHL.setComponentAlignment(centerCheckBox,Alignment.BOTTOM_RIGHT);
                textHL.setComponentAlignment(rightCheckBox,Alignment.BOTTOM_RIGHT);
                textHL.setComponentAlignment(customCheckBox,Alignment.BOTTOM_RIGHT);

                textHL.addComponent(deleteHLButton);
                textHL.setData(textHL);
                textHL.setComponentAlignment(deleteHLButton,Alignment.BOTTOM_CENTER);

                textHL.addComponent(addComponentButton);
                textHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);


                order[0]++;
                customField.getOrder().setValue(Double.toString(order[0]));

                customFieldList.add(customField);

                itemsVL.addComponent(textAreaHL);
                itemsVL.addComponent(textHL);

                itemsVL.setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
                itemsVL.setComponentAlignment(textAreaHL, Alignment.MIDDLE_CENTER);


            }

            private void setTextField(TextField mainTF, HorizontalLayout textHL) {
                mainTF.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });
                textHL.addComponent(mainTF);
            }

            private void setTextArea(TextArea mainTF, HorizontalLayout textHL) {
                mainTF.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });
                textHL.addComponent(mainTF);
            }

        });

        createDbButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                CustomDocument dc = new CustomDocument();

                if(checkForEmpty(cf)){
                    Message.showError(getUILocaleUtil().getMessage("pdf.field.empty"));
                }
                else{

                    ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();

                    USERS user = CommonUtils.getCurrentUser();

                    if (user != null) {
                        try {
                            if (fileDoc.getId() == null) {

                                if(!cf.pdfTitle.getValue().endsWith(".pdf"))
                                {
                                    fileDoc.setFileName(cf.pdfTitle.getValue() + ".pdf");
                                }else{
                                    fileDoc.setFileName(cf.pdfTitle.getValue());
                                }
                                fileDoc.setTitle(cf.title.getValue());
                                fileDoc.setPeriod(Integer.parseInt(cf.deadlineDays.getValue()));
                                fileDoc.setForStudents(cf.getForStudentsCheckBox().getValue());
                                fileDoc.setPdfDocumentType((PDF_DOCUMENT_TYPE) cf.getPdfDocumentTypeComboBox().getValue());
                                fileDoc.setUser(user);
                                fileDoc.setDeleted(false);
                                fileDoc.setCreated(new Date());



                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(fileDoc);


                                List<CustomField> customFields = new ArrayList<>();
                                for(CustomField singleCF : customFieldList){
                                    if(singleCF.getCustomCheckBox().getValue()){
                                        customFields.add(singleCF);
                                    }
                                }

                                if(customFields.size()>0){
                                    CustomFieldsView view = new CustomFieldsView(getUILocaleUtil().getCaption("text.custom"), customFields, fileDoc);
                                }
                                dc.initialize(customFieldList, cf.getTitle().getValue());
                                dc.createPdf(cf.getTitle().getValue());


                                if (dc.getPdfProperties() != null) {
                                    for (PDF_PROPERTY pdfProperty : dc.getPdfProperties()) {
                                        pdfProperty.setPdfDocument(fileDoc);
                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfProperty);
                                    }
                                }
                            }
                            else {

                                if(!cf.pdfTitle.getValue().endsWith(".pdf"))
                                {
                                    fileDoc.setFileName(cf.pdfTitle.getValue() + ".pdf");
                                }else{
                                    fileDoc.setFileName(cf.pdfTitle.getValue());
                                }
                                fileDoc.setPeriod(Integer.parseInt(cf.deadlineDays.getValue()));
                                fileDoc.setForStudents(cf.getForStudentsCheckBox().getValue());
                                fileDoc.setPdfDocumentType((PDF_DOCUMENT_TYPE) cf.getPdfDocumentTypeComboBox().getValue());
                                fileDoc.setTitle(cf.title.getValue());

                                QueryModel<PDF_PROPERTY> pdfPropertyQM = new QueryModel<>(PDF_PROPERTY.class);
                                pdfPropertyQM.addWhere("pdfDocument", ECriteria.EQUAL, fileDoc.getId());

                                List<PDF_PROPERTY> pdfProperties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(pdfPropertyQM);
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(pdfProperties);

                                List<CustomField> customFields = new ArrayList<>();
                                for(CustomField singleCF : customFieldList){
                                    if(singleCF.getCustomCheckBox().getValue()){
                                        customFields.add(singleCF);
                                    }
                                }

                                if(customFields.size()>0){
                                    CustomFieldsView view = new CustomFieldsView(getUILocaleUtil().getCaption("text.custom"), customFields, fileDoc);
                                }

                                dc.initialize(customFieldList, cf.getTitle().getValue());
                                dc.createPdf( cf.getTitle().getValue());


                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(fileDoc);
                                if (dc.getPdfProperties() != null) {
                                    for (PDF_PROPERTY pdfProperty : dc.getPdfProperties()) {
                                        pdfProperty.setPdfDocument(fileDoc);
                                        if(pdfProperty.getId() != null){
                                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pdfProperty);
                                        }
                                        else {
                                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfProperty);
                                        }
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }}
                    CommonUtils.showSavedNotification();

                    refreshEmbedded();
                }
            }


        });

        if (fileDoc.getId() != null) {
            QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
            propertyQM.addWhere("pdfDocument", ECriteria.EQUAL, fileDoc.getId());
            propertyQM.addOrder("orderNumber");
            List<PDF_PROPERTY> properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(propertyQM);

            for (PDF_PROPERTY property : properties) {

                CustomField customFieldProp = new CustomField();
                HorizontalLayout textAreaHL = new HorizontalLayout();
                textAreaHL.setImmediate(true);
                HorizontalLayout textHL = new HorizontalLayout();
                textHL.setImmediate(true);
                Button deleteHLButton = new Button("-");

                customFieldProp.getTextField().setValue(property.getText());
                customFieldProp.setId(property.getId());

                TextArea textArea = customFieldProp.getTextField();
                setTextArea(textArea, textAreaHL,cf);

                ComboBox fontComboBox = customFieldProp.getFontComboBox();
                fontComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(customFieldProp);
                    }
                });

                fontComboBox.setValue(property.getFont());
                textHL.addComponent(fontComboBox);

                ComboBox xComboBox = customFieldProp.getxComboBox();
                xComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(customFieldProp);
                    }
                });
                xComboBox.setValue(Math.round(property.getX()));
                textHL.addComponent(xComboBox);

                customFieldProp.getyComboBox().addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                    }
                });

                customFieldProp.getyComboBox().setValue((Math.round(property.getY())));
                textHL.addComponent(customFieldProp.getyComboBox());
                ComboBox textSizeComboBox = customFieldProp.getTextSizeComboBox();
                textSizeComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(customFieldProp);
                    }
                });


                textSizeComboBox.setValue(property.getSize());
                textHL.addComponent(textSizeComboBox);

                customFieldProp.getOrder().setValue(String.valueOf(property.getOrderNumber()));
                setTextField(customFieldProp.getOrder(),textHL,cf);

                CheckBox centerCheckBox = customFieldProp.getCenterCheckBox();

                CheckBox rightCheckBox = customFieldProp.getRightCheckBox();

                CheckBox customCheckBox = customFieldProp.getCustomCheckBox();
                customCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        if(customCheckBox.getValue()){
                            if(FieldValidator.isNotEmpty(textArea.getValue()))
                            {
                                if(!textArea.getValue().toLowerCase().startsWith("$userinput")){
                                    textArea.setValue("userinput");
                                }
                            }else{
                                textArea.setValue("userinput");
                            }
                            textArea.setEnabled(false);
                        }else{
                            textArea.setValue("");
                            textArea.setEnabled(true);
                        }
                    }
                });

                if(centerCheckBox.getValue()){
                    xComboBox.setValue(0);
                    xComboBox.setReadOnly(true);
                    rightCheckBox.setValue(false);
                }
                else{
                    xComboBox.setReadOnly(false);
                }
                centerCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                        if(centerCheckBox.getValue()){
                            xComboBox.setValue(0);
                            xComboBox.setEnabled(false);
                            rightCheckBox.setValue(false);
                        }
                        else{
                            xComboBox.setEnabled(true);
                            xComboBox.setReadOnly(false);
                        }
                    }
                });


                if(rightCheckBox.getValue()){
                    xComboBox.setValue(0);
                    xComboBox.setReadOnly(true);
                    centerCheckBox.setValue(false);
                }
                else{
                    xComboBox.setReadOnly(false);
                }
                rightCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                        if(rightCheckBox.getValue()){
                            xComboBox.setValue(0);
                            xComboBox.setEnabled(false);
                            centerCheckBox.setValue(false);
                        }
                        else{
                            xComboBox.setEnabled(true);
                            xComboBox.setReadOnly(false);
                        }
                    }
                });


                deleteHLButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if(customFieldList.size()>1){
                            itemsVL.removeComponent(textHL);
                            itemsVL.removeComponent(textAreaHL);
                            customFieldList.remove(customField);
                            HorizontalLayout tempHL = (HorizontalLayout) itemsVL.getComponent(itemsVL.getComponentCount()-1);
                            if(tempHL.getComponentIndex(addComponentButton)<0){
                                tempHL.addComponent(addComponentButton);
                                tempHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);
                            }
                        }

                    }
                });

                customCheckBox.setValue(property.isCustom());
                centerCheckBox.setValue(property.isCenter());
                rightCheckBox.setValue(property.isRight());

                customFieldList.add(customFieldProp);

                textHL.addComponent(centerCheckBox);
                textHL.setComponentAlignment(centerCheckBox,Alignment.BOTTOM_RIGHT);

                textHL.addComponent(rightCheckBox);
                textHL.setComponentAlignment(rightCheckBox,Alignment.BOTTOM_RIGHT);

                textHL.addComponent(customCheckBox);
                textHL.setComponentAlignment(customCheckBox,Alignment.BOTTOM_RIGHT);

                textHL.addComponent(deleteHLButton);
                textHL.setData(textHL);
                textHL.setComponentAlignment(deleteHLButton,Alignment.BOTTOM_CENTER);

                textHL.addComponent(addComponentButton);
                textHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);

                textHL.setData(textHL);
                itemsVL.addComponent(textAreaHL);
                itemsVL.addComponent(textHL);
                itemsVL.setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
                itemsVL.setComponentAlignment(textAreaHL, Alignment.MIDDLE_CENTER);

            }
            cf.pdfTitle.setValue(fileDoc.getFileName());
            cf.forStudentsCheckBox.setValue(fileDoc.isForStudents());
            cf.deadlineDays.setValue(fileDoc.getPeriod()+"");
            cf.pdfDocumentTypeComboBox.setValue(fileDoc.getPdfDocumentType());
            cf.title.setValue(fileDoc.getTitle());
        }
        else {

            HorizontalLayout textAreaHL = new HorizontalLayout();
            textAreaHL.setImmediate(true);
            HorizontalLayout textHL = new HorizontalLayout();
            textHL.setImmediate(true);


            TextArea textArea =customField.getTextField();
            setTextArea(textArea, textAreaHL,cf);

            ComboBox fontComboBox = customField.getFontComboBox();
            fontComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                }
            });

            fontComboBox.setValue("Normal");
            textHL.addComponent(fontComboBox);

            ComboBox xComboBox = customField.getxComboBox();
            xComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                }
            });
            xComboBox.setValue(6);
            textHL.addComponent(xComboBox);
            ComboBox yComboBox = customField.getyComboBox();
            yComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                }
            });
            yComboBox.setValue(0);
            textHL.addComponent(yComboBox);
            ComboBox textSizeComboBox = customField.getTextSizeComboBox();
            textSizeComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                }
            });
            textSizeComboBox.setValue(12);
            textHL.addComponent(textSizeComboBox);

            setTextField(customField.getOrder(),textHL,cf);
            customField.getOrder().setValue(Double.toString(1));

            CheckBox centerCheckBox = customField.getCenterCheckBox();
            CheckBox rightCheckBox = customField.getRightCheckBox();
            CheckBox customCheckBox = customField.getCustomCheckBox();

            customCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    if(customCheckBox.getValue()){
                        if(FieldValidator.isNotEmpty(textArea.getValue()))
                        {
                            if(!textArea.getValue().toLowerCase().startsWith("$userinput")){
                                textArea.setValue("userinput");
                            }
                        }else{
                            textArea.setValue("userinput");
                        }
                        textArea.setEnabled(false);
                    }else{
                        textArea.setValue("");
                        textArea.setEnabled(true);
                    }
                }
            });

            centerCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                    if(centerCheckBox.getValue()){
                        xComboBox.setValue(0);
                        xComboBox.setEnabled(false);
                        rightCheckBox.setValue(false);
                    }
                    else{
                        xComboBox.setValue(6);
                        xComboBox.setEnabled(true);}
                }
            });

            rightCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                    if(rightCheckBox.getValue()){
                        xComboBox.setValue(0);
                        xComboBox.setEnabled(false);
                        centerCheckBox.setValue(false);
                    }
                    else{
                        xComboBox.setValue(6);
                        xComboBox.setEnabled(true);}
                }
            });

            textHL.addComponent(centerCheckBox);
            textHL.addComponent(rightCheckBox);
            textHL.addComponent(customCheckBox);

            customFieldList.add(customField);

            textHL.addComponent(addComponentButton);
            textHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);
            textHL.setComponentAlignment(centerCheckBox,Alignment.BOTTOM_RIGHT);
            textHL.setComponentAlignment(rightCheckBox,Alignment.BOTTOM_RIGHT);
            textHL.setComponentAlignment(customCheckBox,Alignment.BOTTOM_RIGHT);

            itemsVL.addComponent(textAreaHL);
            itemsVL.addComponent(textHL);
            itemsVL.setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
            itemsVL.setComponentAlignment(textAreaHL, Alignment.MIDDLE_CENTER);


        }

        getContent().addComponent(itemsVL);
        getContent().setComponentAlignment(itemsVL, Alignment.MIDDLE_CENTER);

        activityHL.addComponent(createDbButton);
        activityHL.addComponent(openPdfButton);

        getContent().addComponent(activityHL);
        getContent().setComponentAlignment(activityHL, Alignment.MIDDLE_CENTER);

    }

    private boolean checkForEmpty(CustomField cf) {
        for(CustomField customField: customFieldList){
            if(customField.getTextSizeComboBox().getValue() == null || customField.getTextField().isEmpty()
                    || cf.getTitle().isEmpty() || cf.getPdfTitle().isEmpty()
                    || customField.getFontComboBox().getValue() == null
                    || customField.getxComboBox().getValue() == null || customField.getyComboBox().getValue() == null
                    || customField.getOrder().isEmpty() || cf.getDeadlineDays().isEmpty()){
                return true;
            }

        }
        return false;
    }

    private boolean checkForEmpty(CustomField customField, CustomField cf) {

        return customField.getTextSizeComboBox().getValue() == null || customField.getTextField().isEmpty()
                || cf.getTitle().isEmpty() || cf.getPdfTitle().isEmpty()
                || customField.getFontComboBox().getValue() == null
                || customField.getxComboBox().getValue() == null || customField.getyComboBox().getValue() == null ||
                customField.getOrder().isEmpty()|| cf.getDeadlineDays().isEmpty();
    }


    private void refresh(CustomField cf) {

        if (customFieldList.isEmpty()) {
            return;
        }
        for (CustomField customField : customFieldList) {
            if (checkForEmpty(customField, cf)) {
                return;
            }
        }
        openPdfButton.setEnabled(true);
        CustomDocument dc = new CustomDocument();
        String fileName = cf.getTitle().getValue();
        dc.initialize(customFieldList, fileName);
        // dc.createPdf(fileName);
        ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
        streamSource = new CustomSource(byteArrayOutputStream);
        myResource.setStreamSource(streamSource);

        String filename = getFileName(cf);
        myResource.setFilename(filename);
        opener.setResource(myResource);
    }

    private void refreshEmbedded(){
        if(mainFile.getId()!=null){
            if(prevClassWithEmbedded instanceof PdfGenerationPart){
                ((PdfGenerationPart) prevClassWithEmbedded).createEmbedded();
                pdfEmbedded = ((PdfGenerationPart) prevClassWithEmbedded).getPdfEmbedded();
                if(pdfEmbedded!=null){
                    pdfEmbedded.setSource(EmployeePdfCreator.getStreamResourceFromPdfDocument(mainFile));
                }
            }
        }
    }

    private void setTextArea(TextArea mainTF, HorizontalLayout textHL, CustomField cf) {
        mainTF.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });
        textHL.addComponent(mainTF);
    }

    private void setTextField(TextField mainTF, HorizontalLayout textHL, CustomField cf) {
        mainTF.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });
        textHL.addComponent(mainTF);
    }

    private String getFileName(CustomField cf) {
        String timeInMillisWithPdf = Calendar.getInstance().getTimeInMillis() + ".pdf";
        String filename;
        if (!cf.getPdfTitle().getValue().isEmpty()) {
            filename = cf.getPdfTitle().getValue() + "_" + timeInMillisWithPdf;
        } else {
            filename = timeInMillisWithPdf;
        }
        return filename;
    }

    private StreamResource createResource(CustomField cf) {
        return new StreamResource(streamSource, getFileName(cf));
    }

    @Override
    public String getViewName() {
        return getUILocaleUtil().getCaption("pdf.save");
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("pdf.edit");
    }

    @Override
    public void initView(boolean b) throws Exception {
    }
}

