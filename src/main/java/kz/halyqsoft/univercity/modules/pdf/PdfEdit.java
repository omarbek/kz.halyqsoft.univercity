package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.Property;
import com.vaadin.sass.internal.util.StringUtil;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PdfEdit extends AbstractCommonView {

    private StreamResource myResource;
    private ArrayList<CustomField> customFieldList = new ArrayList<>();
    private StreamResource.StreamSource streamSource;
    private BrowserWindowOpener opener;
    private Button openPdfButton = new Button(getUILocaleUtil().getCaption("open"));

    public PdfEdit(PDF_DOCUMENT file) {
        openPdfButton.setEnabled(false);
        try {
            addOrEdit(file);

        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    private void addOrEdit(PDF_DOCUMENT fileDoc) throws Exception {
        CustomField cf = new CustomField();
        Button addComponentButton = new Button("+");
        Button createDbButton = CommonUtils.createSaveButton();
        HorizontalLayout mainHL = new HorizontalLayout();
        VerticalLayout itemsVL = new VerticalLayout();

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
        mainHL.addComponent(cf.title);

        cf.deadlineDays.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh(cf);
            }
        });
        mainHL.addComponent(cf.deadlineDays);

        itemsVL.addComponent(mainHL);
        itemsVL.setComponentAlignment(mainHL, Alignment.MIDDLE_CENTER);

            addComponentButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {

                    CustomField customField = new CustomField();

                    HorizontalLayout textHL = new HorizontalLayout();
                    HorizontalLayout textAreaHL = new HorizontalLayout();

                    Button deleteHLButton = new Button("-");
                    deleteHLButton.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            if(textHL.getComponentIndex(addComponentButton)==-1){
                                customFieldList.remove(customField);
                                itemsVL.removeComponent(textAreaHL);
                                itemsVL.removeComponent(textHL);
                            }else{
                                Message.showInfo("Can not delete!");
                            }
                        }
                    });

                    setTextArea(customField.getTextField(), textAreaHL);

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
                    centerCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            refresh(cf);
                            if(centerCheckBox.getValue()){
                                xComboBox.setValue(0);
                                xComboBox.setEnabled(false);
                            }
                            else{
                                xComboBox.setValue(6);
                                xComboBox.setEnabled(true);}

                        }
                    });

                        textHL.addComponent(centerCheckBox);



                        order[0]++;
                    customField.getOrder().setValue(Double.toString(order[0]));

                    customFieldList.add(customField);
                    textHL.addComponent(addComponentButton);

                    itemsVL.addComponent(textAreaHL);
                    itemsVL.addComponent(textHL);

                    textHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);
                    textHL.addComponent(deleteHLButton);
                    textHL.setData(textHL);
                    textHL.setComponentAlignment(deleteHLButton,Alignment.BOTTOM_CENTER);
                    textHL.setComponentAlignment(centerCheckBox,Alignment.BOTTOM_RIGHT);

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
                    }else if(!NumberUtils.isDigits(cf.deadlineDays.getValue())){
                        Message.showError(getUILocaleUtil().getMessage("pdf.fields.correct"));
                    }else{
                    dc.initialize(customFieldList, cf.getTitle().getValue());

                    ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
                    byte[] file = byteArrayOutputStream.toByteArray();
                QueryModel<USERS> userQM = new QueryModel<>(USERS.class);
                String currentUserLogin = CommonUtils.getCurrentUserLogin();
                userQM.addWhere("login", ECriteria.EQUAL, currentUserLogin);
                USERS user = null;

                try {
                    user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userQM);

                } catch (NoResultException e) {
                    user = null;
                } catch (Exception e) {
                    //todo
                }
                if (user != null) {

                    try {
                        PDF_DOCUMENT pdfDocument = new PDF_DOCUMENT();
                           if (fileDoc.getId() == null) {

                            pdfDocument.setFileName(cf.pdfTitle.getValue() + ".pdf");
                            pdfDocument.setTitle(cf.title.getValue());
                            pdfDocument.setPeriod(Integer.parseInt(cf.deadlineDays.getValue()));
                            pdfDocument.setFileByte(file);
                            pdfDocument.setUser(user);
                            pdfDocument.setDeleted(false);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfDocument);
                            if (dc.getPdfProperties() != null) {
                                for (PDF_PROPERTY pdfProperty : dc.getPdfProperties()) {
                                    pdfProperty.setPdfDocument(pdfDocument);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfProperty);
                                }
                            }
                        }
                        else {
                            pdfDocument.setId(fileDoc.getId());
                            pdfDocument.setFileName(cf.pdfTitle.getValue());
                            pdfDocument.setTitle(cf.title.getValue());
                            pdfDocument.setPeriod(Integer.parseInt(cf.deadlineDays.getValue()));
                            pdfDocument.setFileByte(file);
                            pdfDocument.setUser(user);
                            pdfDocument.setDeleted(false);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pdfDocument);
                            if (dc.getPdfProperties() != null) {
                                for (PDF_PROPERTY pdfProperty : dc.getPdfProperties()) {

                                    pdfProperty.setPdfDocument(pdfDocument);
                                    if(pdfProperty.getId() != null){
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pdfProperty);}
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
                HorizontalLayout textHL = new HorizontalLayout();
                Button deleteHLButton = new Button("-");

                customFieldProp.getTextField().setValue(property.getText());
                customFieldProp.setId(property.getId());

                setTextArea(customFieldProp.getTextField(), textAreaHL,cf);

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
                centerCheckBox.setValue(property.isCenter());
                if(centerCheckBox.getValue()){
                    xComboBox.setValue(0);
                    xComboBox.setReadOnly(true);
                }
                else{
                    xComboBox.setReadOnly(false);}
                centerCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        refresh(cf);
                        if(centerCheckBox.getValue()){
                            xComboBox.setValue(0);
                            xComboBox.setEnabled(false);
                        }
                        else{
                            xComboBox.setEnabled(true);
                            xComboBox.setReadOnly(false);}
                    }
                });

                textHL.addComponent(centerCheckBox);

                customFieldList.add(customFieldProp);
                textHL.addComponent(addComponentButton);
                textHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);
                textHL.addComponent(deleteHLButton);
                textHL.setData(textHL);
                textHL.setComponentAlignment(deleteHLButton,Alignment.BOTTOM_CENTER);
                textHL.setComponentAlignment(centerCheckBox,Alignment.BOTTOM_RIGHT);

                itemsVL.addComponent(textAreaHL);
                itemsVL.addComponent(textHL);
                itemsVL.setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
                itemsVL.setComponentAlignment(textAreaHL, Alignment.MIDDLE_CENTER);

            }
            cf.pdfTitle.setValue(fileDoc.getFileName());
            cf.title.setValue(fileDoc.getTitle());
            cf.deadlineDays.setValue(fileDoc.getPeriod()+"");
        }
        else {

            HorizontalLayout textAreaHL = new HorizontalLayout();
            HorizontalLayout textHL = new HorizontalLayout();

            setTextArea(customField.getTextField(), textAreaHL,cf);

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
            centerCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    refresh(cf);
                    if(centerCheckBox.getValue()){
                        xComboBox.setValue(0);
                        xComboBox.setEnabled(false);
                    }
                    else{
                        xComboBox.setValue(6);
                        xComboBox.setEnabled(true);}
                }
            });
            textHL.addComponent(centerCheckBox);

            customFieldList.add(customField);

            textHL.addComponent(addComponentButton);
            textHL.setComponentAlignment(addComponentButton,Alignment.BOTTOM_CENTER);
            textHL.setComponentAlignment(centerCheckBox,Alignment.BOTTOM_RIGHT);

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
            if(customField.getTextSizeComboBox().getValue() == null
                    || customField.getTextField().isEmpty()
                    || cf.getTitle().isEmpty()
                    || cf.getPdfTitle().isEmpty()
                    || cf.getDeadlineDays().isEmpty()
                    || customField.getFontComboBox().getValue() == null
                    || customField.getxComboBox().getValue() == null
                    || customField.getyComboBox().getValue() == null
                    || customField.getOrder().isEmpty()){
                return true;
            }

        }
        return false;
    }

    private boolean checkForEmpty(CustomField customField, CustomField cf) {

        return customField.getTextSizeComboBox().getValue() == null
                || customField.getTextField().isEmpty()
                || cf.getTitle().isEmpty() || cf.getPdfTitle().isEmpty()
                || customField.getFontComboBox().getValue() == null
                || customField.getxComboBox().getValue() == null
                || customField.getyComboBox().getValue() == null
                || customField.getOrder().isEmpty()
                || customField.getDeadlineDays().isEmpty() ;
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
        dc.initialize(customFieldList, cf.getTitle().getValue());
        ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
        streamSource = new CustomSource(byteArrayOutputStream);
        myResource.setStreamSource(streamSource);
        String filename = getFileName(cf);
        myResource.setFilename(filename);
        opener.setResource(myResource);
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

    public void check(CustomField customField){
        if(customField.getTitle() == null){

        }
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

