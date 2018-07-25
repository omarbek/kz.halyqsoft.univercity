package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.TEACHER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Omarbek
 * @created on 24.07.2018
 */
public class ScheduleDialog extends WindowUtils {

    private ScheduleWidget scheduleWidget;
    private TIME time;
    private SEMESTER_DATA semesterData;
    private GROUPS group;
    private LESSON_TIME lessonTime;

    ScheduleDialog(ScheduleWidget scheduleWidget, TIME time, SEMESTER_DATA semesterData, GROUPS group) {
        this.scheduleWidget = scheduleWidget;
        this.time = time;
        this.semesterData = semesterData;
        this.group = group;
        init(1000, 500);
    }

    @Override
    protected String createTitle() {
        return "Schedule";
    }

    @Override
    protected void refresh() throws Exception {
        scheduleWidget.refresh();
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        try {
            QueryModel<LESSON_TIME> lessonTimeQM = new QueryModel<>(LESSON_TIME.class);
            lessonTimeQM.addWhere("beginTime", ECriteria.EQUAL, time.getId());
            lessonTime = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(lessonTimeQM);

            QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
            scheduleDetailQM.addWhere("semesterData", ECriteria.EQUAL, semesterData.getId());
            scheduleDetailQM.addWhere("group", ECriteria.EQUAL, group.getId());
            scheduleDetailQM.addWhere("lessonTime", ECriteria.EQUAL, lessonTime.getId());
            scheduleDetailQM.addOrder("weekDay");

            HorizontalLayout scheduleHL = new HorizontalLayout();
            scheduleHL.setSpacing(true);
            scheduleHL.setSizeFull();

            initLabels(scheduleHL, semesterData.toString(), SEMESTER_DATA.class);
            initLabels(scheduleHL, group.toString(), GROUPS.class);
            initLabels(scheduleHL, lessonTime.toString(), LESSON_TIME.class);

            mainVL.addComponent(scheduleHL);
            mainVL.setComponentAlignment(scheduleHL, Alignment.MIDDLE_CENTER);

            List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(scheduleDetailQM);
            for (SCHEDULE_DETAIL scheduleDetail : scheduleDetails) {
                scheduleHL = new HorizontalLayout();
                scheduleHL.setSpacing(true);
                scheduleHL.setSizeFull();

                QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
                subjectQM.addWhere("deleted", Boolean.FALSE);
                BeanItemContainer<SUBJECT> subjectBIC = new BeanItemContainer<>(SUBJECT.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(subjectQM));

                QueryModel<LESSON_TYPE> lessonTypeQM = new QueryModel<>(LESSON_TYPE.class);
                BeanItemContainer<LESSON_TYPE> lessonTypeBIC = new BeanItemContainer<>(LESSON_TYPE.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(lessonTypeQM));

                QueryModel<WEEK_DAY> weekdayQM = new QueryModel<>(WEEK_DAY.class);
                BeanItemContainer<WEEK_DAY> weekDayBIC = new BeanItemContainer<>(WEEK_DAY.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(weekdayQM));

                QueryModel<ROOM> roomQM = new QueryModel<>(ROOM.class);
                BeanItemContainer<ROOM> roomBIC = new BeanItemContainer<>(ROOM.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomQM));

                QueryModel<EMPLOYEE> teacherQM = new QueryModel<>(EMPLOYEE.class);
                FromItem usersFI = teacherQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "id");
                teacherQM.addWhere(usersFI, "deleted", Boolean.FALSE);
                teacherQM.addWhere("id", ECriteria.MORE, ID.valueOf(2));
                BeanItemContainer<EMPLOYEE> teacherBIC = new BeanItemContainer<>(EMPLOYEE.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(teacherQM));

                ComboBox subjectCB = initComboBox(scheduleHL, subjectBIC, SUBJECT.class);
                subjectCB.setValue(scheduleDetail.getSubject());
                ComboBox teacherCB = initComboBox(scheduleHL, teacherBIC, EMPLOYEE.class);
                teacherCB.setValue(scheduleDetail.getTeacher());
                ComboBox roomCB = initComboBox(scheduleHL, roomBIC, ROOM.class);
                roomCB.setValue(scheduleDetail.getRoom());
                ComboBox weekDayCB = initComboBox(scheduleHL, weekDayBIC, WEEK_DAY.class);
                weekDayCB.setValue(scheduleDetail.getWeekDay());
                ComboBox lessonTypeCB = initComboBox(scheduleHL, lessonTypeBIC, LESSON_TYPE.class);
                lessonTypeCB.setValue(scheduleDetail.getLessonType());
                lessonTypeCB.setReadOnly(true);

                subjectCB.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        QueryModel<EMPLOYEE> teacherQM = new QueryModel<>(EMPLOYEE.class);
                        FromItem usersFI = teacherQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "id");
                        FromItem teacherSubjectFI = teacherQM.addJoin(EJoin.INNER_JOIN, "id", TEACHER_SUBJECT.class, "employee");
                        teacherQM.addWhere(usersFI, "deleted", Boolean.FALSE);
                        teacherQM.addWhere("id", ECriteria.MORE, ID.valueOf(2));
                        teacherQM.addWhere(teacherSubjectFI, "subject", ECriteria.EQUAL, ((SUBJECT) subjectCB.getValue()).getId());

