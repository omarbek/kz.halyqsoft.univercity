package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_TEACHER_LOAD_ASSIGN_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.TimeUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

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
    private ComboBox groupCB;

    private static SEMESTER_DATA currentSemesterData;

    private static final int INFORMATIONAL_STUDY_DIRECT = 9;
    private static final String COMPUTER = "Компьютер";

    private static final int LECTURE = 1;
    private static final int PRACTICE = 3;

    private static final int LECTURE_COUNT = 2;
    private static final int PRACTICE_COUNT = 2;

    public ScheduleView(AbstractTask task) throws Exception {
        super(task);
        currentSemesterData = CommonUtils.getCurrentSemesterData();
    }

    @Override
    public void initView(boolean b) throws Exception {
        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();

        Button generateScheduleButton = new Button(getUILocaleUtil().getCaption("generate.schedule"));
        generateScheduleButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    generateSchedule();
                    refresh(groupCB);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to generate schedule", e);
                }
            }
        });
        buttonsHL.addComponent(generateScheduleButton);

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
        buttonsHL.addComponent(generateLessonsButton);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

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
        groupCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    refresh(groupCB);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to refresh schedule widget", e);
                }
            }
        });
        getContent().addComponent(groupCB);
        getContent().setComponentAlignment(groupCB, Alignment.MIDDLE_CENTER);

        getContent().addComponent(tablesVL);
        getContent().setComponentAlignment(tablesVL, Alignment.MIDDLE_CENTER);
    }

    private void generateLessons() throws Exception {
        SEMESTER_DATA currentSemesterData = CommonUtils.getCurrentSemesterData();
        if (currentSemesterData != null) {
            QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            scheduleDetailQM.addWhere("semesterData", ECriteria.EQUAL, currentSemesterData.getId());
            scheduleDetailQM.addWhere("id", ECriteria.EQUAL, ID.valueOf(3624));
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
                    lesson.setCanceled(true);
                    lesson.setCancelReason("Учитель не пришел на урок");
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

    private void refresh(ComboBox groupCB) throws Exception {
        ScheduleWidget scheduleWidget = new ScheduleWidget(currentSemesterData, (GROUPS) groupCB.getValue());
        scheduleWidget.refresh();
        tablesVL.removeAllComponents();
        tablesVL.addComponent(scheduleWidget);
    }

    private void generateSchedule() throws Exception {
        if (currentSemesterData != null) {
            QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            scheduleDetailQM.addWhere("semesterData", ECriteria.EQUAL, currentSemesterData.getId());
            List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(scheduleDetailQM);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(scheduleDetails);

            //generate
            QueryModel<V_TEACHER_LOAD_ASSIGN_DETAIL> loadAssignDetailQM = new QueryModel<>(
                    V_TEACHER_LOAD_ASSIGN_DETAIL.class);
            FromItem loadAssignDetFI = loadAssignDetailQM.addJoin(EJoin.INNER_JOIN, "id",
                    TEACHER_LOAD_ASSIGN_DETAIL.class, "id");
            FromItem loadAssignFI = loadAssignDetFI.addJoin(EJoin.INNER_JOIN, "teacherLoadAssign",
                    TEACHER_LOAD_ASSIGN.class, "id");
            loadAssignDetailQM.addWhere(loadAssignFI, "entranceYear", ECriteria.EQUAL,
                    currentSemesterData.getYear().getId());
            loadAssignDetailQM.addWhere("semesterPeriod", ECriteria.EQUAL, currentSemesterData.
                    getSemesterPeriod().getId());
            loadAssignDetailQM.addOrderDesc("lcHour");
            loadAssignDetailQM.addOrderDesc("prHour");
            loadAssignDetailQM.addOrderDesc("studentCount");
            List<V_TEACHER_LOAD_ASSIGN_DETAIL> loadAssignDetails = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(loadAssignDetailQM);
            for (V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail : loadAssignDetails) {
                generateByShifts(loadAssignDetail, 1);
                generateByShifts(loadAssignDetail, 2);
            }
        } else {
            Message.showError(CommonUtils.getSemesterIsGoingNowLabel().getValue());
        }
    }

    private void generateByShifts(V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail, Integer shift) throws Exception {
        QueryModel<SHIFT_STUDY_YEAR> shiftQM = new QueryModel<>(SHIFT_STUDY_YEAR.class);
        FromItem shiftFI = shiftQM.addJoin(EJoin.INNER_JOIN, "shift", SHIFT.class, "id");
        shiftQM.addWhere(shiftFI, "name", ECriteria.EQUAL, shift.toString());
        List<SHIFT_STUDY_YEAR> shiftStudyYears = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(shiftQM);
        for (SHIFT_STUDY_YEAR shiftStudyYear : shiftStudyYears) {
            List<GROUPS> groups = getGroupsByStream(loadAssignDetail.getStream(), shiftStudyYear);
            generateSubjects(loadAssignDetail, null, groups, shift);
            for (GROUPS group : groups) {
                generateSubjects(loadAssignDetail, group, groups, shift);
            }
        }
    }

    private void generateSubjects(V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail, GROUPS group,
                                  List<GROUPS> groups, int shift) throws Exception {
        if (loadAssignDetail.getSubject().getStudyDirect().getId().equals(ID.valueOf(
                INFORMATIONAL_STUDY_DIRECT))) {//computer
            addLectureAndPractice(loadAssignDetail, group, false, groups, shift);
        } else {
            addLectureAndPractice(loadAssignDetail, group, true, groups, shift);
        }
    }

    private void addLectureAndPractice(V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail, GROUPS group,
                                       boolean notComputer, List<GROUPS> groups, int shift) throws Exception {
        if (group == null) {
            for (int i = 0; i < loadAssignDetail.getLcHour() / 15; i++) {
                if (choosedDayAndTime(loadAssignDetail, null, notComputer, groups, shift)) {
                    //TODO change choosedDayAndTime to void
                }
            }
        } else {
            for (int i = 0; i < (loadAssignDetail.getPrHour()
                    + loadAssignDetail.getLbHour()) / groups.size() / 15;
                 i++) {
                if (choosedDayAndTime(loadAssignDetail, group, notComputer, groups, shift)) {
                    //TODO change choosedDayAndTime to void
                }
            }
        }
    }

    private boolean choosedDayAndTime(V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail, GROUPS group,
                                      boolean notComputer, List<GROUPS> groups, Integer shift) throws Exception {
        QueryModel<WEEK_DAY> weekDayQM = new QueryModel<>(WEEK_DAY.class);
        List<WEEK_DAY> weekDays = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(weekDayQM);
        QueryModel<LESSON_TIME> lessonTimeQM = new QueryModel<>(LESSON_TIME.class);
        FromItem shiftFI = lessonTimeQM.addJoin(EJoin.INNER_JOIN, "shift", SHIFT.class, "id");
        lessonTimeQM.addWhere(shiftFI, "name", ECriteria.EQUAL, shift.toString());
        List<LESSON_TIME> lessonTimes = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(lessonTimeQM);
        for (WEEK_DAY weekDay : weekDays) {
            for (LESSON_TIME time : lessonTimes) {
                if (group == null) {
                    Integer size = loadAssignDetail.getStudentCount();
                    if (added(groups, loadAssignDetail.getSubject(),
                            loadAssignDetail.getTeacher(), weekDay, time, LECTURE, size, notComputer)) {
                        return true;
                    }
                } else {
                    if ((loadAssignDetail.getPrHour() + loadAssignDetail.getLbHour()) / 15 /
                            (loadAssignDetail.getSubject().getAcademicFormula().getPrCount() +
                                    loadAssignDetail.getSubject().getAcademicFormula().getLbCount()) != 1) {
                        groups = new ArrayList<>();
                        groups.add(group);
                    }
                    V_GROUP groupView = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).
                            lookup(V_GROUP.class, group.getId());
                    if (added(groups, loadAssignDetail.getSubject(),
                            loadAssignDetail.getTeacher(), weekDay, time, PRACTICE,
                            groupView.getStudentCount(), notComputer)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private boolean added(List<GROUPS> groups, SUBJECT subject, EMPLOYEE teacher, WEEK_DAY weekDay,
                          LESSON_TIME time, int roomType, Integer studentCount, boolean notComputer)
            throws Exception {
        for (GROUPS group : groups) {
            if (scheduleAlreadyHas(teacher, group, weekDay, time)) {
                return false;
            }
            if (subject.getLcCount() > 0) {
                if (LECTURE == roomType && groupAlreadyHasNLessons(weekDay, group, null)) {
                    return false;
                }
                if (PRACTICE == roomType && lectureAfterThisTime(group, subject, weekDay, time)) {
                    return false;
                }
            }
            if (groupAlreadyHasNLessons(weekDay, group, subject)) {
                return false;
            }
        }
        ROOM room = getRoom(weekDay, time, roomType, studentCount, notComputer);
        for (GROUPS group : groups) {
            SCHEDULE_DETAIL scheduleDetail = new SCHEDULE_DETAIL();
            scheduleDetail.setGroup(group);
            scheduleDetail.setLessonTime(time);
            scheduleDetail.setLessonType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(LESSON_TYPE.class, ID.valueOf(roomType)));
            scheduleDetail.setRoom(room);
            scheduleDetail.setSemesterData(currentSemesterData);
            scheduleDetail.setSubject(subject);
            scheduleDetail.setTeacher(teacher);
            scheduleDetail.setWeekDay(weekDay);
            if (!groupHasEnoughLessons(group, subject)) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);
            }
        }
        return true;
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

    private List<GROUPS> getGroupsByStream(STREAM stream, SHIFT_STUDY_YEAR shiftStudyYear) throws Exception {
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        FromItem streamGroupFI = groupsQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class, "group");
        groupsQM.addWhere(streamGroupFI, "stream", ECriteria.EQUAL, stream.getId());
        groupsQM.addWhere("deleted", Boolean.FALSE);
        groupsQM.addWhere("studyYear", ECriteria.EQUAL, shiftStudyYear.getStudyYear().getId());
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM);
    }
}
