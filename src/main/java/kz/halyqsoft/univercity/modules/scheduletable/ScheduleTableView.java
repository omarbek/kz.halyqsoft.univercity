package kz.halyqsoft.univercity.modules.scheduletable;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
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
    private VerticalLayout mainVl;

    private static final int STUDENT_ID = 2;

    public ScheduleTableView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainVl = new VerticalLayout();
        mainVl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        matrixGL = new GridLayout();
        HorizontalLayout buttonsPanel = CommonUtils.createButtonPanel();
        buttonsPanel.setSizeFull();
        buttonsPanel.setSpacing(true);
        buttonsPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Button teacherBtn = new Button(getUILocaleUtil().getCaption("teachers"));
        teacherBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                matrixGL.removeAllComponents();
                mainVl.removeAllComponents();
                ComboBox teacherCB = new ComboBox(getUILocaleUtil().getCaption("teachers"));
                teacherCB.setFilteringMode(FilteringMode.CONTAINS);
                teacherCB.setWidth(300 ,Unit.PIXELS);
                teacherCB.setNullSelectionAllowed(false);
                teacherCB.setTextInputAllowed(true);
                teacherCB.setImmediate(true);
                teacherCB.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        try{
                            matrixGL.removeAllComponents();
                            user = (EMPLOYEE)teacherCB.getValue();
                            times.removeAll(times);
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
                            List<WEEK_DAY> weekDayList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                                    weekDayQM);

                            for (WEEK_DAY weekDay : weekDayList) {
                                weekDays = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(weekDayQM);
                            }

                            int cols = 9;
                            int rows = times.size() + 1;

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
                            refreshTeacher();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                QueryModel teacherQM = new QueryModel(EMPLOYEE.class);
                teacherQM.addJoin(EJoin.INNER_JOIN , "id" , USERS.class, "id");
                FromItem VEmployeeFI = teacherQM.addJoin(EJoin.INNER_JOIN, "id", V_EMPLOYEE.class, "id");
                teacherQM.addWhere(VEmployeeFI, "employeeType", ECriteria.EQUAL , EMPLOYEE_TYPE.TEACHER_ID);

                BeanItemContainer<EMPLOYEE> employeeBIC = null;
               try{
                   employeeBIC = new BeanItemContainer<EMPLOYEE>(EMPLOYEE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(teacherQM));
               }catch (Exception e){
                   e.printStackTrace();
               }
                teacherCB.setContainerDataSource(employeeBIC);

                mainVl.addComponent(teacherCB);
                mainVl.addComponent(matrixGL);
            }
        });
        Button groupBtn = new Button(getUILocaleUtil().getEntityLabel(GROUPS.class));
        groupBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                matrixGL.removeAllComponents();
                mainVl.removeAllComponents();
                ComboBox groupCB = new ComboBox(getUILocaleUtil().getEntityLabel(GROUPS.class));
                groupCB.setFilteringMode(FilteringMode.CONTAINS);
                groupCB.setWidth(300 ,Unit.PIXELS);
                groupCB.setNullSelectionAllowed(false);
                groupCB.setTextInputAllowed(true);
                groupCB.setImmediate(true);
                groupCB.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        times.removeAll(times);
                        try{
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
                            List<WEEK_DAY> weekDayList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                                    weekDayQM);

                            for (WEEK_DAY weekDay : weekDayList) {
                                weekDays = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(weekDayQM);
                            }

                            int cols = 9;
                            int rows = times.size() + 1;

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


                            refreshGroup((GROUPS) groupCB.getValue());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                QueryModel groupQM = new QueryModel(GROUPS.class);
                groupQM.addJoin(EJoin.INNER_JOIN , "id" , V_GROUP.class, "id");

                BeanItemContainer<GROUPS> groupBIC = null;
                try{
                    groupBIC = new BeanItemContainer<GROUPS>(GROUPS.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM));
                }catch (Exception e){
                    e.printStackTrace();
                }
                groupCB.setContainerDataSource(groupBIC);

                mainVl.addComponent(groupCB);
                mainVl.addComponent(matrixGL);
            }
        });
        Button myBtn = new Button(getUILocaleUtil().getCaption("my.schedule"));
        myBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try{
                    user = CommonUtils.getCurrentUser();
                    mainVl.removeAllComponents();
                    times.removeAll(times);
                    matrixGL.removeAllComponents();
                if (user.getTypeIndex() == STUDENT_ID) {
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

                        refreshStudent();
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
                        refreshTeacher();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                mainVl.addComponent(matrixGL);
                mainVl.setComponentAlignment(matrixGL, Alignment.MIDDLE_CENTER);
            }
        });

        buttonsPanel.addComponent(teacherBtn);
        buttonsPanel.addComponent(groupBtn);
        buttonsPanel.addComponent(myBtn);

        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsPanel);
        getContent().addComponent(mainVl);
    }


    public void refreshStudent() throws Exception {

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
        QueryModel<GROUPS> groupQM = new QueryModel<>(GROUPS.class);
        FromItem seItem = groupQM.addJoin(EJoin.INNER_JOIN, "id", STUDENT_EDUCATION.class, "groups");
        FromItem sItem = seItem.addJoin(EJoin.INNER_JOIN, "student", USERS.class, "id");
        groupQM.addWhere(sItem, "id", ECriteria.EQUAL, user.getId());

        GROUPS group = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(groupQM);

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

                        ScheduleCellTeacher sc = new ScheduleCellTeacher(list);
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


    public void refreshGroup(GROUPS group) {

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
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