package kz.halyqsoft.univercity.modules.stream.generate;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STREAM;
import kz.halyqsoft.univercity.modules.stream.StreamView;
import kz.halyqsoft.univercity.modules.stream.dialogs.DetailDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.*;

public class CommonStreamsDialog extends WindowUtils implements EntityListener {

    private GridWidget subjectGW;
    private GridWidget streamGW;
    private StreamView streamView;
    private int studyYear;

    public CommonStreamsDialog(StreamView streamView, int studyYear) {
        this.studyYear = studyYear;
        init(null, null);
        this.streamView = streamView;
    }

    @Override
    protected String createTitle() {
        return studyYear + "-" + getUILocaleUtil().getCaption("course");//TODO
    }

    @Override
    protected void refresh() throws Exception {
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();

        HorizontalLayout widgetsHL = CommonUtils.createButtonPanel();
        widgetsHL.setSizeFull();

        subjectGW = new GridWidget(SUBJECT.class);
        subjectGW.addEntityListener(this);
        subjectGW.setMultiSelect(false);
        subjectGW.setSizeFull();
        subjectGW.showToolbar(false);
        widgetsHL.addComponent(subjectGW);

        DBGridModel subjectGM = (DBGridModel) subjectGW.getWidgetModel();
        subjectGM.setRefreshType(ERefreshType.MANUAL);
        subjectGM.setRowNumberVisible(true);

        setSubjects();

        streamGW = new GridWidget(V_STREAM.class);
        streamGW.addEntityListener(this);
        streamGW.setSizeFull();
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
        streamGW.addButtonClickListener(AbstractToolbar.REFRESH_BUTTON, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                refreshStreams();
            }
        });
        widgetsHL.addComponent(streamGW);

        DBGridModel streamGM = (DBGridModel) streamGW.getWidgetModel();
        streamGM.setCrudEntityClass(STREAM.class);
        streamGM.setRefreshType(ERefreshType.MANUAL);
        streamGM.getFormModel().getFieldModel("streamType").setReadOnly(true);
        streamGM.getFormModel().getFieldModel("streamType").setReadOnlyFixed(true);
        streamGM.getFormModel().getFieldModel("subject").setReadOnly(true);
        streamGM.getFormModel().getFieldModel("subject").setReadOnlyFixed(true);
        streamGM.getFormModel().getFieldModel("semesterPeriod").setRequired(true);

        mainVL.addComponent(widgetsHL);

        return mainVL;
    }

    private void setSubjects() {
        try {
            List<SUBJECT> mainSubjects = getSubjects(V_CURRICULUM_DETAIL.class);
            List<SUBJECT> electiveSubjects = getSubjects(V_ELECTIVE_SUBJECT.class);
            List<SUBJECT> addingSubjects = getSubjects(V_CURRICULUM_ADD_PROGRAM.class);
            mainSubjects.addAll(electiveSubjects);
            mainSubjects.addAll(addingSubjects);
            ((DBGridModel) subjectGW.getWidgetModel()).setRefreshType(ERefreshType.MANUAL);
            ((DBGridModel) subjectGW.getWidgetModel()).setEntities(mainSubjects);
            subjectGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    private List<SUBJECT> getSubjects(Class<? extends Entity> entity) throws Exception {
        QueryModel<SUBJECT> mainSubjectQM = new QueryModel<>(SUBJECT.class);
        FromItem curDetFI = mainSubjectQM.addJoin(EJoin.INNER_JOIN, "id", entity, "subject");
        FromItem curFI = curDetFI.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem semesterFI = curDetFI.addJoin(EJoin.INNER_JOIN, "semester", SEMESTER.class, "id");
        mainSubjectQM.addWhere(semesterFI, "studyYear", ECriteria.EQUAL, ID.valueOf(studyYear));
        mainSubjectQM.addWhere(curFI, "diplomaType", ECriteria.EQUAL, STUDENT_DIPLOMA_TYPE.FULL_TIME);
        mainSubjectQM.addWhere(curFI, "deleted", false);
        //        mainSubjectQM.addWhere(curFI, "curriculumStatus", ECriteria.EQUAL, CURRICULUM_STATUS.APPROVED);//TODO
        mainSubjectQM.addWhere("deleted", false);
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(mainSubjectQM);
    }

    private void refreshStreams() {
        try {
            if (subjectGW != null) {
                if (subjectGW.getSelectedEntity() != null) {
                    QueryModel<V_STREAM> streamQM = new QueryModel<>(V_STREAM.class);
                    streamQM.addWhere("streamType", ECriteria.EQUAL, STREAM_TYPE.COMMON);
                    streamQM.addWhere("subject", ECriteria.EQUAL, subjectGW.getSelectedEntity().getId());
                    List<V_STREAM> streams = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(streamQM);
                    ((DBGridModel) streamGW.getWidgetModel()).setEntities(streams);
                    streamGW.refresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if (entityEvent.getSource().equals(subjectGW)) {
            if (entityEvent.getAction() == EntityEvent.SELECTED) {
                refreshStreams();
            }
        } else {
            if (entityEvent.getAction() == EntityEvent.CREATED
                    || entityEvent.getAction() == EntityEvent.MERGED
                    || entityEvent.getAction() == EntityEvent.REMOVED) {
                refreshStreams();
            }
        }
    }

    @Override
    public boolean preCreate(Object o, int i) {
        if (subjectGW.getSelectedEntity() != null) {
            return true;
        } else {
            Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
            return false;
        }
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {
        try {
            STREAM stream = (STREAM) entity;
            stream.setStreamType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STREAM_TYPE.class, STREAM_TYPE.COMMON));
            stream.setSubject((SUBJECT) subjectGW.getSelectedEntity());
            stream.setCreated(new Date());
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {
    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        try {
            STREAM stream = (STREAM) entity;
            if (b) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(stream);

                QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
                ID studyYearId = ID.valueOf(studyYear);
                semesterQM.addWhere("studyYear", ECriteria.EQUAL, studyYearId);
                semesterQM.addWhere("semesterPeriod", ECriteria.EQUAL, stream.getSemesterPeriod().getId());
                SEMESTER semester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookupSingle(semesterQM);

                QueryModel<GROUPS> groupQM = new QueryModel<>(GROUPS.class);
                FromItem specFI = groupQM.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
                FromItem curriculumFI = specFI.addJoin(EJoin.INNER_JOIN, "id", CURRICULUM.class, "speciality");
                FromItem curriculumDetailFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "id", CURRICULUM_DETAIL.class,
                        "curriculum");//TODO
                groupQM.addWhere(curriculumDetailFI, "semester", ECriteria.EQUAL, semester.getId());
                groupQM.addWhere(curriculumDetailFI, "semesterData", ECriteria.EQUAL, CommonUtils.
                        getSemesterDataBySemesterAndEntranceYear(semester, CommonUtils.getCurrentSemesterData().getYear())
                        .getId());
                groupQM.addWhere(curriculumDetailFI, "subject", ECriteria.EQUAL, subjectGW.getSelectedEntity().getId());
                groupQM.addWhere(curriculumDetailFI, "deleted", false);
                groupQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, CommonUtils.getEntranceYearByStudyYear(
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDY_YEAR.class,
                                studyYearId)).getId());
                groupQM.addWhere(specFI, "deleted", false);
                groupQM.addWhere("studyYear", ECriteria.EQUAL, studyYearId);
                List<GROUPS> groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM);

                for (GROUPS group : groups) {
                    STREAM_GROUP streamGroup = new STREAM_GROUP();
                    streamGroup.setStream(stream);
                    streamGroup.setGroup(group);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(streamGroup);
                }
            } else {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        refreshStreams();
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        boolean b = streamView.setDeleted(o, list, streamGW);
        refreshStreams();
        return b;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }
}
