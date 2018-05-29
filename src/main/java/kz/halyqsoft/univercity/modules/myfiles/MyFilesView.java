package kz.halyqsoft.univercity.modules.myfiles;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class MyFilesView extends AbstractTaskView {

    public MyFilesView(AbstractTask task) throws Exception {
        super(task);
    }

    private static final String UPLOAD_FOLDER = "C:/Users/Omarbek/IdeaProjects/univercity/tmp/";

    @Override
    public void initView(boolean b) throws Exception {
        FileUploader fr = new FileUploader();
        Upload fileUpload = new Upload();
        fileUpload.setCaption(null);
        fileUpload.setButtonCaption(getUILocaleUtil().getCaption("select.file"));
        fileUpload.setReceiver(fr);
        fileUpload.addSucceededListener(fr);
        fileUpload.setImmediate(true);
        fileUpload.setVisible(true);
        fileUpload.addSucceededListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(Upload.SucceededEvent event) {
                try {
                    USER_DOCUMENT_FILE userDocumentFile = new USER_DOCUMENT_FILE();
                    File file = fr.getFile();
                    userDocumentFile.setFileName(file.getName());
                    userDocumentFile.setFileBytes(fr.getByteArray());
//                    userDocumentFile.setFileBytes(getByteArray(file));
                    userDocumentFile.setUserDocument(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(USER_DOCUMENT.class, ID.valueOf(216)));
                    userDocumentFile.setDeleted(false);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userDocumentFile);
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        fileUpload.addFailedListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(Upload.FailedEvent event) {
                Notification.show("error");
            }
        });
        getContent().addComponent(fileUpload);


        QueryModel<USER_DOCUMENT_FILE> fileQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        List<USER_DOCUMENT_FILE> files = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(fileQM);
        FileInputStream fileInputStream = null;
        for (USER_DOCUMENT_FILE file : files) {
            HorizontalLayout downloadHL = new HorizontalLayout();
            downloadHL.setSpacing(true);
            Label fileNameL = new Label(file.getFileName());

            File f = new File(file.getFileName());

            Button downloadB = new Button(getUILocaleUtil().getCaption("download"));
            downloadB.setHeight(15, Unit.PERCENTAGE);
            downloadB.setStyleName("link");
            downloadB.setData(file);
            downloadB.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    USER_DOCUMENT_FILE userDocumentFile = (USER_DOCUMENT_FILE) downloadB.getData();
                    writeBytesToFile(userDocumentFile.getFileBytes(), UPLOAD_FOLDER + userDocumentFile.getFileName());
                    CommonUtils.showSavedNotification();

//                    writeBytesToFile(file.getFileBytes(), UPLOAD_FOLDER + file.getFileName());
//                    writeBytesToFileClassic(file.getFileBytes(), UPLOAD_FOLDER + file.getFileName());
//                    writeBytesToFileNio(file.getFileBytes(), UPLOAD_FOLDER + file.getFileName());
                }
            });


//            PrintWriter pw = new PrintWriter(f);
//            String content = new String(file.getFileBytes());
//
//            if (content.startsWith("[")) {
//                String[] byteValues = content.substring(1, content.length() - 1).split(",");
//                byte[] bytes = new byte[byteValues.length];
//                for (int i = 0, len = bytes.length; i < len; i++) {
//                    bytes[i] = Byte.parseByte(byteValues[i].trim());
//                }
//                content = new String(bytes);
//            }
//
//            pw.println(content);
//
//            Resource res = new FileResource(f);
//            FileDownloader fd = new FileDownloader(res);
//            fd.extend(downloadB);
//
//            pw.close();

//            byte[] bFile = new byte[(int) f.length()];
//            fileInputStream = new FileInputStream(f);
//            fileInputStream.read(bFile);

            downloadHL.addComponent(downloadB);
            downloadHL.addComponent(fileNameL);

            Button openDocButton = new Button("openDoc");
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

            getContent().addComponent(downloadHL);
        }
    }

    private static void writeBytesToFileClassic(byte[] bFile, String fileDest) {

        FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(fileDest);
            fileOuputStream.write(bFile);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Since JDK 7 - try resources
    private static void writeBytesToFile(byte[] bFile, String fileDest) {

        try (FileOutputStream fileOuputStream = new FileOutputStream(fileDest)) {
            fileOuputStream.write(bFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Since JDK 7, NIO
    private static void writeBytesToFileNio(byte[] bFile, String fileDest) {

        try {
            Path path = Paths.get(fileDest);
            Files.write(path, bFile);
        } catch (IOException e) {
            e.printStackTrace();
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
