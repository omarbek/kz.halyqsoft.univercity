package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_TEACHER_LOAD_ASSIGN_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.TimeUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 13.07.2018
 */
public class ScheduleManual extends AbstractCommonView implements EntityListener{

    private VerticalLayout tablesVL = new VerticalLayout();
    private ComboBox semesterDataCB;

    private static SEMESTER_DATA currentSemesterData;

    private static final int INFORMATIONAL_STUDY_DIRECT = 9;
    private static final String COMPUTER = "Компьютер";

    private static final int LECTURE = 1;
    private static final int PRACTICE = 3;

    private static final int LECTURE_COUNT = 2;
    private static final int PRACTICE_COUNT = 2;

    private GridWidget weekDayGW;
    private GridWidget scheduleDetailGW;
    private DBGridModel scheduleDetailGM;
    private VerticalLayout mainVL;
    private String title;



    public ScheduleManual( String task) {
        this.title=task;
        mainVL = new VerticalLayout();
        //   super(task);

        currentSemesterData = CommonUtils.getCurrentSemesterData();
    }


    @Override
    public String getViewName() {
        return null;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return null;
    }


    public VerticalLayout getMainVL() {
        return mainVL;
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainVL.setSizeFull();

        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setSizeFull();

        weekDayGW = new GridWidget(WEEK_DAY.class);
        weekDayGW.setImmediate(true);
        weekDayGW.setSizeFull();
        weekDayGW.addEntityListener(this);
        weekDayGW.setButtonVisible(IconToolbar.REFRESH_BUTTON,true);
        weekDayGW.setButtonVisible(IconToolbar.ADD_BUTTON,false);
        weekDayGW.setButtonVisible(IconToolbar.EDIT_BUTTON,false);
        weekDayGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON,false);
        weekDayGW.setButtonVisible(IconToolbar.DELETE_BUTTON,false);

        mainHL.addComponent(weekDayGW);

        scheduleDetailGW = new GridWidget(SCHEDULE_DETAIL.class);
        scheduleDetailGW.setImmediate(true);
        scheduleDetailGW.setSizeFull();
        scheduleDetailGW.addEntityListener(this);
        scheduleDetailGM = (DBGridModel)scheduleDetailGW.getWidgetModel();
        scheduleDetailGM.setRefreshType(ERefreshType.MANUAL);
        scheduleDetailGM.setDeferredCreate(true);
        scheduleDetailGM.setDeferredDelete(true);
        mainHL.addComponent(scheduleDetailGW);

        QueryModel scheduleDetailTeacherQM = ((FKFieldModel)scheduleDetailGM.getFormModel().getFieldModel("teacher")).getQueryModel();
        scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id" , USERS.class ,"id");
        FromItem vEmployeeFI = scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id", V_EMPLOYEE.class,"id");
        scheduleDetailTeacherQM.addWhere(vEmployeeFI , "employeeType" , ECriteria.EQUAL , EMPLOYEE_TYPE.TEACHER_ID);
        ((FKFieldModel)scheduleDetailGM.getFormModel().getFieldModel("subject")).getQueryModel().addWhere("deleted", ECriteria.EQUAL , false);
        ((FKFieldModel)scheduleDetailGM.getFormModel().getFieldModel("group")).getQueryModel().addWhere("deleted", ECriteria.EQUAL , false);

