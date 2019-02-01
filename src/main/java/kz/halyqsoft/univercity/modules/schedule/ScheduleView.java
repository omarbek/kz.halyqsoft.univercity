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
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
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
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
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
public class ScheduleView extends AbstractTaskView {

    private VerticalLayout tablesVL = new VerticalLayout();
    protected ComboBox semesterDataCB;

    private static SEMESTER_DATA currentSemesterData;

    private static final int INFORMATIONAL_STUDY_DIRECT = 9;
    private static final String COMPUTER = "Компьютер";

    private static final int LECTURE = 1;
    private static final int PRACTICE = 3;

    private static final int LECTURE_COUNT = 2;
    private static final int PRACTICE_COUNT = 2;

    protected GridWidget weekDayGW;
    protected GridWidget scheduleDetailGW;
    protected DBGridModel scheduleDetailGM;

    public ScheduleView(AbstractTask task) throws Exception {
        super(task);
        currentSemesterData = CommonUtils.getCurrentSemesterData();
    }

    @Override
    public void initView(boolean b) throws Exception {
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        getContent().setSizeFull();
        Button generateLessonsButton = new Button(getUILocaleUtil().getCaption("generate.lessons"));
        generateLessonsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    generateLessons();
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to generate lessons", e);
                }
            }
        });
       // getContent().addComponent(generateLessonsButton);
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
        scheduleDetailGW.setButtonVisible(AbstractToolbar.EDIT_BUTTON , false);
        scheduleDetailGW.addEntityListener(this);
        scheduleDetailGM = (DBGridModel)scheduleDetailGW.getWidgetModel();
        scheduleDetailGM.setRefreshType(ERefreshType.MANUAL);
        scheduleDetailGM.setDeferredCreate(true);
        scheduleDetailGM.setDeferredDelete(true);
        FKFieldModel subjectFM = (FKFieldModel) scheduleDetailGM.getFormModel().getFieldModel("subject");
        scheduleDetailGM.getFormModel().getFieldModel("teacher").setInView(false);
        scheduleDetailGM.getFormModel().getFieldModel("teacher").setInEdit(false);
        mainHL.addComponent(scheduleDetailGW);

        QueryModel<SUBJECT> subjectQM = ((FKFieldModel)scheduleDetailGM.getFormModel().getFieldModel("subject")).getQueryModel();
        subjectQM.addWhere("deleted", ECriteria.EQUAL , false);
        FromItem fi = subjectQM.addJoin(EJoin.INNER_JOIN, "id" , SEMESTER_SUBJECT.class , "subject");
        subjectQM.addWhereAnd(fi, "semesterData" ,ECriteria.EQUAL, CommonUtils.getCurrentSemesterData().getId());
        QueryModel<GROUPS> groupsQM = ((FKFieldModel)scheduleDetailGM.getFormModel().getFieldModel("group")).getQueryModel();
        groupsQM.addWhere("deleted" , ECriteria.EQUAL , false);

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

        getContent().addComponent(semesterDataCB);
        getContent().addComponent(mainHL);

    }

    protected void refresh() throws Exception {

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

    private Date getDateByTime(Date dateByWeekDay, TIME time) {
        TimeUtils clock = new TimeUtils(time);
        if (clock.isError()) return null;
        String hours = clock.getHours();
        String minutes = clock.getMinutes();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateByWeekDay);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
        cal.set(Calendar.MINUTE, Integer.parseInt(minutes));
        return cal.getTime();
    }

    private Date getDateByWeekDay(int week, SEMESTER_DATA currentSemesterData, int weekDay) {
        LocalDate localDate = currentSemesterData.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).
                toLocalDate();
        for (int i = 0; i < week; i++) {
            localDate = localDate.with(TemporalAdjusters.next(DayOfWeek.of(weekDay)));
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    private ROOM getRoom(WEEK_DAY weekDay, LESSON_TIME time, int roomType, int numberOfStudents,
                         boolean notComputer) throws Exception {
        if (LECTURE == roomType) {
            List<ROOM> lectureRooms = getLectureRooms(numberOfStudents);
            for (ROOM room : lectureRooms) {
                if (!scheduleAlreadyHas(room, weekDay, time)) {
                    return room;
                }
            }
            return lectureRooms.get(0);
        } else {
            List<ROOM> practiceRooms = getPracticeRooms(numberOfStudents);
            for (ROOM room : practiceRooms) {
                if (!scheduleAlreadyHas(room, weekDay, time)) {
                    if (COMPUTER.equals(room.getEquipment()) || notComputer) {
                        return room;
                    }
                }
            }
        }
        return new ROOM();
    }

    private boolean scheduleAlreadyHas(ROOM room, WEEK_DAY weekDay, LESSON_TIME time) throws Exception {
        String sql = "SELECT 1 " +
                "FROM schedule_detail sched " +
                "  INNER JOIN lesson_time time ON time.id = sched.lesson_time_id " +
                "WHERE " +
                "  (sched.room_id = ?1 AND sched.week_day_id = ?2 AND time.lesson_number = ?3)";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, room.getId().getId());
        params.put(2, weekDay.getId().getId());
        params.put(3, time.getLessonNumber());
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

    static boolean scheduleAlreadyHasForTwoSubjects(EMPLOYEE teacher, GROUPS group, WEEK_DAY weekDay, LESSON_TIME time)
            throws Exception {
        String sql = "SELECT teacher_id FROM schedule_detail WHERE  " +
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


    private void generateLessons() throws Exception {
        SEMESTER_DATA currentSemesterData = CommonUtils.getCurrentSemesterData();
        if (currentSemesterData != null) {
            QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            scheduleDetailQM.addWhere("semesterData", ECriteria.EQUAL, currentSemesterData.getId());
            List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(scheduleDetailQM);
            for (SCHEDULE_DETAIL scheduleDetail : scheduleDetails) {
                QueryModel<LESSON> lessonQM = new QueryModel<>(LESSON.class);
                lessonQM.addWhere("scheduleDetail", ECriteria.EQUAL, scheduleDetail.getId());
                List<LESSON> lessons = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(lessonQM);
                for (LESSON lesson : lessons) {
                    QueryModel<LESSON_DETAIL> lessonDetailQM = new QueryModel<>(LESSON_DETAIL.class);
                    lessonDetailQM.addWhere("lesson", ECriteria.EQUAL, lesson.getId());
                    List<LESSON_DETAIL> lessonDetails = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(lessonDetailQM);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(lessonDetails);
                }
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(lessons);

                for (int i = 0; i < 15; i++) {
                    LESSON lesson = new LESSON();
                    lesson.setScheduleDetail(scheduleDetail);
                    Date dateByWeekDay = getDateByWeekDay(i + 1, currentSemesterData, scheduleDetail.getWeekDay().
                            getId().getId().intValue());
                    lesson.setLessonDate(dateByWeekDay);
                    lesson.setBeginDate(getDateByTime(dateByWeekDay, scheduleDetail.getLessonTime().getBeginTime()));
                    lesson.setFinishDate(getDateByTime(dateByWeekDay, scheduleDetail.getLessonTime().getEndTime()));
                    lesson.setCanceled(false);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(lesson);

                    QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
                    studentEducationQM.addWhere("groups", ECriteria.EQUAL, scheduleDetail.getGroup().getId());
                    studentEducationQM.addWhereNull("child");
                    List<STUDENT_EDUCATION> studentEducations = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(studentEducationQM);
                    for (STUDENT_EDUCATION studentEducation : studentEducations) {
                        LESSON_DETAIL lessonDetail = new LESSON_DETAIL();
                        lessonDetail.setAttendanceMark(0);
                        lessonDetail.setLesson(lesson);
                        lessonDetail.setStudentEducation(studentEducation);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(lessonDetail);
                    }

                }

            }
        } else {
            Message.showInfo(CommonUtils.getSemesterIsGoingNowLabel().getValue());
        }
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

        return super.preCreate(source, buttonId);
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        super.onCreate(source, e, buttonId);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if(source.equals(scheduleDetailGW)){
            TeacherDialog teacherDialog = new TeacherDialog(this, source, e, isNew, buttonId);
        }
        return false;
    }

    public void deleteStudentSubjects(SCHEDULE_DETAIL scheduleDetail){
        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        studentSubjectQM.addWhere("semesterData" , ECriteria.EQUAL, scheduleDetail.getSemesterData().getId());
        studentSubjectQM.addWhereAnd("deleted" , ECriteria.EQUAL, false);
        FromItem fi = studentSubjectQM.addJoin(EJoin.INNER_JOIN, "subject" , SEMESTER_SUBJECT.class, "id");
        studentSubjectQM.addWhereAnd(fi,"subject" , ECriteria.EQUAL, scheduleDetail.getSubject().getId() );
        try{
            List list = CommonUtils.getQuery().lookup(studentSubjectQM);
            CommonUtils.getQuery().delete(list);
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog("unable to delete student subjects" , e);
        }
    }

    public void deleteStudentTeacherSubjects(SCHEDULE_DETAIL scheduleDetail){
        QueryModel<STUDENT_TEACHER_SUBJECT> studentTeacherSubjectQM = new QueryModel<>(STUDENT_TEACHER_SUBJECT.class);
        studentTeacherSubjectQM.addWhere("semester",ECriteria.EQUAL, TeacherDialog.getSemester(scheduleDetail.getGroup()).getId());
        FromItem fi = studentTeacherSubjectQM.addJoin(EJoin.INNER_JOIN, "studentEducation" , STUDENT_EDUCATION.class , "id");
        studentTeacherSubjectQM.addWhereNullAnd(fi , "child" );
        studentTeacherSubjectQM.addWhereAnd(fi ,"groups" , ECriteria.EQUAL ,scheduleDetail.getGroup().getId());
        FromItem fi2 = studentTeacherSubjectQM.addJoin(EJoin.INNER_JOIN , "teacherSubject" ,TEACHER_SUBJECT.class  , "id");
        studentTeacherSubjectQM.addWhereAnd(fi2 ,"employee" ,ECriteria.EQUAL , scheduleDetail.getTeacher().getId());
        studentTeacherSubjectQM.addWhereAnd(fi2 ,"subject" ,ECriteria.EQUAL , scheduleDetail.getSubject().getId());

        try{
            List<STUDENT_TEACHER_SUBJECT> studentTeacherSubjects = CommonUtils.getQuery().lookup(studentTeacherSubjectQM);
            CommonUtils.getQuery().delete(studentTeacherSubjects);
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog("unable to delete student teacher subject", e);
        }

    }

    public List<STUDENT_SUBJECT> createStudentSubjects(SCHEDULE_DETAIL scheduleDetail){
        SEMESTER_SUBJECT semesterSubject = null;
        QueryModel<SEMESTER_SUBJECT> semesterSubjectQM = new QueryModel<>(SEMESTER_SUBJECT.class);
        semesterSubjectQM.addWhere("semesterData",ECriteria.EQUAL, scheduleDetail.getSemesterData().getId());
        semesterSubjectQM.addWhereAnd("subject",ECriteria.EQUAL, scheduleDetail.getSubject().getId());
        try{
            semesterSubject = (SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterSubjectQM));
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog("No instance in semester_subject with such subject and semester", e);
            return new ArrayList<>();
        }

        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("groups"  , ECriteria.EQUAL ,scheduleDetail.getGroup().getId());
        studentEducationQM.addWhereNullAnd("child");
        List<STUDENT_EDUCATION> studentEducations = null;
        try{
            studentEducations = CommonUtils.getQuery().lookup(studentEducationQM);
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog("Do not found student educations", e);
        }
        List<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
        for(STUDENT_EDUCATION studentEducation : studentEducations){
            STUDENT_SUBJECT studentSubject = new STUDENT_SUBJECT();
            studentSubject.setStudentEducation(studentEducation);
            studentSubject.setSemesterData(scheduleDetail.getSemesterData());
            studentSubject.setRegDate(new Date());
            studentSubject.setSubject(semesterSubject);
            studentSubjects.add(studentSubject);
        }

        for(STUDENT_SUBJECT studentSubject : studentSubjects){
            try{
                QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
                studentSubjectQM.addWhere("semesterData", ECriteria.EQUAL, studentSubject.getSemesterData().getId());
                studentSubjectQM.addWhereAnd("subject", ECriteria.EQUAL, studentSubject.getSubject().getId());
                studentSubjectQM.addWhereAnd("studentEducation", ECriteria.EQUAL, studentSubject.getStudentEducation().getId());
                studentSubjectQM.addWhereAnd("deleted", ECriteria.EQUAL, false);
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterSubjectQM);
                }catch (NoResultException nre){
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentSubject);
                }
            }catch (Exception e){
                CommonUtils.showMessageAndWriteLog("Can not create student subjects", e);
            }

        }

        return studentSubjects;
    }

    protected boolean isAllEmpty(SCHEDULE_DETAIL scheduleDetail) throws Exception{
//        if(scheduleDetail.getLessonType().getId().getId().longValue()!=LECTURE){
//            if(scheduleAlreadyHasForTwoSubjects(scheduleDetail.getTeacher(), scheduleDetail.getGroup(),scheduleDetail.getWeekDay(),scheduleDetail.getLessonTime())){
//                Message.showError(getUILocaleUtil().getMessage("group.or.teacher.not.free.this.time"));
//                return false;
//            }
//        }

        if(scheduleAlreadyHas(scheduleDetail.getRoom(),scheduleDetail.getWeekDay(),scheduleDetail.getLessonTime())){
            QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            scheduleDetailQM.addWhere("subject" ,ECriteria.EQUAL, scheduleDetail.getSubject().getId());
            scheduleDetailQM.addWhereAnd("lessonType" ,ECriteria.EQUAL, scheduleDetail.getLessonType().getId());
            scheduleDetailQM.addWhereAnd("teacher" ,ECriteria.EQUAL, scheduleDetail.getTeacher().getId());
            scheduleDetailQM.addWhereAnd("lessonTime" ,ECriteria.EQUAL, scheduleDetail.getLessonTime().getId());
            scheduleDetailQM.addWhereAnd("room" ,ECriteria.EQUAL, scheduleDetail.getRoom().getId());
            List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(scheduleDetailQM);
            if(scheduleDetails.size()==0){
                Message.showError(getUILocaleUtil().getMessage("room.not.free.this.time"));
                return false;
            }
            else{
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
            List<ID> ids = new ArrayList<>();
            for(Entity  entity: entities){
                if(entity instanceof  SCHEDULE_DETAIL ){
                    SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL)entity;
                    deleteStudentSubjects(scheduleDetail);
                    deleteStudentTeacherSubjects(scheduleDetail);
                    ids.add(scheduleDetail.getId());
                }
            }
            QueryModel<LESSON> lessonQM = new QueryModel<>(LESSON.class);
            if(ids.size()>0){
                lessonQM.addWhereIn("scheduleDetail" , ids);
                lessonQM.addSelect("id");
                List<LESSON> lessonList = CommonUtils.getQuery().lookup(lessonQM);
                List<ID> lessonIds = new ArrayList<>();
                for(LESSON lesson : lessonList){
                    lessonIds.add(lesson.getId());
                }

                if(lessonIds.size()> 0){
                    QueryModel<LESSON_DETAIL> lessonDetailQM = new QueryModel<>(LESSON_DETAIL.class);
                    lessonDetailQM.addWhereIn("lesson" , lessonIds);
                    lessonDetailQM.addSelect("id");
                    List<LESSON_DETAIL> lessonDetails = CommonUtils.getQuery().lookup(lessonDetailQM);
                    if(lessonDetails.size() > 0){
                        String sql = "delete from lesson_detail where id in ( ";
                        for(LESSON_DETAIL ls : lessonDetails){
                            sql+= ls.getId().getId().longValue() +", ";
                        }
                        sql += lessonDetails.get(0).getId().getId().longValue();
                        sql += ")";
                        Map params = new HashMap<Integer, Object>();
                        try{
                            CommonUtils.getQuery().lookupSingle(sql ,params);
                        }catch (Exception e){
                            //Ignored
                        }
                    }
                }
                if(lessonList.size()>0){
                    CommonUtils.getQuery().delete(lessonList);
                }
            }
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(entities);
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
