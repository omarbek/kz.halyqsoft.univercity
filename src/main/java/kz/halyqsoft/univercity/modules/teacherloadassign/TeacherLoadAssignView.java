package kz.halyqsoft.univercity.modules.teacherloadassign;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE_DEPT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR;
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
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadinaddon.IntegerCellRenderer;
import org.r3a.common.vaadinaddon.converter.IntegerCellConverter;

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
    private final List<Grid> gridList = new ArrayList<>();

    private ENTRANCE_YEAR currentYear;
    private SEMESTER_DATA currentSemesterData;

    private static final int DOES_NOT_WORK = 4;

    public TeacherLoadAssignView(AbstractTask task) throws Exception {
        super(task);
        totalHourText = getUILocaleUtil().getEntityFieldLabel(V_TEACHER_LOAD_ASSIGN_DETAIL.class, "totalHour");
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

    public void refresh() {
        for (Grid grid : gridList) {
            getContent().removeComponent(grid);
        }

        gridList.clear();
        totalHour = 0;

        ID chairId = ID.valueOf(-1);
        if (chairCB.getValue() != null) {
            chairId = ((DEPARTMENT) chairCB.getValue()).getId();
        }

        String sql = "SELECT " +
                "  a.ID, " +
                "  a.EMPLOYEE_ID, " +
                "  a.EMPLOYEE_TYPE_ID, " +
                "  b.TYPE_NAME EMPLOYEE_TYPE_NAME, " +
                "  a.DEPT_ID, " +
                "  c.DEPT_NAME, " +
                "  c.DEPT_SHORT_NAME, " +
                "  a.POST_ID, " +
                "  d.POST_NAME, " +
                "  a.LIVE_LOAD, " +
                "  a.WAGE_RATE, " +
                "  a.RATE_LOAD, " +
                "  a.HIRE_DATE, " +
                "  a.DISMISS_DATE, " +
                "  a.ADVISER, " +
                "  a.PARENT_ID " +
                "FROM EMPLOYEE_DEPT a INNER JOIN EMPLOYEE_TYPE b ON a.EMPLOYEE_TYPE_ID = b.ID " +
                "  INNER JOIN DEPARTMENT C ON a.DEPT_ID = C.ID " +
                "  INNER JOIN POST D ON a.POST_ID = D.ID " +
                "  INNER JOIN EMPLOYEE e ON a.EMPLOYEE_ID = e.ID " +
                "WHERE a.DEPT_ID = ?1 AND a.EMPLOYEE_TYPE_ID = ?2 AND e.EMPLOYEE_STATUS_ID <= ?3";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, chairId.getId());
        params.put(2, 2);
        params.put(3, 3);

        try {
            List<V_EMPLOYEE_DEPT> vedList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(sql, params, V_EMPLOYEE_DEPT.class);
            for (V_EMPLOYEE_DEPT ved : vedList) {
                V_EMPLOYEE_DEPT ved1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(V_EMPLOYEE_DEPT.class, ved.getId());
                addGrid(ved1);
            }

            totalHourChairLabel.setValue(totalHourText + " - " + totalHour);
        } catch (Exception ex) {
            LOG.error("Unable to refresh teacher load assign: ", ex);
            Message.showError(ex.toString());
        }
    }

    private void addGrid(V_EMPLOYEE_DEPT ved) throws Exception {
        ID teachLoadAssignId = (teacherLoadAssign != null && teacherLoadAssign.getId() != null) ?
                teacherLoadAssign.getId() : ID.valueOf(-1);

        String sql = "SELECT loadView.* " +
                "FROM v_TEACHER_LOAD_ASSIGN_DETAIL loadView " +
                "  INNER JOIN teacher_load_assign_detail load ON load.id = loadView.id " +
                "WHERE load.teacher_load_assign_id = ?1 AND load.teacher_id = ?2";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, teachLoadAssignId.getId());
        params.put(2, ved.getEmployee().getId().getId());

        try {
            List<V_TEACHER_LOAD_ASSIGN_DETAIL> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(sql, params, V_TEACHER_LOAD_ASSIGN_DETAIL.class);
            if (!list.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(ved.getEmployee().getLastName());
                sb.append(" ");
                sb.append(ved.getEmployee().getFirstName().charAt(0));
                sb.append('.');
                if (ved.getEmployee().getMiddleName() != null) {
                    sb.append(ved.getEmployee().getMiddleName().charAt(0));
                    sb.append('.');
                }
                sb.append(", ");
                double wageRate = ved.getWageRate();
                if (wageRate == 1) {
                    sb.append(String.format(getUILocaleUtil().getCaption("wage.rate.1"), wageRate));
                } else {
                    sb.append(String.format(getUILocaleUtil().getCaption("wage.rate.2"), wageRate));
                }
                sb.append(", ");
                sb.append(ved.getPostName());

                Grid grid = new Grid();
                grid.setSizeFull();
                grid.setCaption(sb.toString());
                grid.addStyleName("header-center");
                grid.addColumn("subjectName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "subjectName"));
                grid.addColumn("speciality").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        CURRICULUM.class, "speciality"));
                grid.addColumn("language").setHeaderCaption(getUILocaleUtil().getCaption("language"));
                grid.addColumn("studyYear").setHeaderCaption(getUILocaleUtil().getCaption("study.year.1"));
                grid.addColumn("credit").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "credit")).setWidth(79);
                grid.addColumn("formula").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "formula")).setWidth(80);
                grid.addColumn("semesterPeriodName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "semesterPeriodName")).setWidth(84);
                grid.addColumn("studentCount", Integer.class).setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "studentCount")).setWidth(92).setRenderer(
                        new IntegerCellRenderer()).setConverter(new IntegerCellConverter());
                grid.addColumn("lcHour").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "lcHour"));
                grid.addColumn("lbHour").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "lbHour"));
                grid.addColumn("prHour").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "prHour"));
                grid.addColumn("totalHour").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, "totalHour"));
                grid.setSelectionMode(Grid.SelectionMode.NONE);
                grid.setHeightMode(HeightMode.ROW);
                if (list.size() < 5) {
                    grid.setHeightByRows(list.size() + 1);
                } else {
                    grid.setHeightByRows(5);
                }
                Grid.HeaderRow hr = grid.prependHeaderRow();
                Grid.HeaderCell hc = hr.join(hr.getCell("lcHour"));
                hc.setText(getUILocaleUtil().getCaption("lecture"));
                hc = hr.join(hr.getCell("lbHour"));
                hc.setText(getUILocaleUtil().getCaption("laboratory"));
                hc = hr.join(hr.getCell("prHour"));
                hc.setText(getUILocaleUtil().getCaption("seminar"));

                hr = grid.prependHeaderRow();
                hc = hr.join(hr.getCell("lcHour"), hr.getCell("lbHour"), hr.getCell("prHour"));
                hc.setText(getUILocaleUtil().getCaption("class.type.hour"));

                Grid.FooterRow fr = grid.addFooterRowAt(0);
                fr.getCell("subjectName").setText(getUILocaleUtil().getCaption("in.total"));

                BeanItemContainer<V_TEACHER_LOAD_ASSIGN_DETAIL> bic = new BeanItemContainer<>(
                        V_TEACHER_LOAD_ASSIGN_DETAIL.class, list);
                grid.setContainerDataSource(bic);
                grid.setFrozenColumnCount(1);

                double totalHourTeacher = 0.0;
                for (V_TEACHER_LOAD_ASSIGN_DETAIL vtlad : list) {
                    totalHourTeacher += vtlad.getTotalHour();
                    totalHour += totalHourTeacher;
                }

                fr.getCell("totalHour").setText(String.valueOf(totalHourTeacher));

                getContent().addComponent(grid);
                getContent().setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
                gridList.add(grid);
            }
        } catch (Exception ex) {
            LOG.error("Unable to load teacher load assign: ", ex);
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

            run();
            refresh();

        } catch (Exception ex) {
            ex.printStackTrace();//TODO catch
        }
    }

    private void run() throws Exception {
        boolean hasUnassignedSubjects = false;
        List<TEACHER_LOAD_ASSIGN_DETAIL> newList = new ArrayList<>();
        try {
            String sql = "SELECT a.* " +
                    "FROM TEACHER_SUBJECT a INNER JOIN EMPLOYEE b ON a.EMPLOYEE_ID = b.ID " +
                    "  INNER JOIN EMPLOYEE_DEPT c ON b.ID = c.EMPLOYEE_ID AND c.DISMISS_DATE IS NULL " +
                    "WHERE a.SUBJECT_ID = ?1 AND b.EMPLOYEE_STATUS_ID != ?2";
            Map<Integer, Object> params = new HashMap<>();
            params.put(2, DOES_NOT_WORK);
            for (V_LOAD_TO_CHAIR loadToChair : getLoads()) {
                SUBJECT subject = loadToChair.getSubject();
                params.put(1, subject.getId().getId());

                Double subjectHours = loadToChair.getTotalCount();
                if (subjectHours > 0) {
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
                                employeeDept = SessionFacadeFactory.getSessionFacade(
                                        CommonEntityFacadeBean.class).lookupSingle(employeeSql, employeeParams,
                                        EMPLOYEE_DEPT.class);
                            } catch (NonUniqueResultException e) {
                                e.printStackTrace();//TODO catch
                            }
                            if (!calculated) {
                                employeeHours = employeeDept.getPost().getStudyLoad().doubleValue();
                                calculated = true;
                            }

                            SEMESTER_PERIOD semesterPeriod = currentSemesterData.getSemesterPeriod();
                            QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
                            FromItem yearFI = semesterQM.addJoin(EJoin.INNER_JOIN, "studyYear", STUDY_YEAR.class, "id");
                            semesterQM.addWhere("semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
                            semesterQM.addWhere(yearFI, "studyYear", ECriteria.EQUAL, loadToChair.getStudyYear());
                            SEMESTER semester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    lookupSingle(semesterQM);

                            String streams = loadToChair.getStream();
                            StringTokenizer stringTokenizer = new StringTokenizer(streams, ",");
                            STREAM stream = null;
                            while (stringTokenizer.hasMoreElements()) {
                                String groupName = stringTokenizer.nextElement().toString();
                                String streamSql = "SELECT " +
                                        "  t0.* " +
                                        "FROM STREAM t0 INNER JOIN STREAM_GROUP t1 ON t0.ID = t1.STREAM_ID " +
                                        "  INNER JOIN GROUPS t2 ON t1.GROUP_ID = t2.ID " +
                                        "WHERE t2.name = ?1 AND t0.NAME IS NOT NULL";
                                Map<Integer, Object> streamParams = new HashMap<>();
                                streamParams.put(1, groupName);
                                try {
                                    stream = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                            lookupSingle(streamSql, streamParams, STREAM.class);
                                } catch (NoResultException e) {
                                    stream = null;
                                }
                                if (stream != null) {
                                    break;
                                }
                            }

                            if (stream != null) {
                                if (employeeDept.isLecturer()) {
                                    TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = getLoadAssignDetail(loadToChair,
                                            employee, semesterPeriod, semester, stream, null);

                                    V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetailView = SessionFacadeFactory.
                                            getSessionFacade(CommonEntityFacadeBean.class).lookup(
                                            V_TEACHER_LOAD_ASSIGN_DETAIL.class,
                                            loadAssignDetail.getId());
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                            delete(loadAssignDetail);

                                    Double currentHour = loadAssignDetailView.getTotalHour();
                                    if (employeeHours >= currentHour) {
                                        subjectHours -= currentHour;
                                        employeeHours -= currentHour;

                                        newList.add(loadAssignDetail);
                                    } else {
                                        Double onlyLectureHour = loadToChair.getLcCount();
                                        if (employeeHours >= onlyLectureHour) {
                                            subjectHours -= onlyLectureHour;
                                            employeeHours -= onlyLectureHour;

                                            loadAssignDetail.setLbHour(0.0);
                                            loadAssignDetail.setPrHour(0.0);
                                            newList.add(loadAssignDetail);
                                        }
                                    }
                                } else {
                                    QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
                                    FromItem streamGroupFI = groupsQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class,
                                            "group");
                                    groupsQM.addWhere(streamGroupFI, "stream", ECriteria.EQUAL, stream.getId());
                                    List<GROUPS> groups = SessionFacadeFactory.getSessionFacade(
                                            CommonEntityFacadeBean.class).
                                            lookup(groupsQM);
                                    for (GROUPS group : groups) {
                                        TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = getLoadAssignDetail(loadToChair,
                                                employee, semesterPeriod, semester, null, group);

                                        V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetailView = SessionFacadeFactory.
                                                getSessionFacade(CommonEntityFacadeBean.class).lookup(
                                                V_TEACHER_LOAD_ASSIGN_DETAIL.class, loadAssignDetail.getId());
                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                                delete(loadAssignDetail);

                                        Double withoutLectureHour = loadAssignDetailView.getTotalHour() -
                                                loadToChair.getLcCount();
                                        if (employeeHours >= withoutLectureHour) {
                                            subjectHours -= withoutLectureHour;
                                            employeeHours -= withoutLectureHour;

                                            loadAssignDetail.setLcHour(0.0);
                                            newList.add(loadAssignDetail);
                                        }
                                    }
                                }
                            } else {
                                LOG.info("No stream in chair:" + chairCB.getValue().toString()
                                        + ", year:" + entranceYearCB.getValue().toString()
                                        + ", teacher:" + employee.toString()
                                        + ", subject:" + subject.toString());
                            }
                        }
                    } else {
                        hasUnassignedSubjects = true;
                    }
                }
            }

            if (!newList.isEmpty()) {
                List<TEACHER_LOAD_ASSIGN_DETAIL> tmpList = new ArrayList<>();
                for (TEACHER_LOAD_ASSIGN_DETAIL teacherLoadAssignDetail : newList) {
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(teacherLoadAssignDetail);
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

    private TEACHER_LOAD_ASSIGN_DETAIL getLoadAssignDetail(V_LOAD_TO_CHAIR loadToChair, EMPLOYEE employee,
                                                           SEMESTER_PERIOD semesterPeriod, SEMESTER semester,
                                                           STREAM stream, GROUPS group) throws Exception {
        TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetail = new TEACHER_LOAD_ASSIGN_DETAIL();
        loadAssignDetail.setTeacherLoadAssign(teacherLoadAssign);
        loadAssignDetail.setTeacher(employee);
        loadAssignDetail.setSubject(loadToChair.getSubject());
        loadAssignDetail.setSemesterPeriod(semesterPeriod);
        loadAssignDetail.setStream(stream);
        loadAssignDetail.setLcHour(loadToChair.getLcCount());
        loadAssignDetail.setLbHour(loadToChair.getLbCount());
        loadAssignDetail.setPrHour(loadToChair.getPrCount());
        loadAssignDetail.setSemester(semester);
        loadAssignDetail.setStudentDiplomaType(loadToChair.getCurriculum().getDiplomaType());
        loadAssignDetail.setStream(stream);
        loadAssignDetail.setGroup(group);
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(loadAssignDetail);
        return loadAssignDetail;
    }

    private void save() throws Exception {
        if (showMessageIfLoadIsEmpty()) return;

        if (!teacherLoadAssign.isAccepted()) {
            List<TEACHER_LOAD_ASSIGN_DETAIL> mergeList = new ArrayList<>();
            for (Grid grid : gridList) {
                Container.Indexed gridData = grid.getContainerDataSource();
                try {
                    for (Object item : gridData.getItemIds()) {
                        V_TEACHER_LOAD_ASSIGN_DETAIL loadAssignDetailView = (V_TEACHER_LOAD_ASSIGN_DETAIL) item;
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
        List<V_LOAD_TO_CHAIR> loads = getLoads();
        if (loads.isEmpty()) {
            Message.showError(getUILocaleUtil().getMessage("do.teach.load.calc.first"));
            return true;
        }
        return false;
    }

    private List<V_LOAD_TO_CHAIR> getLoads() throws Exception {
        QueryModel<V_LOAD_TO_CHAIR> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR.class);
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

