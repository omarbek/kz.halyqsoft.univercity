package kz.halyqsoft.univercity.modules.teacherloadassign;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_TEACHER_LOAD_ASSIGN_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.*;


/**
 * @author Omarbek
 * @created on 03.07.2018
 */
public class TeacherLoadAssignView extends AbstractTaskView {

    private TEACHER_LOAD_ASSIGN teacherLoadAssign;
    private final String totalHourText;
    private final Label totalHourChairLabel;
    private Button assign;
    private Button save;
    private Button accept;
    private ComboBox chairCB;
    private ComboBox entranceYearCB;
    private CheckBox acceptedCB;

    private double totalHour = 0;
    private final List<GridWidget> gridList = new ArrayList<>();

    private ENTRANCE_YEAR currentYear;
    private SEMESTER_DATA currentSemesterData;

    private static final int DOES_NOT_WORK = 4;
    private static final int TEACHER = 2;
    private static final int STAFF_AND_NOT_IN_STAFF = 3;
    private static final int ALL_LESSONS = 1;
    private static final int LECTURE = 2;
    private static final int PRACTICE = 3;

    public TeacherLoadAssignView(AbstractTask task) throws Exception {
        super(task);
        totalHourText = getUILocaleUtil().getEntityFieldLabel(TEACHER_LOAD_ASSIGN_DETAIL.class, "totalHour");
        totalHourChairLabel = new Label();
        totalHourChairLabel.addStyleName("total-text-13");
        totalHourChairLabel.setValue(totalHourText + " - 0.0");
    }


    @Override
    public void initView(boolean readOnly) throws Exception {
        currentSemesterData = CommonUtils.getCurrentSemesterData();
        if (currentSemesterData != null) {
            HorizontalLayout filterHL = new HorizontalLayout();
            filterHL.setSpacing(true);
            filterHL.setMargin(true);
            filterHL.addStyleName("form-panel");

            Label chairLabel = new Label();
            chairLabel.addStyleName("bold");
            chairLabel.setWidthUndefined();
            chairLabel.setValue(getUILocaleUtil().getEntityFieldLabel(TEACHER_LOAD_CALC.class, "chair"));
            filterHL.addComponent(chairLabel);

            QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
            chairQM.addWhereNotNull("parent");
            chairQM.addWhereAnd("deleted", Boolean.FALSE);
            BeanItemContainer<DEPARTMENT> chairBic = new BeanItemContainer<>(DEPARTMENT.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
            chairCB = new ComboBox();
            chairCB.setContainerDataSource(chairBic);
            chairCB.setImmediate(true);
            chairCB.setNullSelectionAllowed(true);
            chairCB.setTextInputAllowed(true);
            chairCB.setFilteringMode(FilteringMode.STARTSWITH);
            chairCB.setWidth(400, Unit.PIXELS);
            chairCB.setPageLength(0);
            chairCB.addValueChangeListener(new ChairChangeListener());
            filterHL.addComponent(chairCB);

            Label yearLabel = new Label();
            yearLabel.addStyleName("bold");
            yearLabel.setWidthUndefined();
            yearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(TEACHER_LOAD_CALC.class, "year"));
            filterHL.addComponent(yearLabel);

            currentYear = currentSemesterData.getYear();
            QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
            entranceYearQM.addWhere("beginYear", ECriteria.LESS_EQUAL, currentSemesterData.getYear().getBeginYear() + 1);
            entranceYearQM.addWhereAnd("endYear", ECriteria.LESS_EQUAL, currentSemesterData.getYear().getEndYear() + 1);
            entranceYearQM.addOrderDesc("beginYear");
            BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<>(ENTRANCE_YEAR.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(entranceYearQM));
            entranceYearCB = new ComboBox();
            entranceYearCB.setContainerDataSource(entranceYearBIC);
            entranceYearCB.setImmediate(true);
            entranceYearCB.setNullSelectionAllowed(true);
            entranceYearCB.setTextInputAllowed(true);
            entranceYearCB.setFilteringMode(FilteringMode.STARTSWITH);
            entranceYearCB.addValueChangeListener(new YearChangeListener());
            filterHL.addComponent(entranceYearCB);

            Label acceptedLabel = new Label();
            acceptedLabel.addStyleName("bold");
            acceptedLabel.setWidthUndefined();
            acceptedLabel.setValue(getUILocaleUtil().getEntityFieldLabel(TEACHER_LOAD_CALC.class, "accepted"));
            filterHL.addComponent(acceptedLabel);

            acceptedCB = new CheckBox();
            acceptedCB.setEnabled(false);
            filterHL.addComponent(acceptedCB);

            getContent().addComponent(filterHL);
            getContent().setComponentAlignment(filterHL, Alignment.TOP_CENTER);

            HorizontalLayout toolbar = new HorizontalLayout();
            toolbar.setSpacing(true);

            assign = new Button();
            assign.setCaption(getUILocaleUtil().getCaption("assign"));
            assign.setWidth(130, Unit.PIXELS);
            assign.setIcon(new ThemeResource("img/button/arrow_inout.png"));
            assign.addStyleName("assign");
            assign.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    try {
                        assign();
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                }
            });
            toolbar.addComponent(assign);

