package kz.halyqsoft.univercity.modules.userarrival;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartmentInfo;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployeeInfo;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.userarrival.subview.*;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.DetalizationDialog;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Boolean.FALSE;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class UserArrivalView extends AbstractTaskView implements EntityListener {

    private HorizontalSplitPanel mainHSP;
    private HorizontalLayout mainHL, secondHL;
    private HorizontalLayout buttonPanel;
    private GridWidget absentsGW, lateGW;
    private USER_TYPE userType;
    private HorizontalLayout buttonsHL;
    private VerticalLayout tableVL;
    private Button printBtn;

    private DateField date;
    private GridWidget employeeGW;
    private GridWidget employeeByDepartmentGW;
    private Long depID;

    private Long emplID;
    private ComboBox absentDayCB;
    private Button detalizationBtn;
    private GridWidget vEmployeeInfoGW;
    private DBGridModel vEmployeeInfoGM, vGroupGM;
    private Button backButton;

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

        buttonPanel = CommonUtils.createButtonPanel();

        final TreeTable menuTT = new TreeTable();

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        String main = getUILocaleUtil().getCaption("main");
        String studentAttendance = getUILocaleUtil().getCaption("studentAttendance");
        String employeeAttendance = getUILocaleUtil().getCaption("employeeAttendance");
        String latecomers = getUILocaleUtil().getCaption("latecomers");
        String absent = getUILocaleUtil().getCaption("absent");
        String yearlyAttendance = getUILocaleUtil().getCaption("yearlyAttendance");
        String manuallySign = getUILocaleUtil().getCaption("manuallySign");
        String manuallySignedReport = getUILocaleUtil().getCaption("manuallySign.report");

        hierarchicalContainer.addItem(main);
        hierarchicalContainer.setChildrenAllowed(main, false);
        hierarchicalContainer.addItem(studentAttendance);
        hierarchicalContainer.setChildrenAllowed(studentAttendance, false);
        hierarchicalContainer.addItem(employeeAttendance);
        hierarchicalContainer.setChildrenAllowed(employeeAttendance, false);
        hierarchicalContainer.addItem(latecomers);
        hierarchicalContainer.setChildrenAllowed(latecomers, false);
        hierarchicalContainer.addItem(absent);
        hierarchicalContainer.setChildrenAllowed(absent, false);
        hierarchicalContainer.addItem(yearlyAttendance);
        hierarchicalContainer.setChildrenAllowed(yearlyAttendance, false);
        hierarchicalContainer.addItem(manuallySign);
        hierarchicalContainer.addItem(manuallySignedReport);
        hierarchicalContainer.setChildrenAllowed(manuallySignedReport, false);
        hierarchicalContainer.setParent(manuallySignedReport, manuallySign);


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
                            MainSection mainSection = new MainSection();
                            mainHL.addComponent(mainSection.getMainVL());
                        } else if (employeeAttendance.equals(event.getProperty().getValue().toString())) {

                            date = new DateField();
                            Date currentDate = new Date();
                            date.setValue(currentDate);
                            date.addValueChangeListener(new Property.ValueChangeListener() {
                                @Override
                                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                                    if (date.getValue() != null) {
                                        DateFormat dateFormat = new SimpleDateFormat();
                                        ((DBGridModel) employeeGW.getWidgetModel()).setEntities(getList(dateFormat.format(date.getValue())));
                                        if (employeeByDepartmentGW != null) {
                                            ((DBGridModel) employeeByDepartmentGW.getWidgetModel()).
                                                    setEntities(getEmployee(dateFormat.format(date.getValue())));
                                            //vEmployeeInfoGM.setEntities(getList(getList(dateFormat.format(date.getValue())));
                                        }
                                    } else {
                                        Message.showInfo(getUILocaleUtil().getMessage("error.date"));
                                    }
                                }
                            });
                            setDepartmentInfo();

                        } else if (studentAttendance.equalsIgnoreCase(event.getProperty().getValue().toString())) {
                            GroupAttendance groupAttendance = new GroupAttendance();
                            mainHL.addComponent(groupAttendance.getMainVL());
                        } else if (absent.equalsIgnoreCase(event.getProperty().getValue().toString())) {
                            AbsentAttendance absentAttendance = new AbsentAttendance(tableVL);
                            mainHL.addComponent(absentAttendance.getAbsentsInfo());
                        } else if (manuallySign.equalsIgnoreCase(event.getProperty().getValue().toString())) {
                            SigningSection signingSection = new SigningSection();
                            mainHL.addComponent(signingSection.getMainVL());
                            setAbsentsInfo();

                        } else if (latecomers.equalsIgnoreCase(event.getProperty().getValue().toString())) {
                            GroupLatecomers groupLatecomers = new GroupLatecomers();
                            mainHL.addComponent(groupLatecomers.getMainVL());
                            setAbsentsInfo();

                        } else if (manuallySignedReport.equalsIgnoreCase(event.getProperty().getValue().toString())) {
                            SigningReport signingReport = new SigningReport();
                            mainHL.addComponent(signingReport.getMainVL());
                        }
                        mainHSP.addComponent(mainHL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


        printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String fileName = "document";

                if (tableVL.getComponentIndex(employeeByDepartmentGW) != -1) {
                    for (GridColumnModel gcm : ((DBGridModel) employeeByDepartmentGW.getWidgetModel()).getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < employeeByDepartmentGW.getAllEntities().size(); i++) {
                        VEmployeeInfo vEmployeeInfo = (VEmployeeInfo) employeeByDepartmentGW.getAllEntities().get(i);
                        if (employeeByDepartmentGW.getCaption() != null) {
                            fileName = employeeByDepartmentGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vEmployeeInfo.getFIO());
                        list.add(vEmployeeInfo.getCode());
                        list.add(vEmployeeInfo.getComeIN() != null ? CommonUtils.getFormattedDate(vEmployeeInfo.getComeIN()) : " ");
                        list.add(vEmployeeInfo.getComeOUT() != null ? CommonUtils.getFormattedDate(vEmployeeInfo.getComeOUT()) : " ");
                        tableBody.add(list);
                    }
                } else if (tableVL.getComponentIndex(employeeGW) != -1) {
                    for (GridColumnModel gcm : ((DBGridModel) employeeGW.getWidgetModel()).getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < employeeGW.getAllEntities().size(); i++) {
                        VDepartmentInfo vDepartmentInfo = (VDepartmentInfo) employeeGW.getAllEntities().get(i);
                        if (employeeGW.getCaption() != null) {
                            fileName = employeeGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vDepartmentInfo.getDeptName());
                        list.add(vDepartmentInfo.getCount().toString());
                        list.add(vDepartmentInfo.getIsPresent().toString());
                        list.add(vDepartmentInfo.getPercantage() + " %");
                        tableBody.add(list);
                    }
                }

                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });


        detalizationBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("detalization"));
        detalizationBtn.setVisible(true);
        detalizationBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (employeeByDepartmentGW.getSelectedEntity() != null) {
                    DetalizationDialog detalizationDialog = null;
                    try {
                        detalizationDialog = new DetalizationDialog(CommonUtils.getUILocaleUtil().getCaption("detalization"), SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class, (employeeByDepartmentGW.getSelectedEntity().getId())), date.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Message.showError("chooseARecord");
                }
            }
        });

        mainHSP.addComponent(menuTT);
        getContent().addComponent(mainHSP);
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

                buttonPanel.addComponent(date);
                buttonPanel.setComponentAlignment(date, Alignment.MIDDLE_CENTER);

                buttonPanel.addComponent(printBtn);
                buttonPanel.setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);

                buttonPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
                buttonPanel.addComponent(detalizationBtn);

                secondHL.addComponent(buttonPanel);
                secondHL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);

                tableVL.addComponent(secondHL);
                tableVL.addComponent(employeeByDepartmentGW);
                mainHL.addComponent(tableVL);

            }
        }
    }

    public List<VEmployeeInfo> getEmployee(String date) {
        List<VEmployeeInfo> emplList = new ArrayList<>();

        if (employeeGW.getSelectedEntity() != null) {
            depID = ((VDepartmentInfo) employeeGW.getSelectedEntity()).getDepartmentID();
        }
        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT\n" +
                "                empl.id, trim(empl.LAST_NAME || ' ' || empl.FIRST_NAME || ' ' || coalesce(empl.MIDDLE_NAME, '')) FIO,\n" +
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
                "                GROUP BY  FIO, empl.code,empl.created,arriv.created,arrivF.created, empl.id;";

        try {
            List<Object> emplBDList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, pm);
            if (!emplBDList.isEmpty()) {
                for (Object o : emplBDList) {
                    Object[] oo = (Object[]) o;
                    VEmployeeInfo vempl = new VEmployeeInfo();
                    vempl.setId(ID.valueOf((long) oo[0]));
                    vempl.setFIO((String) oo[1]);
                    vempl.setCode((String) oo[2]);
                    vempl.setComeIN((Date) oo[3]);
                    vempl.setComeOUT((Date) oo[4]);
                    emplList.add(vempl);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load employee list", ex);
        }

        refreshEmployeeList(emplList);
        return emplList;
    }

    private void setAbsentsInfo() {
        absentDayCB = new ComboBox(getUILocaleUtil().getCaption("absentDayCB"));
        absentDayCB.setNullSelectionAllowed(true);
        absentDayCB.setTextInputAllowed(true);
        absentDayCB.setFilteringMode(FilteringMode.CONTAINS);
        absentDayCB.setWidth(300, Unit.PIXELS);

    }

    public void setDepartmentInfo() {

        employeeGW = new GridWidget(VDepartmentInfo.class);
        employeeGW.setCaption(getUILocaleUtil().getCaption("employeeGW"));
        employeeGW.setImmediate(true);
        employeeGW.showToolbar(false);
        employeeGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        employeeGW.addEntityListener(this);

        DBGridModel employeeGM = (DBGridModel) employeeGW.getWidgetModel();
        employeeGM.setTitleVisible(false);
        employeeGM.setMultiSelect(false);
        employeeGM.setEntities(getList(date.getValue().toString()));
        employeeGM.setRefreshType(ERefreshType.MANUAL);
        employeeGM.getFormModel().getFieldModel("departmentID").setInView(FALSE);

        tableVL = new VerticalLayout();
        HorizontalLayout topHL = CommonUtils.createButtonPanel();
        topHL.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);


        topHL.addComponent(date);
        topHL.addComponent(printBtn);

        tableVL.addComponent(topHL);
        tableVL.setComponentAlignment(topHL, Alignment.MIDDLE_RIGHT);
        tableVL.addComponent(employeeGW);
        mainHL.addComponent(tableVL);
    }
}