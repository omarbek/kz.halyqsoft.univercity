package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.Document;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfView extends AbstractTaskView {
    private Button generateButton;
    private Button addComponentButton;
    private Button openPdfButton;
    private ArrayList<CustomField> customFieldList = new ArrayList<CustomField>();
    CustomField cf = new CustomField();
    private StreamResource.StreamSource streamSource;

    private StreamResource createResource() {
        String defaultName = "default.pdf";
        return new StreamResource(streamSource, defaultName);
    }

    public PdfView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        generateButton = new Button("Генерировать");
        addComponentButton = new Button("Добавить");
        openPdfButton = new Button("Открыть");

        cf.pdfTitle = new TextField("Название файла");
        cf.pdfTitle.setImmediate(true);
        cf.pdfTitle.setRequired(true);

        cf.title = new TextField("Заголовок:");
        cf.title.setImmediate(true);
        cf.title.setRequired(true);

        StreamResource myResource = createResource();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(generateButton);

        generateButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                CustomDocument dc = new CustomDocument();
                dc.initialize(customFieldList, cf.title.getValue());
                Document doc = new Document();
                ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
                streamSource = new CustomSource(byteArrayOutputStream);
                myResource.setStreamSource(streamSource);
                if (!cf.pdfTitle.getValue().trim().equals(""))
                    myResource.setFilename(cf.pdfTitle.getValue() + ".pdf");
                else
                    myResource.setFilename("default.pdf");
                byte[] file = byteArrayOutputStream.toByteArray();
                Embedded pdf = new Embedded("",myResource);//display document
                pdf.setMimeType("application/pdf");
                pdf.setType(Embedded.TYPE_BROWSER);
                pdf.setSizeFull();
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
                            pdfDocument.setFileName(cf.pdfTitle.getValue() + ".pdf");
                            pdfDocument.setTitle(cf.title.getValue());
                            pdfDocument.setFileByte(file);
//                        pdfDocument.setUser(user);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfDocument);
                            for(PDF_PROPERTY pdfProperty: dc.getPdfProperties()){
                                pdfProperty.setPdfDocument(pdfDocument);
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfProperty);

                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

        });

        List<HorizontalLayout> horizontalLayoutList = new ArrayList<HorizontalLayout>();

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



            }
        });

        HorizontalLayout acitivityHL = new HorizontalLayout();
        horizontalLayoutList.add(acitivityHL);

        getHL(horizontalLayoutList);

        acitivityHL.addComponent(generateButton);
        acitivityHL.addComponent(addComponentButton);


    }

//       response.setHeader("Expires", "0");
//        response.setHeader("Cache-Control",
//                "must-revalidate, post-check=0, pre-check=0");
//        response.setHeader("Pragma", "public");
//    // setting the content type
//        response.setContentType("application/pdf");
//    // the contentlength
//        response.setContentLength(baos.size());
//    // write ByteArrayOutputStream to the ServletOutputStream
//    OutputStream os = response.getOutputStream();
//        baos.writeTo(os);
//        os.flush();
//        os.close();
    private void getHL(List<HorizontalLayout> horizontalLayoutList) {
        for (HorizontalLayout hl : horizontalLayoutList) {
            getContent().addComponent(hl);

            getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        }
    }

}