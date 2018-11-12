package kz.halyqsoft.univercity.modules.stream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ProgressBar;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STREAM;
import kz.halyqsoft.univercity.modules.stream.dialogs.DetailDialog;
import kz.halyqsoft.univercity.modules.stream.generate.GenerateCommonStreams;
import kz.halyqsoft.univercity.modules.stream.generate.GenerateSpecStreams;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.Date;

/**
 * @author Omarbek
 * @created on 08.11.2018
 */
public class StreamView extends AbstractTaskView {

    public StreamView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        // Add a normal progress bar
        ProgressBar samplePB = new ProgressBar();
        getContent().addComponent(samplePB);
        getContent().setComponentAlignment(samplePB, Alignment.MIDDLE_CENTER);

        Button generateCommonStreamsButton = new Button("generateCommonStreams");//TODO
        generateCommonStreamsButton.addClickListener(new GenerateCommonStreams(samplePB));
        getContent().addComponent(generateCommonStreamsButton);

        Button generateSpecStreamsButton = new Button("generateSpecStreams");//TODO
        generateSpecStreamsButton.addClickListener(new GenerateSpecStreams(samplePB));
        getContent().addComponent(generateSpecStreamsButton);

        GridWidget streamGW = new GridWidget(V_STREAM.class);
        streamGW.addEntityListener(this);
        streamGW.addButtonClickListener(AbstractToolbar.PREVIEW_BUTTON, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (streamGW.getSelectedEntities().isEmpty()) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.noentityedit"));
                } else {
                    V_STREAM streamView = (V_STREAM) streamGW.getSelectedEntity();
                    try {
                        new DetailDialog(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                                .lookup(STREAM.class, streamView.getId()));
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                }
            }
        });

        DBGridModel streamGM = (DBGridModel) streamGW.getWidgetModel();
        streamGM.setCrudEntityClass(STREAM.class);

        getContent().addComponent(streamGW);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return false;
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        STREAM stream = (STREAM) e;
        stream.setCreated(new Date());
        return true;
    }
}
