package kz.halyqsoft.univercity.modules.userarrival;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.userarrival.subview.GroupAttendance;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class UserArrivalView extends AbstractTaskView implements EntityListener {

    private HorizontalSplitPanel mainHSP;
    private HorizontalLayout mainHL, secondHL;
    private VerticalLayout tableVL;
    private GridWidget employeeGW;
    private GridWidget employeeByDepartmentGW;
    private USER_TYPE userType;
    private DateField date;
    private HorizontalLayout buttonsHL;
    private Long depID;

    public UserArrivalView(AbstractTask task) throws Exception {
        super(task);
        userType = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(USER_TYPE.class, ID.valueOf(UserType.STUDENT_INDEX));
    }

    @Override
    public void initView(boolean b) throws Exception {

        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(20);

        mainHL = new HorizontalLayout();
        mainHL.setSpacing(true);
        mainHL.setSizeFull();

        final TreeTable menuTT = new TreeTable();

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        String main = getUILocaleUtil().getCaption("main");
        String studentAttendance = getUILocaleUtil().getCaption("studentAttendance");
        String employeeAttendance = getUILocaleUtil().getCaption("employeeAttendance");
        String latecomers = getUILocaleUtil().getCaption("latecomers");
        String absent = getUILocaleUtil().getCaption("absent");
        String yearlyAttendance = getUILocaleUtil().getCaption("yearlyAttendance");
        hierarchicalContainer.addItem(main);
        hierarchicalContainer.addItem(studentAttendance);
        hierarchicalContainer.addItem(employeeAttendance);
        hierarchicalContainer.addItem(latecomers);
        hierarchicalContainer.addItem(absent);
        hierarchicalContainer.addItem(yearlyAttendance);

        menuTT.setContainerDataSource(hierarchicalContainer);
        menuTT.setSizeFull();
        menuTT.addStyleName("schedule");
        menuTT.setSelectable(true);
        menuTT.setMultiSelect(false);
        menuTT.setNullSelectionAllowed(false);
        menuTT.setImmediate(true);
        menuTT.setColumnReorderingAllowed(false);
        menuTT.setPageLength(20);
        MenuColumn menuColumn = new MenuColumn();
        menuTT.addGeneratedColumn("user arrival", menuColumn);
        menuTT.setColumnHeader("user arrival", getUILocaleUtil().getCaption("attendanceHeader"));

        menuTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                        mainHSP.removeComponent(mainHL);
                        mainHL.removeAllComponents();
                        if (main.equals(event.getProperty().getValue().toString())) {
                            setStudents();
                            setLaters();
                            setEmployees();
                            setNoCards();
                        }else if (employeeAttendance.equals(event.getProperty().getValue().toString())) {
                            date = new DateField();
                            Date currentDate = new Date();
                            date.setValue(currentDate);
                            date.addValueChangeListener(new Property.ValueChangeListener() {
                                @Override
                                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                                    if (date.getValue() != null) {
                                        DateFormat dateFormat = new SimpleDateFormat();
                                        ((DBGridModel) employeeGW.getWidgetModel()).setEntities(getList(dateFormat.format(date.getValue())));
                                        if(employeeByDepartmentGW!=null){
                                            ((DBGridModel) employeeByDepartmentGW.getWidgetModel()).
                                                    setEntities(getEmployee(dateFormat.format(date.getValue())));
                                        }
                                    } else {
                                        Message.showInfo(getUILocaleUtil().getMessage("error.date"));
                                    }
                                }
                            });
                            setDepartmentInfo();
                        }else if(studentAttendance.equalsIgnoreCase(event.getProperty().getValue().toString())){
                            GroupAttendance groupAttendance = new GroupAttendance();

                            mainHL.addComponent(groupAttendance.getMainVL());
                        }
                        mainHSP.addComponent(mainHL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void setNoCards() {
                Panel noCardButton = new Panel("<html><b>"+ getUILocaleUtil().getCaption("noCardButton") +"</b><br>" +
                        "0%<br>" +
                        "(0 из 3209)</html>");
                noCardButton.setIcon(new ThemeResource("img/card.png"));
                noCardButton.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("no card");
                    }
                });
                mainHL.addComponent(noCardButton);
                mainHL.setComponentAlignment(noCardButton, Alignment.MIDDLE_CENTER);
            }

            private void setEmployees() throws Exception {
                QueryModel<V_EMPLOYEE> employeeQM = new QueryModel<>(V_EMPLOYEE.class);
                employeeQM.addSelect("code", EAggregate.COUNT);
                employeeQM.addWhere("deleted", Boolean.FALSE);
                Long allEmployees = (Long) SessionFacadeFactory.
                        getSessionFacade(CommonEntityFacadeBean.class).lookupItems(employeeQM);

                String sql = "SELECT count(1) " +
                        "FROM user_arrival arriv INNER JOIN v_employee empl ON empl.id = arriv.user_id " +
                        "WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                        "      AND arriv.created = (SELECT max(max_arriv.created) " +
                        "                           FROM user_arrival max_arriv " +
                        "                           WHERE max_arriv.user_id = arriv.user_id) " +
                        "      AND come_in = TRUE";
                Long inEmployees = (Long) SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
                long inPercent = inEmployees * 100 / allEmployees;

                Panel employeesPanel = new Panel("<b>"+ getUILocaleUtil().getCaption("employeesPanel")+" - " + allEmployees + "</b><br>" +
                        ""+ getUILocaleUtil().getCaption("attendanceOf")+" - " + inEmployees + " (" + inPercent + "%)<br>" +
                        ""+ getUILocaleUtil().getCaption("absentOf")+" - " + (allEmployees - inEmployees) + " (" + (100 - inPercent) + "%)" +
                        "<br>");
                employeesPanel.setIcon(new ThemeResource("img/employee.png"));
                employeesPanel.setHeight(155, Unit.PIXELS);
                employeesPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("employees");
                    }
                });
                mainHL.addComponent(employeesPanel);
                mainHL.setComponentAlignment(employeesPanel, Alignment.MIDDLE_CENTER);
            }

            private void setLaters() {
                Panel latersPanel = new Panel("<html><b>"+ getUILocaleUtil().getCaption("latersPanel") +"</b><br>" +
                        "9.8%<br>" +
                        "(313 из 3209)</html>");
                latersPanel.setIcon(new ThemeResource("img/clock.png"));
                latersPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("laters");
                    }
                });
                mainHL.addComponent(latersPanel);
                mainHL.setComponentAlignment(latersPanel, Alignment.MIDDLE_CENTER);
            }

            private void setStudents() throws Exception {
                QueryModel<V_STUDENT> studentQM = new QueryModel<>(V_STUDENT.class);
                studentQM.addSelect("userCode", EAggregate.COUNT);
                studentQM.addWhere("deleted", Boolean.FALSE);
                Long allStudents = (Long) SessionFacadeFactory.
                        getSessionFacade(CommonEntityFacadeBean.class).lookupItems(studentQM);

                String sql = "SELECT count(1) " +
                        "FROM user_arrival arriv INNER JOIN v_student stu ON stu.id = arriv.user_id " +
                        "WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                        "      AND arriv.created = (SELECT max(max_arriv.created) " +
                        "                           FROM user_arrival max_arriv " +
                        "                           WHERE max_arriv.user_id = arriv.user_id) " +
                        "      AND come_in = TRUE";
                Long inStudents = (Long) SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
                long inPercent = inStudents * 100 / allStudents;

                Panel studentsPanel = new Panel("<b>"+ getUILocaleUtil().getCaption("studentsPanel") +" - " + allStudents + "</b><br>" +
                        ""+ getUILocaleUtil().getCaption("attendanceOf")+" - " + inStudents + " (" + inPercent + "%)<br>" +
                        ""+ getUILocaleUtil().getCaption("absentOf")+" - " + (allStudents - inStudents) + " (" + (100 - inPercent) + "%)" +
                        "<br>");
                studentsPanel.setIcon(new ThemeResource("img/student.png"));
                studentsPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("Students");
                    }
                });
                mainHL.addComponent(studentsPanel);
                mainHL.setComponentAlignment(studentsPanel, Alignment.MIDDLE_CENTER);
            }
        });
        mainHSP.addComponent(menuTT);
        getContent().addComponent(mainHSP);
    }



    private void setDepartmentInfo() {

        employeeGW = new GridWidget(VDepartmentInfo.class);
        employeeGW.setCaption(getUILocaleUtil().getCaption("employeeGW"));
        employeeGW.setImmediate(true);
        employeeGW.showToolbar(false);
        employeeGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        employeeGW.addEntityListener(UserArrivalView.this);

        DBGridModel employeeGM = (DBGridModel) employeeGW.getWidgetModel();
        employeeGM.setTitleVisible(false);
        employeeGM.setMultiSelect(false);
        employeeGM.setEntities(getList(date.getValue().toString()));
        employeeGM.setRefreshType(ERefreshType.MANUAL);

        tableVL = new VerticalLayout();
        tableVL.addComponent(date);
        tableVL.setComponentAlignment(date, Alignment.MIDDLE_RIGHT);
        tableVL.addComponent(employeeGW);
        mainHL.addComponent(tableVL);
    }

    public List<VDepartmentInfo> getList(String date) {

        List<VDepartmentInfo> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT  dep.DEPT_NAME," +
                " count(empl.dept_id)," +
                " count(user_id)," +
                "dep.id" +
                " FROM v_employee empl" +
                " RIGHT JOIN department dep ON  dep.id = empl.dept_id " +
                " LEFT join (SELECT\n" +
                "               arriv.user_id,\n" +
                "               count(arriv.user_id) as cameCount\n" +
                "             FROM user_arrival arriv\n" +
                "             WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'" + date + "')\n" +
                "                   AND arriv.created = (SELECT max(max_arriv.created)\n" +
                "                                        FROM user_arrival max_arriv\n" +
                "                                        WHERE max_arriv.user_id = arriv.user_id)\n" +
                "                   AND come_in = TRUE\n" +
                "             GROUP BY arriv.user_id)arriv on arriv.user_id=empl.id" +
                " GROUP BY  dep.dept_name,dep.id";

        try {


            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VDepartmentInfo ve = new VDepartmentInfo();
                    ve.setDeptName((String) oo[0]);
                    ve.setCount((long) oo[1]);
                    ve.setIsPresent((long) oo[2]);
                    if (ve.getCount() != 0 && ve.getIsPresent() != 0) {
                        ve.setPercantage((ve.getIsPresent() * 100) / ve.getCount());
                    } else {
                        ve.setPercantage(0);
                    }
                    ve.setDepartmentID((Long) oo[3]);
                    list.add(ve);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }
        refreshList(list);
        return list;
    }

    private void refreshList(List<VDepartmentInfo> list) {
        ((DBGridModel) employeeGW.getWidgetModel()).setEntities(list);
        try {
            employeeGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh department list", ex);
        }
    }

    private void refreshEmployeeList(List<VEmployeeInfo> employeelist) {
        ((DBGridModel) employeeByDepartmentGW.getWidgetModel()).setEntities(employeelist);
        try {
            employeeByDepartmentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh employee list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {

        if (ev.getSource().equals(employeeGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {

                mainHL.removeAllComponents();
                employeeByDepartmentGW = new GridWidget(VEmployeeInfo.class);
                employeeByDepartmentGW.setCaption(getUILocaleUtil().getCaption("employeeByDepartmentGW"));
                employeeByDepartmentGW.setImmediate(true);
                employeeByDepartmentGW.showToolbar(false);
                employeeByDepartmentGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);

                DBGridModel employeeByDepGM = (DBGridModel) employeeByDepartmentGW.getWidgetModel();
                employeeByDepGM.setTitleVisible(false);
                employeeByDepGM.setMultiSelect(false);
                employeeByDepGM.setEntities(getEmployee(date.getValue().toString()));
                employeeByDepGM.setRefreshType(ERefreshType.MANUAL);

                Button backButton = new Button(getUILocaleUtil().getCaption("backButton"));
                backButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        tableVL.removeAllComponents();
                        mainHL.removeAllComponents();
                        setDepartmentInfo();
                    }
                });

                tableVL = new VerticalLayout();
                secondHL = new HorizontalLayout();
                secondHL.setSizeFull();
                secondHL.addComponent(backButton);
                secondHL.setComponentAlignment(backButton, Alignment.MIDDLE_LEFT);
                secondHL.addComponent(date);
                secondHL.setComponentAlignment(date, Alignment.MIDDLE_RIGHT);
                tableVL.addComponent(secondHL);
                tableVL.addComponent(employeeByDepartmentGW);
                mainHL.addComponent(tableVL);
            }
        }
    }

    public List<VEmployeeInfo> getEmployee(String date) {
        List<VEmployeeInfo> emplList = new ArrayList<>();

        if(employeeGW.getSelectedEntity()!=null) {
            depID = ((VDepartmentInfo) employeeGW.getSelectedEntity()).getDepartmentID();
        }
        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT\n" +
                "                trim(empl.LAST_NAME || ' ' || empl.FIRST_NAME || ' ' || coalesce(empl.MIDDLE_NAME, '')) FIO,\n" +
                "                empl.code,\n" +
                "                arriv.created,\n" +
                "                arrivF.created as false\n" +
                "                FROM v_employee empl\n" +
                "                  left join (SELECT\n" +
                "                     arriv.created,\n" +
                "                               arriv.user_id\n" +
                "                       FROM user_arrival arriv\n" +
                "                       WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'" + date + "')\n" +
                "                       AND come_in = TRUE\n" +
                "                       GROUP BY arriv.created,arriv.user_id)arriv on arriv.user_id=empl.id\n" +
                "                  left join (SELECT\n" +
                "                               arrivF.created,\n" +
                "                               arrivF.user_id\n" +
                "                             FROM user_arrival arrivF\n" +
                "                             WHERE date_trunc('day', arrivF.created) = date_trunc('day', timestamp'" + date + "')\n" +
                "                  AND come_in = FALSE\n" +
                "                             GROUP BY arrivF.created,arrivF.user_id)arrivF on arrivF.user_id=empl.id\n" +
                "                  WHERE empl.dept_id =" + depID + "\n" +
                "                GROUP BY  FIO, empl.code,empl.created,arriv.created,arrivF.created;";

        try {
            List<Object> emplBDList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, pm);
            if (!emplBDList.isEmpty()) {
                for (Object o : emplBDList) {
                    Object[] oo = (Object[]) o;
                    VEmployeeInfo vempl = new VEmployeeInfo();
                    vempl.setFIO((String) oo[0]);
                    vempl.setCode((String) oo[1]);
                    vempl.setComeIN((Date) oo[2]);
                    vempl.setComeOUT((Date) oo[3]);
                    emplList.add(vempl);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load employee list", ex);
        }

        refreshEmployeeList(emplList);
        return emplList;
    }
}