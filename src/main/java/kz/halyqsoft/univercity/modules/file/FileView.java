package kz.halyqsoft.univercity.modules.file;


import com.vaadin.server.*;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.modules.pdf.PdfEdit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.facade.SessionFacade;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.sql.CommonDataSource;
import java.io.*;
import java.util.List;

public class FileView extends AbstractTaskView{

    public FileView(AbstractTask task) throws Exception {
        super(task);
    }
    @Override
    public void initView(boolean b) throws Exception {

        ghjk();
    }

    private void ghjk() throws Exception {
        QueryModel<PDF_DOCUMENT> fileQM = new QueryModel<>(PDF_DOCUMENT.class);
        List<PDF_DOCUMENT> files = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(fileQM);
        for (PDF_DOCUMENT file : files) {
            HorizontalLayout downloadHL = new HorizontalLayout();
            downloadHL.setSpacing(true);
            Label fileNameL = new Label(file.getFileName());

            Button downloadB = new Button(getUILocaleUtil().getCaption("download"));
            downloadB.setHeight(15, Unit.PERCENTAGE);
            downloadB.setStyleName("link");

            Button updateButton = new Button(getUILocaleUtil().getCaption("update"));
            updateButton.setHeight(15, Unit.PERCENTAGE);
            updateButton.setStyleName("link");

            VerticalLayout pdfVL=new VerticalLayout();
            updateButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    try {
                        pdfVL.removeAllComponents();

                        PdfEdit pdfEdit = new PdfEdit(file);
                        pdfVL.addComponent(pdfEdit);

                        getContent().addComponent(pdfVL);



                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }});


            Button deleteButton = new Button(getUILocaleUtil().getCaption("delete"));
            deleteButton.setHeight(15, Unit.PERCENTAGE);
            deleteButton.setStyleName("link");

            deleteButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    try {


                        QueryModel<PDF_PROPERTY> propertyFileByIdQM = new QueryModel<>(PDF_PROPERTY.class);
                        propertyFileByIdQM.addWhere("pdfDocument", ECriteria.EQUAL,file.getId());
                        List<PDF_PROPERTY> properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(propertyFileByIdQM);
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(properties);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(file);

                        downloadHL.removeAllComponents();
                        getContent().removeComponent(downloadHL);

                        //file delete
                        //list fileid property
                        //delete(list)
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }});

            File f = new File(file.getFileName());

            PrintWriter pw = new PrintWriter(f);
            String content = new String(file.getFileByte());

            if (content.startsWith("[")) {
                String[] byteValues = content.substring(1, content.length() - 1).split(",");
                byte[] bytes = new byte[byteValues.length];
                for (int i = 0, len = bytes.length; i < len; i++) {
                    bytes[i] = Byte.parseByte(byteValues[i].trim());
                }
                content = new String(bytes);
            }

            pw.println(content);

            Resource res = new FileResource(f);
            FileDownloader fd = new FileDownloader(res);
            fd.extend(updateButton);

            pw.close();

            downloadHL.addComponent(updateButton);
            downloadHL.addComponent(fileNameL);
            downloadHL.addComponent(deleteButton);

            Button openDocButton =new Button("openDoc");
            openDocButton.setData(f);
            openDocButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    addFile(openDocButton);
                }
            });
            downloadHL.addComponent(openDocButton);

            Button openButton = new Button("open");

            final StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    try {
                        return reteriveByteArrayInputStream(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }, f.getName());
            resource.setMIMEType("application/pdf");
            final BrowserWindowOpener opener = new BrowserWindowOpener(resource);
            opener.extend(openButton);

            openButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    opener.setResource(null);
                    opener.setResource(resource);

                }
            });



            downloadHL.addComponent(openButton);
            downloadHL.addComponent(deleteButton);
            downloadHL.addComponent(updateButton);

            getContent().addComponent(downloadHL);
        }
    }

    private void addFile(Button openButton) {
        try {
            File root = (File) openButton.getData();
            Embedded pdf = new Embedded(null, new FileResource(root));
            pdf.setSizeFull();
            pdf.setMimeType("application/pdf");
            pdf.setType(2);
            pdf.setSizeFull();
            pdf.setHeight(700, Unit.PIXELS);
            getContent().removeAllComponents();
            getContent().addComponent(pdf);
        } catch (Exception ex) {
            LOG.error("Unable to load pdf: ", ex);
            Message.showError(ex.toString());
        }
    }

    public static ByteArrayInputStream reteriveByteArrayInputStream(File file) throws IOException {
        return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    }
}
