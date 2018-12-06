package kz.halyqsoft.univercity.modules.stream;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STREAM;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.panel.GroupFilterPanel;
import kz.halyqsoft.univercity.modules.stream.dialogs.DetailDialog;
import kz.halyqsoft.univercity.modules.stream.generate.CommonStreamsDialog;
import kz.halyqsoft.univercity.modules.stream.generate.GenerateSpecStreams;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.Date;
import java.util.List;

/**
 * @author Omarbek
 * @created on 08.11.2018
 */
public class StreamView extends AbstractTaskView implements FilterPanelListener {

    private GridWidget streamGW;
    private GroupFilterPanel groupFilterPanel;

    public StreamView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        groupFilterPanel = new GroupFilterPanel(new FGroupFilter());
        groupFilterPanel.addFilterPanelListener(this);

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();

        generateCommonStreams(buttonsHL, 1);
        generateCommonStreams(buttonsHL, 2);
        generateCommonStreams(buttonsHL, 3);
        generateCommonStreams(buttonsHL, 4);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

//        ProgressBar samplePB = new ProgressBar();
//        samplePB.setWidth("80%");

        Button generateSpecStreamsButton = new Button(getUILocaleUtil().getCaption("generate.spec.streams"));
        generateSpecStreamsButton.addClickListener(new GenerateSpecStreams(/*samplePB*/));

        buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponent(generateSpecStreamsButton);
        getContent().addComponent(generateSpecStreamsButton);
        getContent().setComponentAlignment(generateSpecStreamsButton, Alignment.MIDDLE_CENTER);

        buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.setSizeFull();
//        buttonsHL.addComponent(samplePB);
//        buttonsHL.setComponentAlignment(samplePB, Alignment.MIDDLE_CENTER);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        ComboBox groupNameCB = new ComboBox();
        groupNameCB.setNullSelectionAllowed(true);
        groupNameCB.setTextInputAllowed(true);
        groupNameCB.setFilteringMode(FilteringMode.CONTAINS);
        groupNameCB.setWidth(300, Unit.PIXELS);
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        groupsQM.addWhere("deleted", false);
        BeanItemContainer<GROUPS> groupBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        groupNameCB.setContainerDataSource(groupBIC);
        groupFilterPanel.addFilterComponent("group", groupNameCB);

        getContent().addComponent(groupFilterPanel);
        getContent().setComponentAlignment(groupFilterPanel, Alignment.MIDDLE_CENTER);

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
        streamGM.setRefreshType(ERefreshType.MANUAL);

        doFilter(groupFilterPanel.getFilterBean());

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
        doFilter(groupFilterPanel.getFilterBean());
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

    private void refreshStreams(GROUPS group) {
        try {
            QueryModel<V_STREAM> streamQM = new QueryModel<>(V_STREAM.class);
            if (group != null) {
                FromItem streamGroupFI = streamQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class, "stream");
                streamQM.addWhere(streamGroupFI, "group", ECriteria.EQUAL, group.getId());
            }
            List<V_STREAM> streams = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(streamQM);
            ((DBGridModel) streamGW.getWidgetModel()).setEntities(streams);
            streamGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FGroupFilter groupFilter = (FGroupFilter) abstractFilterBean;
        refreshStreams(groupFilter.getGroup());
    }

    @Override
    public void clearFilter() {
        doFilter(groupFilterPanel.getFilterBean());
    }
}
