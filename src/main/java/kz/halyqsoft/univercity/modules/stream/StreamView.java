package kz.halyqsoft.univercity.modules.stream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressBar;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STREAM;
import kz.halyqsoft.univercity.modules.stream.dialogs.DetailDialog;
import kz.halyqsoft.univercity.modules.stream.generate.CommonStreamsDialog;
import kz.halyqsoft.univercity.modules.stream.generate.GenerateSpecStreams;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.Date;
import java.util.List;

/**
 * @author Omarbek
 * @created on 08.11.2018
 */
public class StreamView extends AbstractTaskView {

    private GridWidget streamGW;

    public StreamView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();

        generateCommonStreams(buttonsHL, 1);
        generateCommonStreams(buttonsHL, 2);
        generateCommonStreams(buttonsHL, 3);
        generateCommonStreams(buttonsHL, 4);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        ProgressBar samplePB = new ProgressBar();
        samplePB.setWidth("80%");

        Button generateSpecStreamsButton = new Button(getUILocaleUtil().getCaption("generate.spec.streams"));
        generateSpecStreamsButton.addClickListener(new GenerateSpecStreams(samplePB));

        buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponent(generateSpecStreamsButton);
        getContent().addComponent(generateSpecStreamsButton);
        getContent().setComponentAlignment(generateSpecStreamsButton, Alignment.MIDDLE_CENTER);

        buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.setSizeFull();
        buttonsHL.addComponent(samplePB);
        buttonsHL.setComponentAlignment(samplePB, Alignment.MIDDLE_CENTER);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        streamGW = new GridWidget(V_STREAM.class);
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

    private void generateCommonStreams(HorizontalLayout buttonsHL, int studyYear) {
        Button generateCommonStreamsButton = new Button(getUILocaleUtil().getCaption("generate.common.streams") +
                "-" + studyYear);
        generateCommonStreamsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                new CommonStreamsDialog(StreamView.this, studyYear);
            }
        });
        buttonsHL.addComponent(generateCommonStreamsButton);
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

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        boolean b = setDeleted(o, list, streamGW);
        refreshStreams();
        return b;
    }

    public boolean setDeleted(Object o, List<Entity> list, GridWidget streamGW) {
        if (o.equals(streamGW)) {
            try {
                for (Entity entity : list) {
                    STREAM stream = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STREAM.class, entity.getId());
                    stream.setDeleted(true);
                    stream.setUpdated(new Date());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(stream);
                }
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
        }
        return false;
    }

    private void refreshStreams() {
        try {
            QueryModel<V_STREAM> streamQM = new QueryModel<>(V_STREAM.class);
            List<V_STREAM> streams = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(streamQM);
            ((DBGridModel) streamGW.getWidgetModel()).setEntities(streams);
            streamGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }
}