                        try {
                            BeanItemContainer<EMPLOYEE> teacherBIC = new BeanItemContainer<>(EMPLOYEE.class,
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(teacherQM));
                            teacherCB.setContainerDataSource(teacherBIC);
                        } catch (Exception e) {
                            e.printStackTrace();//TODO catch
                        }
                    }
                });

                Button saveButton = CommonUtils.createSaveButton();
                saveButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        try {
                            WEEK_DAY weekDay = (WEEK_DAY) weekDayCB.getValue();
                            EMPLOYEE teacher = (EMPLOYEE) teacherCB.getValue();
                            SUBJECT subject = (SUBJECT) subjectCB.getValue();
                            ROOM room = (ROOM) roomCB.getValue();

                            if (weekDay == null || teacher == null || subject == null || room == null) {
                                Message.showInfo(getUILocaleUtil().getMessage("fill.all.fields"));
                                return;
                            }
                            if (scheduleDetail.getWeekDay().equals(weekDay) && scheduleDetail.getTeacher().equals(teacher)
                                    && scheduleDetail.getSubject().equals(subject) && scheduleDetail.getRoom().equals(room)) {
                                Message.showInfo("you didn't do any change");//TODO
                                return;
                            }

                            //count how many groups a teacher teaches a subject on a week day and time
                            Set<GROUPS> groups = getGroups(scheduleDetail.getTeacher(), scheduleDetail.getSubject(),
                                    scheduleDetail.getWeekDay(), lessonTime, scheduleDetail.getLessonType());
                            boolean addOld = false;
                            for (GROUPS group : groups) {
                                if (ScheduleView.scheduleAlreadyHas(teacher, group, weekDay, lessonTime)) {
                                    Message.showError("this teacher or room is busy at " + weekDay.toString() + " "
                                            + lessonTime.toString());//TODO
                                    addOld = true;
                                    break;
                                }
                                if (ScheduleView.groupHasEnoughLessons(group, subject)) {
                                    Message.showError("this group has enough lessons in " + subject.toString());//TODO
                                    addOld = true;
                                    break;
                                }
                            }
                            for (GROUPS group : groups) {
                                if (!addOld) {
                                    scheduleDetail.setWeekDay(weekDay);
                                    scheduleDetail.setTeacher(teacher);
                                    scheduleDetail.setSubject(subject);
                                    scheduleDetail.setRoom(room);
                                }

                                if (scheduleDetail.getGroup().equals(group)) {
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(scheduleDetail);
                                    if (!addOld) {
                                        CommonUtils.showSavedNotification();
                                    }
                                } else {
                                    scheduleDetail.setId(null);
                                    scheduleDetail.setGroup(group);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);
                                    if (!addOld) {
                                        CommonUtils.showSavedNotification();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();//TODO catch
                        }
                    }
                });
                scheduleHL.addComponent(saveButton);
                scheduleHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

                mainVL.addComponent(scheduleHL);
                mainVL.setComponentAlignment(scheduleHL, Alignment.MIDDLE_CENTER);
            }

        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return mainVL;
    }

    private Set<GROUPS> getGroups(EMPLOYEE teacher, SUBJECT subject, WEEK_DAY weekDay, LESSON_TIME lessonTime,
                                  LESSON_TYPE lessonType) throws Exception {
        Set<GROUPS> groups = new HashSet<>();

        QueryModel<SCHEDULE_DETAIL> scheduleDetailQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        scheduleDetailQM.addWhere("subject", ECriteria.EQUAL, subject.getId());
        scheduleDetailQM.addWhere("lessonType", ECriteria.EQUAL, lessonType.getId());
        scheduleDetailQM.addWhere("teacher", ECriteria.EQUAL, teacher.getId());
        scheduleDetailQM.addWhere("lessonTime", ECriteria.EQUAL, lessonTime.getId());
        scheduleDetailQM.addWhere("semesterData", ECriteria.EQUAL, semesterData.getId());
        List<SCHEDULE_DETAIL> scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(scheduleDetailQM);
        for (SCHEDULE_DETAIL scheduleDetail : scheduleDetails) {
            groups.add(scheduleDetail.getGroup());
        }
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(scheduleDetails);
        return groups;
    }

    private ComboBox initComboBox(HorizontalLayout scheduleHL, BeanItemContainer<? extends Entity> beanItemContainer,
                                  Class<? extends Entity> entityClass) {
        ComboBox comboBox = new ComboBox();
        comboBox.setCaption(getUILocaleUtil().getEntityLabel(entityClass));
        comboBox.setTextInputAllowed(true);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        if (beanItemContainer != null) {
            comboBox.setContainerDataSource(beanItemContainer);
        }

        scheduleHL.addComponent(comboBox);
        scheduleHL.setComponentAlignment(comboBox, Alignment.MIDDLE_CENTER);

        return comboBox;
    }

    private void initLabels(HorizontalLayout scheduleHL, String value, Class<? extends Entity> entityClass) {
        Label label = new Label();
        label.setCaption(getUILocaleUtil().getEntityLabel(entityClass));
        label.setValue(value);
        label.setWidthUndefined();

        scheduleHL.addComponent(label);
        scheduleHL.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
    }
}
