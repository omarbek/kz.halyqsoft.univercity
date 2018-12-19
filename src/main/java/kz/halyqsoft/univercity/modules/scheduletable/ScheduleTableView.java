package kz.halyqsoft.univercity.modules.scheduletable;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleTableView extends AbstractTaskView {

    private List<TIME> times = new ArrayList<>();
    private List<WEEK_DAY> weekDays;
    private GridLayout matrixGL;
    private boolean addComponent = true;
    private USERS user = CommonUtils.getCurrentUser();
    private ComboBox groupCB, teacherCB, roomCB;

    private static final int STUDENT = 2;

    public ScheduleTableView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        groupCB = new ComboBox();
        groupCB.setTextInputAllowed(true);
        groupCB.setFilteringMode(FilteringMode.CONTAINS);
        groupCB.setCaption(getUILocaleUtil().getEntityLabel(GROUPS.class));
        QueryModel<GROUPS> groupQM = new QueryModel<>(GROUPS.class);
        groupQM.addWhere("deleted", Boolean.FALSE);
        groupQM.addOrderDesc("id");
        BeanItemContainer<GROUPS> groupBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM));
        groupCB.setContainerDataSource(groupBIC);

        teacherCB = new ComboBox();
        teacherCB.setTextInputAllowed(true);
        teacherCB.setFilteringMode(FilteringMode.CONTAINS);
        teacherCB.setCaption(getUILocaleUtil().getEntityLabel(EMPLOYEE.class));
        QueryModel<USERS> emplQM = new QueryModel<>(USERS.class);
        emplQM.addWhere("typeIndex", ECriteria.EQUAL, 1);
        emplQM.addOrderDesc("id");
        BeanItemContainer<USERS> emplBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(emplQM));
        teacherCB.setContainerDataSource(emplBIC);

        roomCB = new ComboBox();
        roomCB.setTextInputAllowed(true);
        roomCB.setFilteringMode(FilteringMode.CONTAINS);
        roomCB.setCaption(getUILocaleUtil().getEntityLabel(ROOM.class));
        QueryModel<ROOM> roomQM = new QueryModel<>(ROOM.class);
        roomQM.addOrderDesc("id");
        BeanItemContainer<ROOM> roomBIC = new BeanItemContainer<>(ROOM.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomQM));
        roomCB.setContainerDataSource(roomBIC);

        if (user.getTypeIndex() == STUDENT) {

            QueryModel<LESSON_TIME> lessonTimeQM = new QueryModel<>(LESSON_TIME.class);
            List<LESSON_TIME> lessonTimeList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    lessonTimeQM);
            for (LESSON_TIME lt : lessonTimeList) {
                QueryModel<TIME> timeQM = new QueryModel<>(TIME.class);
                timeQM.addWhere("id", ECriteria.EQUAL, lt.getBeginTime().getId());
                timeQM.addOrder("timeValue");
                TIME time = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(timeQM);
                times.add(time);
            }

            QueryModel<WEEK_DAY> weekDayQM = new QueryModel<>(WEEK_DAY.class);
            weekDayQM.addOrder("id");
            List<WEEK_DAY> weekDayList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    weekDayQM);

            for (WEEK_DAY weekDay : weekDayList) {
                weekDays = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(weekDayQM);
            }

            int cols = 9;
            int rows = times.size() + 1;

            matrixGL = new GridLayout();
            matrixGL.setHeight(100, Unit.PERCENTAGE);
            matrixGL.setWidth(78, Unit.PERCENTAGE);
            matrixGL.setColumns(cols);
            matrixGL.setRows(rows);

            int i = 1, j = 1;
            for (TIME time : times) {
                Label timeLabel = new Label(time.toString());
                timeLabel.setHeightUndefined();
                timeLabel.setWidth(35, Unit.PIXELS);
                timeLabel.addStyleName("day-time");
                timeLabel.addStyleName("bold");
                matrixGL.addComponent(timeLabel, 0, j);
                matrixGL.setComponentAlignment(timeLabel, Alignment.MIDDLE_RIGHT);
                j++;
            }

            for (WEEK_DAY wd : weekDays) {
                Label l = new Label(wd.getDayNameRU());
                l.setSizeUndefined();
                l.addStyleName("day-time");
                l.addStyleName("bold");
                matrixGL.addComponent(l, i, 0);
                matrixGL.setComponentAlignment(l, Alignment.MIDDLE_LEFT);
                matrixGL.setColumnExpandRatio(i, 1);
                i++;
            }

            HorizontalLayout comboVL = new HorizontalLayout();

            comboVL.addComponent(groupCB);
            comboVL.addComponent(teacherCB);
            comboVL.addComponent(roomCB);
            groupCB.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    try {
                        if (groupCB.getValue() == null) {
                            refreshStudent();
                        } else {
                            refresh(groupCB);
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                    }
                }
            });
            teacherCB.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    try {
                        if (teacherCB.getValue() == null) {
                            refreshStudent();
                        } else {
                            refreshTeacherCB(teacherCB);
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                    }
                }
            });
            roomCB.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    try {
                        if (roomCB.getValue() == null) {
                            refreshStudent();
                        } else {
                            refreshRoomCB(roomCB);
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                    }
                }
            });

            if (!groupCB.isSelected(null) && !teacherCB.isSelected(null) && !roomCB.isSelected(null)) {
                refreshStudent();
            }

            getContent().addComponent(comboVL);
            getContent().addComponent(matrixGL);
            getContent().setComponentAlignment(comboVL, Alignment.MIDDLE_CENTER);
            getContent().setComponentAlignment(matrixGL, Alignment.MIDDLE_CENTER);
        } else {

            QueryModel<LESSON_TIME> lessonTimeQM = new QueryModel<>(LESSON_TIME.class);
            List<LESSON_TIME> lessonTimeList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    lessonTimeQM);
            for (LESSON_TIME lt : lessonTimeList) {
                QueryModel<TIME> timeQM = new QueryModel<>(TIME.class);
                timeQM.addWhere("id", ECriteria.EQUAL, lt.getBeginTime().getId());
                timeQM.addOrder("timeValue");
                TIME time = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(timeQM);
                times.add(time);
            }

            QueryModel<WEEK_DAY> weekDayQM = new QueryModel<>(WEEK_DAY.class);
            weekDayQM.addOrder("id");
            List<WEEK_DAY> weekDayList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    weekDayQM);

            for (WEEK_DAY weekDay : weekDayList) {
                weekDays = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(weekDayQM);
            }
            int cols = 9;
            int rows = times.size() + 1;

            matrixGL = new GridLayout();
            matrixGL.setHeight(100, Unit.PERCENTAGE);
            matrixGL.setWidth(78, Unit.PERCENTAGE);
            matrixGL.setColumns(cols);
            matrixGL.setRows(rows);

            int i = 1, j = 1;
            for (TIME time : times) {
                Label timeLabel = new Label(time.toString());
                timeLabel.setHeightUndefined();
                timeLabel.setWidth(35, Unit.PIXELS);
                timeLabel.addStyleName("day-time");
                timeLabel.addStyleName("bold");
                matrixGL.addComponent(timeLabel, 0, j);
                matrixGL.setComponentAlignment(timeLabel, Alignment.MIDDLE_RIGHT);
                j++;
            }

            for (WEEK_DAY wd : weekDays) {
                Label l = new Label(wd.getDayNameRU());
                l.setSizeUndefined();
                l.addStyleName("day-time");
                l.addStyleName("bold");
                matrixGL.addComponent(l, i, 0);
                matrixGL.setComponentAlignment(l, Alignment.MIDDLE_LEFT);
                matrixGL.setColumnExpandRatio(i, 1);
                i++;
            }
            HorizontalLayout comboVL = new HorizontalLayout();

            comboVL.addComponent(groupCB);
            comboVL.addComponent(teacherCB);
            comboVL.addComponent(roomCB);

            groupCB.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    try {
                        if (groupCB.getValue() == null) {
                            refreshTeacher();
                        } else {
                            refresh(groupCB);
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                    }
                }
            });

            teacherCB.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    try {
                        if (teacherCB.getValue() == null) {
                            refreshTeacher();
                        } else {
                            refreshTeacherCB(teacherCB);
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                    }
                }
            });
            roomCB.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    try {
                        if (roomCB.getValue() == null) {
                            refreshTeacher();
                        } else {
                            refreshRoomCB(roomCB);
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                    }
                }
            });

            if (!groupCB.isSelected(null) && !teacherCB.isSelected(null) && !roomCB.isSelected(null)) {
                refreshTeacher();
            }

            getContent().addComponent(comboVL);
            getContent().setComponentAlignment(comboVL, Alignment.MIDDLE_CENTER);
            getContent().addComponent(matrixGL);
            getContent().setComponentAlignment(matrixGL, Alignment.MIDDLE_CENTER);

        }

    }

    private void refresh(ComboBox groupCB) throws Exception {
        getSchedule((GROUPS) groupCB.getValue());
    }

    public void getSchedule(GROUPS group) {

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();

        QueryModel<SCHEDULE_DETAIL> sdQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        FromItem lessonTimeFI = sdQM.addJoin(EJoin.INNER_JOIN, "lessonTime", LESSON_TIME.class, "id");
        sdQM.addWhere("group", ECriteria.EQUAL, group.getId());
        sdQM.addWhereAnd("semesterData", ECriteria.EQUAL, semesterData.getId());
        for (int i = 0; i < weekDays.size(); i++) {
            sdQM.addWhere("weekDay", ECriteria.EQUAL, weekDays.get(i).getId());
            for (int j = 0; j < times.size(); j++) {
                sdQM.addWhere(lessonTimeFI, "beginTime", ECriteria.EQUAL, times.get(j).getId());
                try {
                    SCHEDULE_DETAIL scheduleDetail = null;
                    List<SCHEDULE_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(sdQM);
                    if (!list.isEmpty()) {
                        scheduleDetail = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(SCHEDULE_DETAIL.class, list.get(0).getId());
                    }

                    ScheduleCellStudent sc = new ScheduleCellStudent(scheduleDetail);
                    int col = i + 1;
                    int row = j + 1;

                    matrixGL.removeComponent(col, row);
                    if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                            - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
                        matrixGL.removeComponent(col, row + 1);
                        matrixGL.addComponent(sc, col, row, col, row + 1);
                        addComponent = false;
                    } else if (addComponent) {
                        matrixGL.addComponent(sc, col, row);
                    } else {
                        addComponent = true;
                    }

                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load schedule detail", ex);
                }
                matrixGL.removeComponent(weekDays.size() + 1, j + 1);
            }
        }
    }

    private void refreshTeacherCB(ComboBox teacherCB) throws Exception {
        getScheduleTeacher((EMPLOYEE) teacherCB.getValue());
    }

    public void getScheduleTeacher(EMPLOYEE teacher) {

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();

        QueryModel<SCHEDULE_DETAIL> sdQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        FromItem lessonTimeFI = sdQM.addJoin(EJoin.INNER_JOIN, "lessonTime", LESSON_TIME.class, "id");
        sdQM.addWhere("teacher", ECriteria.EQUAL, teacher.getId());
        sdQM.addWhereAnd("semesterData", ECriteria.EQUAL, semesterData.getId());
        for (int i = 0; i < weekDays.size(); i++) {
            sdQM.addWhere("weekDay", ECriteria.EQUAL, weekDays.get(i).getId());
            for (int j = 0; j < times.size(); j++) {
                sdQM.addWhere(lessonTimeFI, "beginTime", ECriteria.EQUAL, times.get(j).getId());
                try {
                    SCHEDULE_DETAIL scheduleDetail = null;
                    List<SCHEDULE_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(sdQM);
                    if (!list.isEmpty()) {
                        scheduleDetail = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(SCHEDULE_DETAIL.class, list.get(0).getId());
                    }

                    ScheduleCellStudent sc = new ScheduleCellStudent(scheduleDetail);
                    int col = i + 1;
                    int row = j + 1;

                    matrixGL.removeComponent(col, row);
                    if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                            - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
                        matrixGL.removeComponent(col, row + 1);
                        matrixGL.addComponent(sc, col, row, col, row + 1);
                        addComponent = false;
                    } else if (addComponent) {
                        matrixGL.addComponent(sc, col, row);
                    } else {
                        addComponent = true;
                    }

                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load schedule detail", ex);
                }
                matrixGL.removeComponent(weekDays.size() + 1, j + 1);
            }
        }
    }

    private void refreshRoomCB(ComboBox roomCB) throws Exception {
        getScheduleRoom((ROOM) roomCB.getValue());
    }

    public void getScheduleRoom(ROOM room) {

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();

        QueryModel<SCHEDULE_DETAIL> sdQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        FromItem lessonTimeFI = sdQM.addJoin(EJoin.INNER_JOIN, "lessonTime", LESSON_TIME.class, "id");
        sdQM.addWhere("room", ECriteria.EQUAL, room.getId());
        sdQM.addWhereAnd("semesterData", ECriteria.EQUAL, semesterData.getId());
        for (int i = 0; i < weekDays.size(); i++) {
            sdQM.addWhere("weekDay", ECriteria.EQUAL, weekDays.get(i).getId());
            for (int j = 0; j < times.size(); j++) {
                sdQM.addWhere(lessonTimeFI, "beginTime", ECriteria.EQUAL, times.get(j).getId());
                try {
                    SCHEDULE_DETAIL scheduleDetail = null;
                    List<SCHEDULE_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(sdQM);
                    if (!list.isEmpty()) {
                        scheduleDetail = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(SCHEDULE_DETAIL.class, list.get(0).getId());
                    }

                    ScheduleCellStudent sc = new ScheduleCellStudent(scheduleDetail);
                    int col = i + 1;
                    int row = j + 1;

                    matrixGL.removeComponent(col, row);
                    if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                            - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
                        matrixGL.removeComponent(col, row + 1);
                        matrixGL.addComponent(sc, col, row, col, row + 1);
                        addComponent = false;
                    } else if (addComponent) {
                        matrixGL.addComponent(sc, col, row);
                    } else {
                        addComponent = true;
                    }


                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load schedule detail", ex);
                }
                matrixGL.removeComponent(weekDays.size() + 1, j + 1);
            }
        }
    }

    public void refreshStudent() throws Exception {

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
        QueryModel<GROUPS> groupQM = new QueryModel<>(GROUPS.class);
        FromItem seItem = groupQM.addJoin(EJoin.INNER_JOIN, "id", STUDENT_EDUCATION.class, "groups");
        FromItem sItem = seItem.addJoin(EJoin.INNER_JOIN, "student", USERS.class, "id");
        groupQM.addWhere(sItem, "id", ECriteria.EQUAL, user.getId());
        GROUPS group = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(groupQM);

        QueryModel<STREAM> streamQM = new QueryModel<>(STREAM.class);
        FromItem streamGroupFI = streamQM.addJoin(EJoin.INNER_JOIN,"id",STREAM_GROUP.class, "stream_id");
        FromItem groupsFI = streamGroupFI.addJoin(EJoin.INNER_JOIN,"group",GROUPS.class,"id");
        FromItem educationFI = groupsFI.addJoin(EJoin.INNER_JOIN,"id",STUDENT_EDUCATION.class,"groups");
        FromItem studentFI = educationFI.addJoin(EJoin.INNER_JOIN,"student",USERS.class,"id");
        streamQM.addWhere(studentFI,"id",ECriteria.EQUAL,user.getId());
        STREAM stream = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(streamQM);

        if (group != null) {
            QueryModel<SCHEDULE_DETAIL> sdQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            FromItem lessonTimeFI = sdQM.addJoin(EJoin.INNER_JOIN, "lessonTime", LESSON_TIME.class, "id");
            sdQM.addWhere("group", ECriteria.EQUAL, group.getId());
            sdQM.addWhereAnd("semesterData", ECriteria.EQUAL, semesterData.getId());
            for (int i = 0; i < weekDays.size(); i++) {
                sdQM.addWhere("weekDay", ECriteria.EQUAL, weekDays.get(i).getId());
                for (int j = 0; j < times.size(); j++) {
                    sdQM.addWhere(lessonTimeFI, "beginTime", ECriteria.EQUAL, times.get(j).getId());
                    try {
                        SCHEDULE_DETAIL scheduleDetail = null;
                        List<SCHEDULE_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(sdQM);
                        if (!list.isEmpty()) {
                            scheduleDetail = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    lookup(SCHEDULE_DETAIL.class, list.get(0).getId());
                        }

                        ScheduleCellStudent sc = new ScheduleCellStudent(scheduleDetail);
                        int col = i  + 1;
                        int row = j + 1;

                        matrixGL.removeComponent(col, row);
                        if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                                - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
                            matrixGL.removeComponent(col, row + 1);
                            matrixGL.addComponent(sc, col, row, col, row + 1);
                            addComponent = false;
                        } else if (addComponent) {
                            matrixGL.addComponent(sc, col, row);
                        } else {
                            addComponent = true;
                        }
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to load schedule detail", ex);
                    }
                    matrixGL.removeComponent(weekDays.size() + 1, j + 1);
                }
            }
        }
    }

    public void refreshTeacher() throws Exception {
        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
        QueryModel<USERS> userQM = new QueryModel<>(USERS.class);
        userQM.addWhere("id", ECriteria.EQUAL, user.getId());

        USERS teacher = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userQM);

        if (teacher != null) {
            QueryModel<SCHEDULE_DETAIL> sdQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            FromItem lessonTimeFI = sdQM.addJoin(EJoin.INNER_JOIN, "lessonTime", LESSON_TIME.class, "id");
            sdQM.addWhere("teacher", ECriteria.EQUAL, teacher.getId());
            sdQM.addWhereAnd("semesterData", ECriteria.EQUAL, semesterData.getId());
            for (int i = 0; i < weekDays.size(); i++) {
                sdQM.addWhere("weekDay", ECriteria.EQUAL, weekDays.get(i).getId());
                for (int j = 0; j < times.size(); j++) {
                    sdQM.addWhere(lessonTimeFI, "beginTime", ECriteria.EQUAL, times.get(j).getId());
                    try {
                        SCHEDULE_DETAIL scheduleDetail = null;
                        List<SCHEDULE_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(sdQM);
                        if (!list.isEmpty()) {
                            scheduleDetail = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    lookup(SCHEDULE_DETAIL.class, list.get(0).getId());
                        }

                        ScheduleCellTeacher sc = new ScheduleCellTeacher(scheduleDetail);
                        int col = i + 1;
                        int row = j + 1;

                        matrixGL.removeComponent(col, row);
                        if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                                - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
                            matrixGL.removeComponent(col, row + 1);
                            matrixGL.addComponent(sc, col, row, col, row + 1);
                            addComponent = false;
                        } else if (addComponent) {
                            matrixGL.addComponent(sc, col, row);
                        } else {
                            addComponent = true;
                        }

                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to load schedule detail", ex);
                    }
                    matrixGL.removeComponent(weekDays.size() + 1, j + 1);
                }
            }

        }

    }

}