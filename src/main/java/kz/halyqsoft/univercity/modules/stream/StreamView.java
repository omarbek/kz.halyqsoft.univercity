package kz.halyqsoft.univercity.modules.stream;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUPS_CREATION_NEEDED;
import kz.halyqsoft.univercity.filter.FStreamFilter;
import kz.halyqsoft.univercity.filter.panel.StreamFilterPanel;
import kz.halyqsoft.univercity.modules.catalog.CatalogEntity;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;
import java.util.zip.ZipEntry;

public class StreamView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private GridWidget ssGW;
    private StreamFilterPanel streamFilterPanel;

    public StreamView(AbstractTask task) throws Exception {
        super(task);
     }

    @Override
    public void initView(boolean b) throws Exception {
        SEMESTER_DATA currentSemesterData = CommonUtils.getCurrentSemesterData();
        if (currentSemesterData != null) {
            VerticalLayout mainVL = new VerticalLayout();

            initGridWidget();
            initFilter();
            Button generateBtn = new Button(getUILocaleUtil().getCaption("generate"));
            generateBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {

                    deleteExtra();

                    QueryModel<V_GROUPS_CREATION_NEEDED> vGroupsCreationNeededQueryModel = new QueryModel<>(
                            V_GROUPS_CREATION_NEEDED.class);

                    List<V_GROUPS_CREATION_NEEDED> groupsList = new ArrayList<>();
                    try {
                        groupsList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                                vGroupsCreationNeededQueryModel));

                        CalculateStream calculateStream = new CalculateStream(groupsList);
                        List<Map<Entity, List<V_GROUPS_CREATION_NEEDED>>> seyfl =
                                calculateStream.sortedEntranceYearFromLanguage;

                        QueryModel<SEMESTER> semesterQueryModel = new QueryModel<>(SEMESTER.class);

                        List<SEMESTER> semesters = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(semesterQueryModel);
                        int z = 1;
                        for (Map<Entity, List<V_GROUPS_CREATION_NEEDED>> value : seyfl) {
                            for (Entity key : value.keySet()) {
                                STREAM stream = new STREAM();
                                stream.setName("STREAM "+ z);
                                stream.setCreated(new Date());
                                stream.setSemesterData(currentSemesterData);
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(stream);
                                int i = 0;

                                for (V_GROUPS_CREATION_NEEDED group : value.get(key)) {
                                    STREAM_GROUP streamGroup = new STREAM_GROUP();
                                    GROUPS gr = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                            lookup(GROUPS.class, group.getId());
                                    streamGroup.setGroup(gr);

                                    if (stream.getSemester() == null) {
                                        stream.setSemester(getSemester(gr.getStudyYear(),
                                                currentSemesterData.getSemesterPeriod()));
                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(stream);
                                    }
                                    streamGroup.setStream(stream);
                                    if (i > 2) {
                                        stream = new STREAM();
                                        stream.setName("STREAM " + (++z));
                                        stream.setCreated(new Date());
                                        stream.setSemesterData(currentSemesterData);
                                        if (stream.getSemester() == null) {
                                            stream.setSemester(getSemester(gr.getStudyYear(),
                                                    currentSemesterData.getSemesterPeriod()));
                                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(stream);
                                        }

                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(stream);
                                        i = 0;
                                    }
                                    i++;
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(streamGroup);
                                }

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    refresh();
                }
            });

            mainVL.addComponent(streamFilterPanel);

            mainVL.addComponent(generateBtn);
            mainVL.setComponentAlignment(generateBtn, Alignment.MIDDLE_CENTER);

            mainVL.addComponent(ssGW);

            getContent().addComponent(mainVL);

            refresh();
        } else {
            Label semIsNotGoingNowLabel = CommonUtils.getSemesterIsGoingNowLabel();
            getContent().addComponent(semIsNotGoingNowLabel);
            getContent().setComponentAlignment(semIsNotGoingNowLabel, Alignment.MIDDLE_CENTER);
        }
    }



    private SEMESTER getSemester(STUDY_YEAR studyYear, SEMESTER_PERIOD semesterPeriod) throws Exception {
        Integer semesterId;
        if (semesterPeriod.getId().equals(SEMESTER_PERIOD.FALL_ID)) {
            semesterId = studyYear.getStudyYear() * 2 - 1;
        } else {
            semesterId = studyYear.getStudyYear() * 2;
        }
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SEMESTER.class,
                ID.valueOf(semesterId));
    }

    private void deleteExtra() {

        try {
            Map<Integer, Object> params = new HashMap<>();
            String sql = "select * from stream_group sg inner join stream  s on sg.stream_id = s.id  where date_trunc('year',s.created )= date_trunc('year' , now())";
            List<STREAM_GROUP> streamList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql, params, STREAM_GROUP.class);

            for (STREAM_GROUP group : streamList) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(group);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Map<Integer, Object> params = new HashMap<>();
            String sql = "select * from stream where date_trunc('year',created )= date_trunc('year' , now()) ";
            List<STREAM> streamList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql, params, STREAM.class);

            for (STREAM group : streamList) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(group);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initGridWidget() {
        ssGW = new GridWidget(STREAM.class);
        ssGW.setSizeFull();
        ssGW.setMultiSelect(true);
        ssGW.setImmediate(true);
        ssGW.addEntityListener(this);

        DBGridModel dbGridModel = (DBGridModel) ssGW.getWidgetModel();
        dbGridModel.setDeferredCreate(true);

        dbGridModel.setRefreshType(ERefreshType.AUTO);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        STREAM stream = (STREAM) e;
        AbstractDialog abstractDialog = new AbstractDialog() {
            @Override
            protected String createTitle() {
                return getUILocaleUtil().getCaption("preview");
            }

        };
        abstractDialog.setWidth(90, Unit.PERCENTAGE);
        abstractDialog.getContent().removeAllComponents();

        abstractDialog.center();

        GridWidget sgGW = new GridWidget(STREAM_GROUP.class);
        sgGW.setMultiSelect(true);
        DBGridModel sgGridModel = (DBGridModel) sgGW.getWidgetModel();
        sgGridModel.setEntities(getStreamGroupByStream(stream));
        sgGridModel.getQueryModel().addWhere("stream", ECriteria.EQUAL, e.getId());
        abstractDialog.getContent().addComponent(sgGW);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                abstractDialog.close();
            }
        });
        abstractDialog.getContent().addComponent(closeButton);
        abstractDialog.getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(abstractDialog);

        return false;
    }

    private List<STREAM_GROUP> getStreamGroupByStream(STREAM stream) {
        QueryModel<STREAM_GROUP> streamGroupQueryModel = new QueryModel(STREAM_GROUP.class);
        streamGroupQueryModel.addWhere("stream", ECriteria.EQUAL, stream.getId());
        List<STREAM_GROUP> streamGroupList = new ArrayList<>();
        try {
            streamGroupList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(streamGroupQueryModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return streamGroupList;
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
        super.onDelete(source, entities, buttonId);
        refresh();
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        STREAM stream = (STREAM) e;
        try {
            if (e.getId() == null ) {//TODO Assyl this line has error, fix it
                stream.setCreated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(stream);
            } else {
                stream.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(stream);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        refresh();
        return false;
    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FStreamFilter sf = (FStreamFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();
        if (sf.getName() != null) {

            sb.append(" and ");
            params.put(i, sf.getName());
            sb.append(" name = ?" + i++);

        }

        if (sf.getSemester() != null) {

            sb.append(" and ");
            params.put(i, sf.getSemester().getId().getId());
            sb.append(" semester_id = ?" + i++);

        }

        if (sf.getSemesterData() != null) {

            sb.append(" and ");
            params.put(i, sf.getSemesterData().getId());
            sb.append(" semester_data_id = ?" + i++);

        }
        List list = new ArrayList<>();

        sb.insert(0, " where TRUE ");
        String sql = "SELECT * from stream "
                + sb.toString();
        try {

            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;

                    STREAM stream = new STREAM();
                    stream.setId(ID.valueOf((long) oo[0]));

                    QueryModel<SEMESTER_DATA> qm1 = new QueryModel<>(SEMESTER_DATA.class);
                    qm1.addWhere("id", ECriteria.EQUAL, ID.valueOf((long) oo[2]));
                    SEMESTER_DATA semesterData = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm1);

                    QueryModel<SEMESTER> qm = new QueryModel<>(SEMESTER.class);
                    qm.addWhere("id", ECriteria.EQUAL, ID.valueOf((long) oo[3]));
                    SEMESTER semester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);

                    stream.setSemesterData(semesterData);
                    stream.setSemester(semester);
                    stream.setName((String) oo[1]);
                    stream.setCreated((Date) oo[4]);
                    list.add(stream);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load streams list", ex);
        }

        refresh(list);

    }

    public void initFilter() throws Exception {
        streamFilterPanel = new StreamFilterPanel(new FStreamFilter());
        streamFilterPanel.addFilterPanelListener(this);
        streamFilterPanel.setImmediate(true);

        ComboBox semesterComboBox = new ComboBox();
        semesterComboBox.setNullSelectionAllowed(true);
        semesterComboBox.setTextInputAllowed(true);
        semesterComboBox.setFilteringMode(FilteringMode.CONTAINS);
        semesterComboBox.setWidth(300, Unit.PIXELS);

        QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
        BeanItemContainer<SEMESTER> semesterBIC = new BeanItemContainer<>(SEMESTER.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterQM));
        semesterComboBox.setContainerDataSource(semesterBIC);

        streamFilterPanel.addFilterComponent("semester", semesterComboBox);

        ComboBox semesterDataComboBox = new ComboBox();
        semesterDataComboBox.setNullSelectionAllowed(true);
        semesterDataComboBox.setTextInputAllowed(true);
        semesterDataComboBox.setFilteringMode(FilteringMode.CONTAINS);
        semesterDataComboBox.setWidth(300, Unit.PIXELS);

        QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
        BeanItemContainer<SEMESTER_DATA> semesterDataBIC = new BeanItemContainer<>(SEMESTER_DATA.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterDataQM));
        semesterDataComboBox.setContainerDataSource(semesterDataBIC);

        streamFilterPanel.addFilterComponent("semesterData", semesterDataComboBox);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        streamFilterPanel.addFilterComponent("name", tf);

    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED) {
            refresh();
        }

        super.handleEntityEvent(ev);
    }

    @Override
    public void clearFilter() {
        doFilter(streamFilterPanel.getFilterBean());
    }

    private void refresh(List<Entity> list) {
        ((DBGridModel) ssGW.getWidgetModel()).setEntities(list);
        try {
            ssGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh stream list", ex);
        }
    }

    private void refresh() {
        doFilter(streamFilterPanel.getFilterBean());
    }
}
