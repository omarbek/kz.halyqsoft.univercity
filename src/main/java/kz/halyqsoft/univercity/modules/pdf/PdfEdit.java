package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.Property;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;

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

    public PdfEdit(PDF_DOCUMENT file) {

        try {
            addOrEdit(file);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    private void addOrEdit(PDF_DOCUMENT fileDoc) throws Exception {
        CustomField cf = new CustomField();
        VerticalLayout itemsVL = new VerticalLayout();
        Button addComponentButton = new Button("Добавить");
        Button openPdfButton = new Button("Открыть");
        Button createDbButton = new Button("Создать");
        CustomField customField = new CustomField();
        if (fileDoc.getId() != null) {
            QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
            propertyQM.addWhere("pdfDocument", ECriteria.EQUAL, fileDoc.getId());
            List<PDF_PROPERTY> properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(propertyQM);
            for (PDF_PROPERTY property : properties) {


                HorizontalLayout textHL = new HorizontalLayout();
                textHL.addComponent(customField.getTextField());
                textHL.addComponent(customField.getxIntegerField());
                textHL.addComponent(customField.getyIntegerField());
                textHL.addComponent(customField.getFontComboBox());
                textHL.addComponent(customField.getTextSize());

                customField.getTextField().setValue(property.getText());
                customField.getxIntegerField().setValue(Integer.toString(Math.round(property.getX())));
                customField.getyIntegerField().setValue(Integer.toString(Math.round(property.getY())));
                customField.getFontComboBox().setValue(property.getFont());
                customField.getTextSize().setValue(property.getSize().toString());
                customField.setId(property.getId());

                customFieldList.add(customField);
                itemsVL.addComponent(textHL);
                itemsVL.setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
//                getContent().addComponent(textHL);
//                getContent().setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);

            }
            cf.pdfTitle.setValue(fileDoc.getFileName());
            cf.title.setValue(fileDoc.getTitle());
        } else {
            myResource = createResource(cf);
            myResource.setMIMEType("application/pdf");
            opener = new BrowserWindowOpener(myResource);
            opener.extend(openPdfButton);

            HorizontalLayout mainHL = new HorizontalLayout();
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
            itemsVL.addComponent(mainHL);
            itemsVL.setComponentAlignment(mainHL, Alignment.MIDDLE_CENTER);
            addComponentButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    CustomField customField = new CustomField();

                    HorizontalLayout textHL = new HorizontalLayout();
                    setTextField(customField.getTextField(), textHL);
                    setTextField(customField.getxIntegerField(), textHL);
                    setTextField(customField.getyIntegerField(), textHL);
                    ComboBox fontComboBox = customField.getFontComboBox();
                    fontComboBox.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            refresh(cf);
                        }
                    });
                    textHL.addComponent(fontComboBox);
                    setTextField(customField.getTextSize(), textHL);


                    customFieldList.add(customField);
                    itemsVL.addComponent(textHL);
                    itemsVL.setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
//                    getContent().addComponent(textHL);
//                    getContent().setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);

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

            });

            createDbButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    CustomDocument dc = new CustomDocument();
                    dc.initialize(customFieldList, cf.getTitle().getValue());

                    ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
                    byte[] file = byteArrayOutputStream.toByteArray();
//                QueryModel<USERS> userQM = new QueryModel<>(USERS.class);
//                String currentUserLogin = CommonUtils.getCurrentUserLogin();
//                userQM.addWhere("login", ECriteria.EQUAL, currentUserLogin);
//                USERS user = null;
//
//                try {
//                    user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userQM);
//
//                } catch (NoResultException e) {
//                    user = null;
//                } catch (Exception e) {
//                    //todo
//                }
//                if (user != null) {

                    try {
                        PDF_DOCUMENT pdfDocument = new PDF_DOCUMENT();
                        if (fileDoc.getId() == null) {

                            pdfDocument.setFileName(cf.pdfTitle.getValue() + ".pdf");
                            pdfDocument.setTitle(cf.title.getValue());
                            pdfDocument.setFileByte(file);

                            //                        pdfDocument.setUser(user);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfDocument);
                            if (dc.getPdfProperties() != null) {
                                for (PDF_PROPERTY pdfProperty : dc.getPdfProperties()) {
                                    pdfProperty.setPdfDocument(pdfDocument);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfProperty);
                                }
                            }
                        } else {
                            pdfDocument.setId(fileDoc.getId());
                            pdfDocument.setFileName(cf.pdfTitle.getValue());
                            pdfDocument.setTitle(cf.title.getValue());
                            pdfDocument.setFileByte(file);
                            //                        pdfDocument.setUser(user);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pdfDocument);
                            if (dc.getPdfProperties() != null) {
                                for (PDF_PROPERTY pdfProperty : dc.getPdfProperties()) {
                                    pdfProperty.setPdfDocument(pdfDocument);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pdfProperty);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

//                    }
            });

            getContent().addComponent(itemsVL);
            getContent().setComponentAlignment(itemsVL, Alignment.MIDDLE_CENTER);

            HorizontalLayout activityHL = new HorizontalLayout();
            activityHL.addComponent(addComponentButton);
            activityHL.addComponent(createDbButton);
            activityHL.addComponent(openPdfButton);

            getContent().addComponent(activityHL);
            getContent().setComponentAlignment(activityHL, Alignment.MIDDLE_CENTER);

        }
    }

    private void refresh(CustomField cf) {
        if (customFieldList.isEmpty()) {
            return;
        }
        for (CustomField customField : customFieldList) {
            if (customField.getTextSize().isEmpty() || customField.getTextField().isEmpty()
                    || cf.getTitle().isEmpty() || cf.getPdfTitle().isEmpty()
                    || customField.getFontComboBox().getValue() == null
                    || customField.getxIntegerField().isEmpty() || customField.getyIntegerField().isEmpty()) {
                return;
            }
        }
        CustomDocument dc = new CustomDocument();
        dc.initialize(customFieldList, cf.getTitle().getValue());
        ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
        streamSource = new CustomSource(byteArrayOutputStream);
        myResource.setStreamSource(streamSource);
        String filename = getFileName(cf);
        myResource.setFilename(filename);
        opener.setResource(myResource);
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
        return "PdfEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return "PdfEdit";//TODO
    }

    @Override
    public void initView(boolean b) throws Exception {
    }
}

