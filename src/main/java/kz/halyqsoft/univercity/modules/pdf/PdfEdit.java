package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.Document;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PdfEdit extends AbstractCommonView {
    private Button addComponentButton;
    private Button openPdfButton;
    private Button createDbButton;
    private StreamResource myResource;
    private ArrayList<CustomField> customFieldList = new ArrayList<CustomField>();
    CustomField cf = new CustomField();
    private StreamResource.StreamSource streamSource;

    private StreamResource createResource() {
        String defaultName = "default.pdf";
        return new StreamResource(streamSource, defaultName);
    }

    public PdfEdit(PDF_DOCUMENT file) throws Exception {

        addOrEdit(file);
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


    private void addOrEdit(PDF_DOCUMENT fileDoc) throws Exception {
        List<HorizontalLayout> horizontalLayoutList = new ArrayList<HorizontalLayout>();
        addComponentButton = new Button("Добавить");
        openPdfButton = new Button("Открыть");
        createDbButton = new Button("Создать");
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
                textHL.addComponent(customField.getStyleComboBox());
                textHL.addComponent(customField.getFontComboBox());
                textHL.addComponent(customField.getTextSize());

                customField.getTextField().setValue(property.getText());
                customField.getxIntegerField().setValue(Integer.toString((int) Math.round(property.getX())));
                customField.getyIntegerField().setValue(Integer.toString((int) Math.round(property.getY())));
                customField.getStyleComboBox().setValue(property.getStyle());
                customField.getFontComboBox().setValue(property.getFont());
                customField.getTextSize().setValue(property.getSize().toString());
                customField.setId(property.getId());

                customFieldList.add(customField);
                horizontalLayoutList.add(textHL);
                getContent().addComponent(textHL);
                getContent().setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);

            }
            cf.pdfTitle.setValue(fileDoc.getFileName());
            cf.title.setValue(fileDoc.getTitle());
        }

        else {


            myResource = createResource();

            myResource.setMIMEType("application/pdf");
            BrowserWindowOpener opener = new BrowserWindowOpener(myResource);
            opener.extend(openPdfButton);


            HorizontalLayout mainHL = new HorizontalLayout();
            mainHL.addComponent(cf.pdfTitle);
            mainHL.addComponent(cf.title);
            horizontalLayoutList.add(mainHL);
            addComponentButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    CustomField customField = new CustomField();

                    HorizontalLayout textHL = new HorizontalLayout();
                    textHL.addComponent(customField.getTextField());
                    textHL.addComponent(customField.getxIntegerField());
                    textHL.addComponent(customField.getyIntegerField());
                    textHL.addComponent(customField.getStyleComboBox());
                    textHL.addComponent(customField.getFontComboBox());
                    textHL.addComponent(customField.getTextSize());

                    customFieldList.add(customField);
                    horizontalLayoutList.add(textHL);
                    getContent().addComponent(textHL);
                    getContent().setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);

                }

            });


            openPdfButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    CustomDocument dc = new CustomDocument();
                    dc.initialize(customFieldList, cf.getTitle().getValue());

                    ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
                    streamSource = new CustomSource(byteArrayOutputStream);
                    myResource.setStreamSource(streamSource);
                    if (!cf.getPdfTitle().getValue().trim().equals("")) {
                        myResource.setFilename(cf.getPdfTitle().getValue() + ".pdf");
                        opener.setResource(myResource);
                    } else {
                        myResource.setFilename("default.pdf");
                        opener.setResource(myResource);
                    }


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

            HorizontalLayout activityHL = new HorizontalLayout();
            horizontalLayoutList.add(activityHL);

            getHL(horizontalLayoutList);

            activityHL.addComponent(addComponentButton);
            activityHL.addComponent(createDbButton);
            activityHL.addComponent(openPdfButton);

        }
    }

    private void getHL(List<HorizontalLayout> horizontalLayoutList) {
        for (HorizontalLayout hl : horizontalLayoutList) {
            getContent().addComponent(hl);

            getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        }
    }
}

