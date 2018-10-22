package kz.halyqsoft.univercity.modules.studentindividualeduplan.dialogs;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

import java.util.List;

public class LanguageDialog extends AbstractDialog{

    private FileDownloader fileDownloader;
    private  Button kazDownload;
    private  Button rusDownload;
    public LanguageDialog(List<STUDENT> studentList)  {
        kazDownload = new Button("KAZ");
        rusDownload = new Button("RUS");
        for(STUDENT student : studentList){
            StreamResource myResource = ApplicantsForm.createResourceStudent("96", student);
            fileDownloader = new FileDownloader(myResource);
            fileDownloader.extend(rusDownload);

            myResource = ApplicantsForm.createResourceStudent("97", student);
            fileDownloader = new FileDownloader(myResource);
            fileDownloader.extend(kazDownload);
        }
        getContent().addComponent(kazDownload);
        getContent().addComponent(rusDownload);
        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return null;
    }
}