            save = new Button();
            save.setCaption(getUILocaleUtil().getCaption("save"));
            save.setWidth(120, Unit.PIXELS);
            save.setIcon(new ThemeResource("img/button/ok.png"));
            save.addStyleName("save");
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    try {
                        save();
                        refresh();
                        CommonUtils.showSavedNotification();
                    } catch (Exception ex) {
                        LOG.error("Unable to save changes: ", ex);
                        Message.showInfo(ex.getMessage());
                    }
                }
            });
            toolbar.addComponent(save);

            accept = new Button();
            accept.setCaption(getUILocaleUtil().getCaption("apply"));
            accept.setWidth(120, Unit.PIXELS);
            accept.setIcon(new ThemeResource("img/button/apply.png"));
            accept.addStyleName("save");
            accept.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (teacherLoadAssign != null && teacherLoadAssign.getId() != null) {
                        QueryModel<TEACHER_LOAD_ASSIGN_DETAIL> tlcdQM = new QueryModel<>(TEACHER_LOAD_ASSIGN_DETAIL.class);
                        tlcdQM.addWhere("teacherLoadAssign", ECriteria.EQUAL, teacherLoadAssign.getId());
                        try {
                            List<TEACHER_LOAD_ASSIGN_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                                    CommonEntityFacadeBean.class).lookup(tlcdQM);
                            if (!list.isEmpty()) {
                                String msg = String.format(getUILocaleUtil().
                                                getMessage("after.button.pressed.further.changes.impossible"),
                                        getUILocaleUtil().getCaption("apply"));
                                Message.showConfirm(msg, new AcceptYesListener());
                            } else {
                                Message.showInfo(getUILocaleUtil().getMessage("do.teach.load.assign.first"));
                            }
                        } catch (Exception ex) {
                            LOG.error("Unable to accept teacher load assigning: ", ex);
                        }
                    } else {
                        Message.showInfo(getUILocaleUtil().getMessage("do.teach.load.assign.first"));
                    }
                }
            });
            toolbar.addComponent(accept);

            getContent().addComponent(toolbar);
            getContent().setComponentAlignment(toolbar, Alignment.TOP_CENTER);

            VerticalLayout summary = new VerticalLayout();
            HorizontalLayout summary1 = new HorizontalLayout();
            summary1.setSpacing(true);
            Label summary1Caption = new Label();
            summary1Caption.addStyleName("total-caption-13");
            summary1Caption.setValue(getUILocaleUtil().getCaption("in.total.chair"));
            summary1.addComponent(summary1Caption);
            summary1.addComponent(totalHourChairLabel);
            summary.addComponent(summary1);

            getContent().addComponent(summary);
            getContent().setComponentAlignment(summary, Alignment.TOP_LEFT);
        } else {
            Label semIsNotGoingNowLabel = CommonUtils.getSemesterIsGoingNowLabel();
            getContent().addComponent(semIsNotGoingNowLabel);
            getContent().setComponentAlignment(semIsNotGoingNowLabel, Alignment.MIDDLE_CENTER);
        }
    }

    public void refresh() throws Exception {
        for (GridWidget grid : gridList) {
            getContent().removeComponent(grid);
        }

        gridList.clear();
        totalHour = 0;

        ID chairId = ID.valueOf(-1);
        if (chairCB.getValue() != null) {
            chairId = ((DEPARTMENT) chairCB.getValue()).getId();
        }
        showGridWidget(chairId);

        totalHourChairLabel.setValue(totalHourText + " - " + totalHour);
    }

    private void showGridWidget(ID chairId) throws Exception {
        ID teachLoadAssignId = (teacherLoadAssign != null && teacherLoadAssign.getId() != null) ?
                teacherLoadAssign.getId() : ID.valueOf(-1);
        QueryModel<TEACHER_LOAD_ASSIGN_DETAIL> teacherLoadAssignDetailQM = new QueryModel<>(
                TEACHER_LOAD_ASSIGN_DETAIL.class);
        FromItem emplFI = teacherLoadAssignDetailQM.addJoin(EJoin.INNER_JOIN, "teacher", EMPLOYEE.class, "id");
        FromItem emplDeptFI = emplFI.addJoin(EJoin.INNER_JOIN, "id", EMPLOYEE_DEPT.class,
                "employee");
        teacherLoadAssignDetailQM.addWhere("teacherLoadAssign", ECriteria.EQUAL, teachLoadAssignId);
        teacherLoadAssignDetailQM.addWhere(emplDeptFI, "department", ECriteria.EQUAL, chairId);
        teacherLoadAssignDetailQM.addWhere(emplDeptFI, "employeeType", ECriteria.EQUAL, ID.valueOf(TEACHER));
        teacherLoadAssignDetailQM.addWhere(emplFI, "status", ECriteria.LESS_EQUAL, ID.valueOf(STAFF_AND_NOT_IN_STAFF));
        List<TEACHER_LOAD_ASSIGN_DETAIL> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(teacherLoadAssignDetailQM);
        if (!list.isEmpty()) {
            GridWidget loadGW = new GridWidget(TEACHER_LOAD_ASSIGN_DETAIL.class);
            loadGW.showToolbar(false);

            DBGridModel loadGM = (DBGridModel) loadGW.getWidgetModel();
            loadGM.setTitleVisible(false);
            loadGM.setMultiSelect(false);
            loadGM.setRefreshType(ERefreshType.MANUAL);

            loadGM.setEntities(list);
            loadGW.refresh();

            getContent().addComponent(loadGW);
            getContent().setComponentAlignment(loadGW, Alignment.MIDDLE_CENTER);
            gridList.add(loadGW);
        }
    }

    private void assign() throws Exception {
        if (showMessageIfLoadIsEmpty()) return;

        try {
            if (teacherLoadAssign.getId() == null) {//TODO check
                teacherLoadAssign.setCreated(new Date());
                teacherLoadAssign.setDepartment((DEPARTMENT) chairCB.getValue());
                teacherLoadAssign.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(teacherLoadAssign);
            } else {
                teacherLoadAssign.setUpdated(new Date());
                teacherLoadAssign.setDepartment((DEPARTMENT) chairCB.getValue());
                teacherLoadAssign.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(teacherLoadAssign);
            }

            deletePrevious(teacherLoadAssign);
            run();
            refresh();

        } catch (Exception ex) {
            ex.printStackTrace();//TODO catch
        }
    }

    private void deletePrevious(TEACHER_LOAD_ASSIGN teacherLoadAssign) throws Exception {
        QueryModel<TEACHER_LOAD_ASSIGN_DETAIL> teacherLoadAssignDetailQM = new QueryModel<>(
                TEACHER_LOAD_ASSIGN_DETAIL.class);
        teacherLoadAssignDetailQM.addWhere("teacherLoadAssign", ECriteria.EQUAL, teacherLoadAssign.getId());
        List<TEACHER_LOAD_ASSIGN_DETAIL> teacherLoadAssignDetails = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(teacherLoadAssignDetailQM);
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(teacherLoadAssignDetails);
    }

    private Double subjectCount;
    private Double withoutLectureCount;

    private void run() throws Exception {
        boolean hasUnassignedSubjects = false;
        List<TEACHER_LOAD_ASSIGN_DETAIL> newList = new ArrayList<>();
        try {
            String sql = "SELECT a.* " +
                    "FROM TEACHER_SUBJECT a INNER JOIN EMPLOYEE b ON a.EMPLOYEE_ID = b.ID " +
                    "  INNER JOIN EMPLOYEE_DEPT c ON b.ID = c.EMPLOYEE_ID AND c.DISMISS_DATE IS NULL " +
                    "  INNER JOIN POST on post.ID = c.POST_ID " +
                    "WHERE a.SUBJECT_ID = ?1 AND b.EMPLOYEE_STATUS_ID != ?2" +
                    " order by post.priority";
            Map<Integer, Object> params = new HashMap<>();
            params.put(2, DOES_NOT_WORK);
            for (LOAD_TO_CHAIR loadToChair : getLoads()) {
                SUBJECT subject = loadToChair.getSubject();
                params.put(1, subject.getId().getId());

                Double totalCount = loadToChair.getTotalCount();
                Double onlyLectureCount = loadToChair.getLcCount();
                subjectCount = loadToChair.getTotalCount();
                withoutLectureCount = totalCount - loadToChair.getLcCount() - loadToChair.getExamCount();

                List<TEACHER_SUBJECT> subjectPpsList = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(sql, params, TEACHER_SUBJECT.class);
                int ppsCount = subjectPpsList.size();

                if (ppsCount > 0) {
                    boolean calculated = false;
                    Double employeeHours = 0.0;
                    for (TEACHER_SUBJECT teacherSubject : subjectPpsList) {
                        EMPLOYEE employee = teacherSubject.getEmployee();

                        String employeeSql = "SELECT empl_dept.* " +
                                "FROM employee_dept empl_dept INNER JOIN post post ON empl_dept.post_id = post.id " +
                                "WHERE empl_dept.employee_id = ?1 AND empl_dept.dismiss_date IS NULL " +
                                "      AND post.tp = TRUE " +
                                "ORDER BY post.study_load DESC;";
                        Map<Integer, Object> employeeParams = new HashMap<>();
                        employeeParams.put(1, employee.getId().getId());
                        EMPLOYEE_DEPT employeeDept = null;
                        try {
                            employeeDept = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    lookupSingle(employeeSql, employeeParams, EMPLOYEE_DEPT.class);
                        } catch (NoResultException e) {
                            employeeDept = null;
                        } catch (NonUniqueResultException e) {
                            e.printStackTrace();//TODO catch
                        }
                        if (employeeDept != null && !calculated) {
                            employeeHours = employeeDept.getPost().getStudyLoad().doubleValue();
                            calculated = true;
                        }

                        SEMESTER_PERIOD semesterPeriod = currentSemesterData.getSemesterPeriod();
                        QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
                        FromItem yearFI = semesterQM.addJoin(EJoin.INNER_JOIN, "studyYear", STUDY_YEAR.class,
                                "id");
                        semesterQM.addWhere("semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
                        semesterQM.addWhere(yearFI, "studyYear", ECriteria.EQUAL, loadToChair.getStudyYear());
                        SEMESTER semester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookupSingle(semesterQM);

                        STREAM stream = loadToChair.getStream();

                        if (stream != null) {
                            if (employeeDept != null && employeeDept.isLecturer()) {
                                if (subjectCount > 0) {
                                    if (employeeHours >= totalCount) {
                                        subjectCount -= totalCount;
                                        employeeHours -= totalCount;

                                        TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = getLoadAssignDetail(
                                                ALL_LESSONS, loadToChair, employee, semesterPeriod, semester,
                                                stream, null);
                                        newList.add(loadAssignDetail);
                                    } else {
                                        if (employeeHours >= onlyLectureCount) {
                                            subjectCount -= onlyLectureCount;
                                            employeeHours -= onlyLectureCount;

                                            TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = getLoadAssignDetail(
                                                    LECTURE, loadToChair, employee, semesterPeriod, semester,
                                                    stream, null);
                                            newList.add(loadAssignDetail);
                                        }
                                    }
                                }
                            } else {
                                List<GROUPS> groups = CommonUtils.getGroupsByStream(stream);
                                for (GROUPS group : groups) {
                                    employeeHours = getEmplHours(newList, loadToChair, employeeHours, employee,
                                            semesterPeriod, semester, group);
                                }
                            }
                        } else {
                            GROUPS group = loadToChair.getGroup();
                            employeeHours = getEmplHours(newList, loadToChair, employeeHours, employee,
                                    semesterPeriod, semester, group);
                        }
                    }
                } else {
                    hasUnassignedSubjects = true;
                }
            }

            if (!newList.isEmpty()) {
                List<TEACHER_LOAD_ASSIGN_DETAIL> tmpList = new ArrayList<>();
                for (TEACHER_LOAD_ASSIGN_DETAIL teacherLoadAssignDetail : newList) {
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                create(teacherLoadAssignDetail);
                        tmpList.add(teacherLoadAssignDetail);
                    } catch (Exception ex) {
                        LOG.error("Unable to assign teacher load: ", ex);
                        if (!tmpList.isEmpty()) {
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(tmpList);
                            } catch (Exception ex1) {
                                LOG.error("Unable to delete teacher load assign: ", ex1);
                            }
                        }
                    }
                }
            }

            if (hasUnassignedSubjects) {
                Message.showError(getUILocaleUtil().getMessage("no.teachers.assigned.to.subject"));
            }
        } catch (Exception ex) {
            LOG.error("Unable to assign teacher load: ", ex);
            Message.showError(ex.getMessage());
        }
    }

    private Double getEmplHours(List<TEACHER_LOAD_ASSIGN_DETAIL> newList, LOAD_TO_CHAIR loadToChair, Double employeeHours, EMPLOYEE employee, SEMESTER_PERIOD semesterPeriod, SEMESTER semester, GROUPS group) throws Exception {
        if (subjectCount > 0) {
            if (employeeHours >= withoutLectureCount) {
                subjectCount -= withoutLectureCount;
                employeeHours -= withoutLectureCount;

                TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = getLoadAssignDetail(PRACTICE, loadToChair,
                        employee, semesterPeriod, semester, null, group);
                newList.add(loadAssignDetail);
            }
        }
        return employeeHours;
    }

    private TEACHER_LOAD_ASSIGN_DETAIL getLoadAssignDetail(int lessonType, LOAD_TO_CHAIR loadToChair,
                                                           EMPLOYEE employee, SEMESTER_PERIOD semesterPeriod,
                                                           SEMESTER semester, STREAM stream,
                                                           GROUPS group) throws Exception {
        TEACHER_LOAD_ASSIGN_DETAIL teacherLoadAssignDetail = new TEACHER_LOAD_ASSIGN_DETAIL();
        teacherLoadAssignDetail.setTeacherLoadAssign(teacherLoadAssign);
        teacherLoadAssignDetail.setTeacher(employee);
        SUBJECT subject = loadToChair.getSubject();
        teacherLoadAssignDetail.setSubject(subject);
        teacherLoadAssignDetail.setSemesterPeriod(semesterPeriod);
        teacherLoadAssignDetail.setStream(stream);
        teacherLoadAssignDetail.setGroup(group);
        teacherLoadAssignDetail.setStudyYear(loadToChair.getStudyYear());
        teacherLoadAssignDetail.setSemester(semester);
        teacherLoadAssignDetail.setStudentDiplomaType(loadToChair.getCurriculum().getDiplomaType());
        teacherLoadAssignDetail.setStudentCount(loadToChair.getStudentNumber());
        teacherLoadAssignDetail.setCredit(subject.getCreditability());

        Double lcCount = lessonType != PRACTICE ? loadToChair.getLcCount() : 0.0;
        Double prCount = lessonType != LECTURE ? loadToChair.getPrCount() : 0.0;
        Double lbCount = lessonType != LECTURE ? loadToChair.getLbCount() : 0.0;

        teacherLoadAssignDetail.setLcHour(lcCount);
        teacherLoadAssignDetail.setPrHour(prCount);
        teacherLoadAssignDetail.setLbHour(lbCount);

        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(teacherLoadAssignDetail);

        V_TEACHER_LOAD_ASSIGN_DETAIL teacherLoadAssignDetailView = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(V_TEACHER_LOAD_ASSIGN_DETAIL.class,
                teacherLoadAssignDetail.getId());
        teacherLoadAssignDetail.setWithTeacherHour(teacherLoadAssignDetailView.getWithTeacherHour());
        teacherLoadAssignDetail.setRatingHour(teacherLoadAssignDetailView.getRatingHour());
        teacherLoadAssignDetail.setExamHour(teacherLoadAssignDetailView.getExamHour());
        teacherLoadAssignDetail.setControlHour(teacherLoadAssignDetailView.getControlHour());
        teacherLoadAssignDetail.setCourseWorkHour(teacherLoadAssignDetailView.getCourseWorkHour());
        teacherLoadAssignDetail.setDiplomaHour(teacherLoadAssignDetailView.getDiplomaHour());
        teacherLoadAssignDetail.setPracticeHour(teacherLoadAssignDetailView.getPracticeHour());
        teacherLoadAssignDetail.setMek(teacherLoadAssignDetailView.getMek());
        teacherLoadAssignDetail.setProtectDiplomaHour(teacherLoadAssignDetailView.getProtectDiplomaHour());
        teacherLoadAssignDetail.setTotalHour(teacherLoadAssignDetailView.getTotalHour());

        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(teacherLoadAssignDetail);

        return teacherLoadAssignDetail;
    }

    private void save() throws Exception {
        if (showMessageIfLoadIsEmpty()) return;

        if (!teacherLoadAssign.isAccepted()) {
            List<TEACHER_LOAD_ASSIGN_DETAIL> mergeList = new ArrayList<>();
            for (GridWidget grid : gridList) {
                List<Entity> allEntities = grid.getAllEntities();
                try {
                    for (Entity item : allEntities) {
                        TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetailView = (TEACHER_LOAD_ASSIGN_DETAIL) item;
                        TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(TEACHER_LOAD_ASSIGN_DETAIL.class,
                                loadAssignDetailView.getId());
                        loadAssignDetail.setLbHour(loadAssignDetailView.getLbHour());
                        loadAssignDetail.setLcHour(loadAssignDetailView.getLcHour());
                        loadAssignDetail.setStudentDiplomaType(loadAssignDetailView.getStudentDiplomaType());
                        loadAssignDetail.setSemester(loadAssignDetailView.getSemester());
                        loadAssignDetail.setStream(loadAssignDetailView.getStream());
                        loadAssignDetail.setSemesterPeriod(loadAssignDetailView.getSemesterPeriod());
                        loadAssignDetail.setSubject(loadAssignDetailView.getSubject());
                        loadAssignDetail.setPrHour(loadAssignDetailView.getPrHour());
                        loadAssignDetail.setTeacher(loadAssignDetailView.getTeacher());
                        loadAssignDetail.setTeacherLoadAssign(loadAssignDetailView.getTeacherLoadAssign());
                        loadAssignDetail.setGroup(loadAssignDetailView.getGroup());
                        mergeList.add(loadAssignDetail);
                    }

                    if (!mergeList.isEmpty()) {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mergeList);
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to save changes: ", ex);
                    Message.showError(ex.toString());
                }
            }
        }
    }

    private boolean showMessageIfLoadIsEmpty() throws Exception {
        List<LOAD_TO_CHAIR> loads = getLoads();
        if (loads.isEmpty()) {
            Message.showError(getUILocaleUtil().getMessage("do.teach.load.calc.first"));
            return true;
        }
        return false;
    }

    private List<LOAD_TO_CHAIR> getLoads() throws Exception {
        QueryModel<LOAD_TO_CHAIR> loadQM = new QueryModel<>(LOAD_TO_CHAIR.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, ((DEPARTMENT) chairCB.getValue()).getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(loadQM);
    }

    private class ChairChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent ev) {
            DEPARTMENT chair = (DEPARTMENT) ev.getProperty().getValue();
            if (chair != null) {
                ENTRANCE_YEAR year = (ENTRANCE_YEAR) entranceYearCB.getValue();
                if (year != null) {
                    reset(chair, year);
                }
            }
        }
    }

    private void reset(DEPARTMENT chair, ENTRANCE_YEAR year) {
        try {
            if (!getLoads().isEmpty()) {
                QueryModel<TEACHER_LOAD_ASSIGN> teacherLoadQM = new QueryModel<>(TEACHER_LOAD_ASSIGN.class);
                teacherLoadQM.addWhere("department", ECriteria.EQUAL, ((DEPARTMENT) chairCB.getValue()).getId());
                teacherLoadQM.addWhere("entranceYear", ECriteria.EQUAL, ((ENTRANCE_YEAR) entranceYearCB.getValue()).getId());
                try {
                    teacherLoadAssign = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(teacherLoadQM);
                } catch (NoResultException e) {
                    teacherLoadAssign = null;
                }
                if (teacherLoadAssign != null) {
                    boolean accepted = teacherLoadAssign.isAccepted();
                    acceptedCB.setValue(accepted);
                    assign.setEnabled(!accepted);
                    save.setEnabled(!accepted);
                    accept.setEnabled(!accepted);
                } else {
                    teacherLoadAssign = new TEACHER_LOAD_ASSIGN();
                    assign.setEnabled(true);
                    save.setEnabled(true);
                    accept.setEnabled(true);
                    acceptedCB.setValue(Boolean.FALSE);
                }
            } else {
                teacherLoadAssign = null;
                assign.setEnabled(true);
                save.setEnabled(true);
                accept.setEnabled(true);
                acceptedCB.setValue(Boolean.FALSE);
            }
            refresh();
        } catch (Exception ex) {
            LOG.error("Unable to find teacher load assign: ", ex);
        }
    }

    private class YearChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent ev) {
            ENTRANCE_YEAR year = (ENTRANCE_YEAR) ev.getProperty().getValue();
            if (year != null) {
                DEPARTMENT chair = (DEPARTMENT) chairCB.getValue();
                if (chair != null) {
                    reset(chair, year);
                }
            }
        }
    }

    private class AcceptYesListener extends AbstractYesButtonListener {

        @Override
        public void buttonClick(Button.ClickEvent ev) {
            teacherLoadAssign.setAccepted(true);
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(teacherLoadAssign);
                assign.setEnabled(false);
                save.setEnabled(false);
                accept.setEnabled(false);
                acceptedCB.setValue(Boolean.TRUE);
            } catch (Exception ex) {
                LOG.error("Unable to accept teacher load calculating: ", ex);
                Message.showError(ex.toString());
            }
        }
    }
}

