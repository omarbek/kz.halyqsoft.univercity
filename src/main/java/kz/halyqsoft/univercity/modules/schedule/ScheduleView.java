package kz.halyqsoft.univercity.modules.schedule;//package kz.halyqsoft.univercity.modules.schedule;
//
//import com.vaadin.ui.Button;
//import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
//import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE;
//import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
//import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
//import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK;
//import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
//import kz.halyqsoft.univercity.entity.beans.univercity.view.VFilter;
//import kz.halyqsoft.univercity.entity.beans.univercity.view.VVFilter;
//import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
//import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
//import org.r3a.common.dblink.utils.SessionFacadeFactory;
//import org.r3a.common.entity.beans.AbstractTask;
//import org.r3a.common.entity.query.QueryModel;
//import org.r3a.common.entity.query.where.ECriteria;
//import org.r3a.common.vaadin.view.AbstractTaskView;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Omarbek
// * @created on 26.06.2018
// */
//public class ScheduleView extends AbstractTaskView {
//
//    private static List<VFilter> filters = new ArrayList<>();
//    private static List<SCHEDULE> schedules = new ArrayList<>();
//    private static List<ROOM> rooms=new ArrayList<>();
//    private static List<WEEK_DAY> weekDays=new ArrayList<>();
//
//
//    public ScheduleView(AbstractTask task) throws Exception {
//        super(task);
//    }
//
//    @Override
//    public void initView(boolean b) throws Exception {
//        Button generateButton = new Button();
//        getContent().addComponent(generateButton);
//        generateButton.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                try {
//                    QueryModel<GROUPS> groupQM = new QueryModel<>(GROUPS.class);
//                    List<GROUPS> groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                            lookup(groupQM);
//
//                    QueryModel<V_EMPLOYEE> teacherQM = new QueryModel<>(V_EMPLOYEE.class);
//                    teacherQM.addWhere("employeeType", ECriteria.EQUAL, 2);
//                    List<V_EMPLOYEE> teachers = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                            lookup(teacherQM);
//
//                    QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
//                    List<SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                            lookup(subjectQM);
//
//                    QueryModel<ROOM> roomQM = new QueryModel<>(ROOM.class);
//                    rooms = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                            lookup(roomQM);
//
//                    createFilter(new VFilter(subjects.get(0), groups, 15, 120, teachers.get(0), 108));
//                    createFilter(new VFilter(subjects.get(0), groups, 30, 60, teachers.get(0), 51));
//                    createFilter(new VFilter(subjects.get(0), groups, 30, 30, teachers.get(0), 4));
//
//                    generate(filters);
//
//                    QueryModel<WEEK_DAY> weekDayQM=new QueryModel<>(WEEK_DAY.class);
//                    weekDays=SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                            lookup(weekDayQM);
//                    for (SCHEDULE schedule : schedules) {
////                        for (WEEK_DAY weekDay : weekDays) {
////                            for (int i = 0; i < 5; i++) {
////                                if (weekDay.equals(schedule.getWeekDay()) && ((i + 1) == schedule.getTime())) {
////                                    System.out.println(schedule.getGROUPS());
////                                    System.out.println(schedule.getWeekDay() + " " + schedule.getTime() + " "
////                                            + schedule.getSubject() + " " + schedule.getTeacher() + " " + schedule.getRoom()
////                                            + " " + schedule.getRoomType());
////                                }
////                            }
////                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    private static void generate(List<VFilter> filters) {
//        for (VFilter filter : filters) {
//            for (GROUPS group : filter.getGroups()) {
//                generateSubjectsWithComputer(filter, group);
//
////                if (filter.getLectures() > 0) {
////                    for (Room room : rooms) {
////                        if (!room.getType().equals(RoomType.PRAC)) {
////
////                        }
////                    }
////                }
////                schedules.add(new Schedule(group, ));
//            }
//        }
//    }
//
//    private static void generateSubjectsWithComputer(VFilter filter, GROUPS group) {
//
//        if (filter.getSubject().isNeedComputer()) {
//            if (filter.getLectures() > 0) {
//                for (ROOM room : rooms) {
//                    if (!room.getType().equals(RoomType.PRAC) && room.getCapacity() > filter.getNumberOfStudents()) {
//                        if (choosedDayAndTime(filter, group, room, RoomType.LEC)) {
//                            break;
//                        }
//                    }
//                }
//            }
//            for (Room room : rooms) {
//                if (room.isHasComputer()) {
//                    if (choosedDayAndTime(filter, group, room, RoomType.PRAC)) {
//                        return;
//                    }
//                }
//            }
//        }
//    }
//
//    private static boolean choosedDayAndTime(VFilter filter, GROUPS group, ROOM room, RoomType roomType) {
//        for (WEEK_DAY weekDay : WeekDay.values()) {
//            for (int i = 0; i < 5; i++) {
//                if (RoomType.PRAC.equals(roomType) && added(group, room, filter.getSubject(), filter.getTeacher(), weekDay, i + 1, roomType)) {
//                    return true;
//                }
//                if (RoomType.LEC.equals(roomType) && added(filter.getGROUPSs(), room, filter.getSubject(), filter.getTeacher(), weekDay, i + 1, roomType)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private static boolean added(List<GROUPS> groups, Room room, Subject subject, Teacher teacher, WeekDay weekDay, int time, RoomType roomType) {
//        for (GROUPS group : groups) {
//            for (Schedule schedule : schedules) {
//                if (
//                        (schedule.getTeacher().equals(teacher)
//                                && schedule.getWeekDay().equals(weekDay)
//                                && schedule.getTime() == time)
//                                ||
//                                (schedule.getGROUPS().equals(group)
//                                        && schedule.getWeekDay().equals(weekDay)
//                                        && schedule.getTime() == time)
//                                ||
//                                (schedule.getRoom().equals(room)
//                                        && schedule.getWeekDay().equals(weekDay)
//                                        && schedule.getTime() == time)
//                                ||
//                                (schedule.getSubject().equals(subject)
//                                        && schedule.getWeekDay().equals(weekDay)
//                                        && schedule.getTime() == time)
//                        ) {
//                    return false;
//                }
//            }
//        }
//        for (GROUPS group : groups) {
//            schedules.add(new Schedule(group, room, subject, teacher, weekDay, time, roomType));
//        }
//        return true;
//    }
//
//    private static boolean added(GROUPS group, Room room, Subject subject, Teacher teacher, WeekDay weekDay, int time, RoomType roomType) {
//        for (Schedule schedule : schedules) {
//            if (
//                    (schedule.getTeacher().equals(teacher)
//                            && schedule.getWeekDay().equals(weekDay)
//                            && schedule.getTime() == time)
//                            ||
//                            (schedule.getGROUPS().equals(group)
//                                    && schedule.getWeekDay().equals(weekDay)
//                                    && schedule.getTime() == time)
//                            ||
//                            (schedule.getRoom().equals(room)
//                                    && schedule.getWeekDay().equals(weekDay)
//                                    && schedule.getTime() == time)
//                            ||
//                            (schedule.getSubject().equals(subject)
//                                    && schedule.getWeekDay().equals(weekDay)
//                                    && schedule.getTime() == time)
//                    ) {
//                return false;
//            }
//        }
//        schedules.add(new Schedule(group, room, subject, teacher, weekDay, time, roomType));
//        return true;
//    }
//
//    private static void createFilter(VFilter filter) {
//        filters.add(filter);
//    }
//}
