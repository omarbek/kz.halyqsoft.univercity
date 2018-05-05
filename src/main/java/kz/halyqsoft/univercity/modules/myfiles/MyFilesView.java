package kz.halyqsoft.univercity.modules.myfiles;

import com.vaadin.server.*;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.facade.SessionFacade;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.sql.CommonDataSource;
import java.io.*;
import java.util.List;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class MyFilesView extends AbstractTaskView {

    public MyFilesView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        QueryModel<USER_DOCUMENT_FILE> fileQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        List<USER_DOCUMENT_FILE> files = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(fileQM);
        for (USER_DOCUMENT_FILE file : files) {
            HorizontalLayout downloadHL = new HorizontalLayout();
            downloadHL.setSpacing(true);
            Label fileNameL = new Label(file.getFileName());

            Button downloadB = new Button(getUILocaleUtil().getCaption("download"));
            downloadB.setHeight(15, Unit.PERCENTAGE);
            downloadB.setStyleName("link");

            File f = new File(file.getFileName());

            PrintWriter pw = new PrintWriter(f);
            String content = new String(file.getFileBytes());

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
            fd.extend(downloadB);

            pw.close();

            downloadHL.addComponent(downloadB);
            downloadHL.addComponent(fileNameL);

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