        semesterDataCB = new ComboBox();
        semesterDataCB.setWidth(300,Unit.PIXELS);
        semesterDataCB.setTextInputAllowed(true);
        semesterDataCB.setFilteringMode(FilteringMode.CONTAINS);
        semesterDataCB.setCaption(getUILocaleUtil().getEntityLabel(SEMESTER_DATA.class));
        QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
        semesterDataQM.addOrderDesc("id");
        BeanItemContainer<SEMESTER_DATA> semesterDataBIC = new BeanItemContainer<>(SEMESTER_DATA.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterDataQM));
        semesterDataCB.setContainerDataSource(semesterDataBIC);

        if(CommonUtils.getCurrentSemesterData()!=null){
            semesterDataCB.setValue(CommonUtils.getCurrentSemesterData());
        }

        semesterDataCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                try{
                    refresh();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mainVL.addComponent(semesterDataCB);
        mainVL.addComponent(mainHL);

    }

    private void refresh() throws Exception {

        if(semesterDataCB.getValue()==null){
            Message.showError(getUILocaleUtil().getMessage("select.semester"));
            return;
        }
        if(weekDayGW.getSelectedEntity()==null){
            Message.showError(getUILocaleUtil().getMessage("select.week.day"));
            return;
        }

        QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        scheduleDetailQM.addWhere("weekDay", ECriteria.EQUAL, weekDayGW.getSelectedEntity().getId());
        scheduleDetailQM.addWhereAnd("semesterData", ECriteria.EQUAL,((SEMESTER_DATA)semesterDataCB.getValue()).getId());
        List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(scheduleDetailQM);
        scheduleDetailGM.setEntities(scheduleDetails);
        scheduleDetailGW.refresh();
    }

    static boolean groupHasEnoughLessons(GROUPS group, SUBJECT subject) throws Exception {
        QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        scheduleDetailQM.addSelect("id", EAggregate.COUNT);
        scheduleDetailQM.addWhere("subject", ECriteria.EQUAL, subject.getId());
        scheduleDetailQM.addWhere("group", ECriteria.EQUAL, group.getId());
        scheduleDetailQM.addWhere("semesterData", ECriteria.EQUAL, currentSemesterData.getId());
        long countOfLessons = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookupItems(scheduleDetailQM);
        return subject.getCreditability().getCredit() == countOfLessons;
    }

    private ROOM getRoom(WEEK_DAY weekDay, LESSON_TIME time, int roomType, int numberOfStudents,
                         boolean notComputer, SEMESTER_DATA semesterData) throws Exception {
        if (LECTURE == roomType) {
            List<ROOM> lectureRooms = getLectureRooms(numberOfStudents);
            for (ROOM room : lectureRooms) {
                if (!scheduleAlreadyHas(room, weekDay, time,semesterData)) {
                    return room;
                }
            }
            return lectureRooms.get(0);
        } else {
            List<ROOM> practiceRooms = getPracticeRooms(numberOfStudents);
            for (ROOM room : practiceRooms) {
                if (!scheduleAlreadyHas(room, weekDay, time,semesterData)) {
                    if (COMPUTER.equals(room.getEquipment()) || notComputer) {
                        return room;
                    }
                }
            }
        }
        return new ROOM();
    }

    private boolean scheduleAlreadyHas(ROOM room, WEEK_DAY weekDay, LESSON_TIME time,SEMESTER_DATA semesterData) throws Exception {
        String sql = "SELECT 1 " +
                "FROM schedule_detail sched " +
                "  INNER JOIN lesson_time time ON time.id = sched.lesson_time_id " +
                "WHERE " +
                "  (sched.room_id = ?1 AND sched.week_day_id = ?2 AND time.lesson_number = ?3 AND sched.semester_data_id=?4)";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, room.getId().getId());
        params.put(2, weekDay.getId().getId());
        params.put(3, time.getLessonNumber());
        params.put(4, semesterData.getId().getId());
        List results = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(
                sql, params);
        return results.size() > 0;
    }

    private List<ROOM> getLectureRooms(int numberOfStudents) throws Exception {
        return getRooms(numberOfStudents, LECTURE);
    }

    private List<ROOM> getPracticeRooms(int numberOfStudents) throws Exception {
        return getRooms(numberOfStudents, PRACTICE);
    }

    private List<ROOM> getRooms(int numberOfStudents, int roomType) throws Exception {
        StringBuilder sqlSB = new StringBuilder("SELECT * " +
                "FROM room " +
                "WHERE room_type_id ");
        if (LECTURE == roomType) {
            sqlSB.append("not in (2,3,6)");
        } else {
            sqlSB.append("!=1");
        }
        sqlSB.append(" AND capacity >= ?1 " +
                "ORDER BY capacity, room_type_id, room_no");
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, numberOfStudents);
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sqlSB.toString(), params,
                ROOM.class);
    }

    private boolean lectureAfterThisTime(GROUPS group, SUBJECT subject, WEEK_DAY weekDay, LESSON_TIME time)
            throws Exception {
        String sql = "SELECT sched.* " +
                "FROM schedule_detail sched " +
                "  INNER JOIN room ON room.id = sched.room_id " +
                "WHERE sched.group_id = ?1 AND sched.subject_id = ?2 AND room.room_type_id not in (2,3,6)";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, group.getId().getId());
        params.put(2, subject.getId().getId());
        List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(sql, params, SCHEDULE_DETAIL.class);
        List<Integer> mins = getMins(scheduleDetails);
        int weekDayOfLecture = mins.get(0);
        int timeOfLecture = mins.get(1);
        int resLecture = Integer.valueOf(weekDayOfLecture + "" + timeOfLecture);
        int res = Integer.valueOf(weekDay.getId() + "" + time.getLessonNumber());
        return res < resLecture;
    }

    private List<Integer> getMins(List<SCHEDULE_DETAIL> schedules) {
        List<Integer> mins = new ArrayList<>(2);
        int week = 5;
        int time = 5;
        for (SCHEDULE_DETAIL schedule : schedules) {
            if (schedule.getWeekDay().getId().getId().intValue() < week) {
                week = schedule.getWeekDay().getId().getId().intValue();
            }
        }
        for (SCHEDULE_DETAIL schedule : schedules) {
            if (schedule.getWeekDay().getId().getId().intValue() == week) {
                if (schedule.getLessonTime().getLessonNumber() < time) {
                    time = schedule.getLessonTime().getLessonNumber();
                }
            }
        }
        mins.add(week);
        mins.add(time);
        return mins;
    }

    private boolean groupAlreadyHasNLessons(WEEK_DAY weekDay, GROUPS group, SUBJECT subject) throws Exception {
        int count;
        StringBuilder sqlSB = new StringBuilder("SELECT count(1) " +
                "FROM schedule_detail sched " +
                "  INNER JOIN room ON room.id = sched.room_id " +
                "WHERE sched.week_day_id = ?1 AND sched.group_id = ?2");
        if (subject == null) {
            count = LECTURE_COUNT;
            sqlSB.append(" AND room.room_type_id not in (2,3,6)");
        } else {
            count = PRACTICE_COUNT;
            sqlSB.append(" AND room.room_type_id != 1");
            sqlSB.append(" and sched.subject_id = ");
            sqlSB.append(subject.getId().getId());
            sqlSB.append(" and lesson_type_id != 1");
        }
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, weekDay.getId().getId());
        params.put(2, group.getId().getId());
        Long size = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookupSingle(sqlSB.toString(), params);
        return size.intValue() == count;
    }

    static boolean scheduleAlreadyHas(EMPLOYEE teacher, GROUPS group, WEEK_DAY weekDay, LESSON_TIME time)
            throws Exception {
        String sql = "SELECT 1 FROM schedule_detail WHERE  " +
                "((teacher_id=?1 and week_day_id=?2 and lesson_time_id=?4) or " +
                "(group_id=?3 and week_day_id=?2 and lesson_time_id=?4))";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, teacher.getId().getId());
        params.put(2, weekDay.getId().getId());
        params.put(3, group.getId().getId());
        params.put(4, time.getId().getId());
        List results = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        return results.size() > 0;
    }

    private List<GROUPS> getGroupsByStream(STREAM stream, SHIFT_STUDY_YEAR shiftStudyYear) throws Exception {
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        FromItem streamGroupFI = groupsQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class, "group");
        groupsQM.addWhere(streamGroupFI, "stream", ECriteria.EQUAL, stream.getId());
        groupsQM.addWhere("deleted", Boolean.FALSE);
        groupsQM.addWhere("studyYear", ECriteria.EQUAL, shiftStudyYear.getStudyYear().getId());
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM);
    }
    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if(ev.getSource().equals(weekDayGW)){
            if(ev.getAction() == EntityEvent.SELECTED){
                if(semesterDataCB.getValue()==null){
                    Message.showError(getUILocaleUtil().getMessage("select.semester"));
                    return;
                }

                try{
                    refresh();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {

        if(source.equals(scheduleDetailGW)){
            if(semesterDataCB.getValue()==null){
                Message.showError(getUILocaleUtil().getMessage("select.semester"));
                return false;
            }
            if(weekDayGW.getSelectedEntity()==null){
                Message.showError(getUILocaleUtil().getMessage("select.week.day"));
                return false;
            }
        }

        return true;
    }



    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if(isNew){
            if(source.equals(scheduleDetailGW)){
                SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) e;
                scheduleDetail.setWeekDay((WEEK_DAY) weekDayGW.getSelectedEntity());
                scheduleDetail.setSemesterData((SEMESTER_DATA) semesterDataCB.getValue());

                try{
                    if(isAllEmpty(scheduleDetail)){
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);
                        refresh();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        }else{

            try{
                SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) e;
                if(isAllEmpty(scheduleDetail)){
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(scheduleDetail);
                    refresh();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }


    private boolean isAllEmpty(SCHEDULE_DETAIL scheduleDetail) throws Exception{
        if(scheduleDetail.getLessonType().getId().getId().longValue()!=LECTURE){
            if(scheduleAlreadyHas(scheduleDetail.getTeacher(), scheduleDetail.getGroup(),scheduleDetail.getWeekDay(),scheduleDetail.getLessonTime())){
                Message.showError(getUILocaleUtil().getMessage("group.or.teacher.not.free.this.time"));
                return false;
            }
        }

        if(scheduleAlreadyHas(scheduleDetail.getRoom(),scheduleDetail.getWeekDay(),scheduleDetail.getLessonTime(),scheduleDetail.getSemesterData())){
            QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            scheduleDetailQM.addWhere("subject" ,ECriteria.EQUAL, scheduleDetail.getSubject().getId());
            scheduleDetailQM.addWhereAnd("lessonType" ,ECriteria.EQUAL, scheduleDetail.getLessonType().getId());
            scheduleDetailQM.addWhereAnd("teacher" ,ECriteria.EQUAL, scheduleDetail.getTeacher().getId());
            scheduleDetailQM.addWhereAnd("lessonTime" ,ECriteria.EQUAL, scheduleDetail.getLessonTime().getId());
            scheduleDetailQM.addWhereAnd("room" ,ECriteria.EQUAL, scheduleDetail.getRoom().getId());
            scheduleDetailQM.addWhereAnd("semesterData" ,ECriteria.EQUAL, scheduleDetail.getSemesterData().getId());
            List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(scheduleDetailQM);
            if(scheduleDetails.size()==0){
                Message.showError(getUILocaleUtil().getMessage("room.not.free.this.time"));
                return false;
            }else{
                for(SCHEDULE_DETAIL sd : scheduleDetails){
                    if(sd.getGroup().getId().getId().longValue()==scheduleDetail.getGroup().getId().getId().longValue()){
                        Message.showError(getUILocaleUtil().getMessage("group.already.has.lesson"));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(entities);
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return true;
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
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return true;
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
