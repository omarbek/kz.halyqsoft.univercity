package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_TEACHER_LOAD_ASSIGN_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 13.07.2018
 */
public class ScheduleView extends AbstractTaskView {

    private SEMESTER_DATA currentSemesterData;
    private VerticalLayout tablesVL=new VerticalLayout();
    private ComboBox groupCB;

    private static final int LECTURE = 1;
    private static final int PRACTICE = 3;
    private static final int INFORMATIONAL_STUDY_DIRECT = 9;
    private static final String COMPUTER = "Компьютер";

    public ScheduleView(AbstractTask task) throws Exception {
        super(task);
        currentSemesterData = CommonUtils.getCurrentSemesterData();
    }

    @Override
    public void initView(boolean b) throws Exception {
        Button generateButton = new Button("generate");//TODO
        generateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    generate();
                    refresh(groupCB);
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        getContent().addComponent(generateButton);
        getContent().setComponentAlignment(generateButton, Alignment.MIDDLE_CENTER);

        groupCB = new ComboBox();
        groupCB.setTextInputAllowed(true);
        groupCB.setFilteringMode(FilteringMode.CONTAINS);
        groupCB.setCaption(getUILocaleUtil().getEntityLabel(GROUPS.class));
        QueryModel<GROUPS> groupQM = new QueryModel<>(GROUPS.class);
        groupQM.addWhere("deleted", Boolean.FALSE);
        BeanItemContainer<GROUPS> groupBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM));
        groupCB.setContainerDataSource(groupBIC);
        groupCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    refresh(groupCB);
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        getContent().addComponent(groupCB);
        getContent().setComponentAlignment(groupCB, Alignment.MIDDLE_CENTER);

        getContent().addComponent(tablesVL);
        getContent().setComponentAlignment(tablesVL, Alignment.MIDDLE_CENTER);
    }

    private void refresh(ComboBox groupCB) throws Exception {
        ScheduleWidget scheduleWidget = new ScheduleWidget(currentSemesterData,(GROUPS)groupCB.getValue());
        scheduleWidget.refresh();
        tablesVL.removeAllComponents();
        tablesVL.addComponent(scheduleWidget);
    }

    private void generate() throws Exception {
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
                    groups = new ArrayList<>();
                    groups.add(group);
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
            if (scheduleAlreadyHas(teacher, group, weekDay, time, subject)) {
                return false;
            }
            if (roomType == LECTURE && groupAlreadyHasNLectures(weekDay, group, 2)) {
                return false;
            }
            if (roomType == PRACTICE && lectureAfterThisTime(group, subject, weekDay, time)) {
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
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);
        }
        return true;
    }

    private ROOM getRoom(WEEK_DAY weekDay, LESSON_TIME time, int roomType, int numberOfStudents,
                         boolean notComputer) throws Exception {
        if (roomType == LECTURE) {
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
        return getRooms(numberOfStudents, PRACTICE);
    }

    private List<ROOM> getPracticeRooms(int numberOfStudents) throws Exception {
        return getRooms(numberOfStudents, LECTURE);
    }

    private List<ROOM> getRooms(int numberOfStudents, int roomTypeId) throws Exception {
        String sql = "SELECT * " +
                "FROM room " +
                "WHERE room_type_id != ?1 AND capacity >= ?2 " +
                "ORDER BY capacity;";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, roomTypeId);
        params.put(2, numberOfStudents);
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql, params,
                ROOM.class);
    }

    private boolean lectureAfterThisTime(GROUPS group, SUBJECT subject, WEEK_DAY weekDay, LESSON_TIME time)
            throws Exception {
        String sql = "SELECT sched.* " +
                "FROM schedule_detail sched " +
                "  INNER JOIN room ON room.id = sched.room_id " +
                "WHERE sched.group_id = ?1 AND sched.subject_id = ?2 AND room.room_type_id = ?3";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, group.getId().getId());
        params.put(2, subject.getId().getId());
        params.put(3, LECTURE);
        List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(sql, params, SCHEDULE_DETAIL.class);
        int weekDayOfLecture = getMin(scheduleDetails, true);
        int timeOfLecture = getMin(scheduleDetails, false);
        int resLecture = Integer.valueOf(weekDayOfLecture + "" + timeOfLecture);
        int res = Integer.valueOf(weekDay.getId() + "" + time.getLessonNumber());
        return res < resLecture;
    }

    private int getMin(List<SCHEDULE_DETAIL> schedules, boolean isDay) {
        int min = 5;
        for (SCHEDULE_DETAIL schedule : schedules) {
            if (isDay) {
                if (schedule.getWeekDay().getId().getId().intValue() < min) {
                    min = schedule.getWeekDay().getId().getId().intValue();
                }
            } else {
                if (schedule.getLessonTime().getLessonNumber() < min) {
                    min = schedule.getLessonTime().getLessonNumber();
                }
            }
        }
        return min;
    }

    private boolean groupAlreadyHasNLectures(WEEK_DAY weekDay, GROUPS group, int lectureCount) throws Exception {
        String sql = "SELECT count(1) " +
                "FROM schedule_detail sched " +
                "  INNER JOIN room ON room.id = sched.room_id " +
                "WHERE sched.week_day_id = ?1 AND sched.group_id = ?2 AND room.room_type_id = ?3";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, weekDay.getId().getId());
        params.put(2, group.getId().getId());
        params.put(3, LECTURE);
        Long size = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookupSingle(sql, params);
        return size.intValue() == lectureCount;
    }

    private boolean scheduleAlreadyHas(EMPLOYEE teacher, GROUPS group, WEEK_DAY weekDay, LESSON_TIME time,
                                       SUBJECT subject) throws Exception {
        String sql = "SELECT 1 FROM schedule_detail WHERE  " +
                "(teacher_id=?1 and week_day_id=?2 and lesson_time_id=?5) or " +
                "(group_id=?3 and week_day_id=?2 and lesson_time_id=?5) or " +
                "(subject_id=?4 and week_day_id=?2 and lesson_time_id=?5)";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, teacher.getId().getId());
        params.put(2, weekDay.getId().getId());
        params.put(3, group.getId().getId());
        params.put(4, subject.getId().getId());
        params.put(5, time.getId().getId());
        List results = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(
                sql, params);
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
