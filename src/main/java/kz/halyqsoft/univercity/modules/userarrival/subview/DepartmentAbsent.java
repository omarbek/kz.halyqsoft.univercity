package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.DetalizationDialog;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Boolean.FALSE;

public class DepartmentAbsent implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL,dateHL;
    private HorizontalLayout buttonPanel;
    private GridWidget employeeGW,employeeByDepartmentGW;
    private DateField dateField, dateField2;
    private DBGridModel employeeGM,employeeByDepartmentGM;
    private Button  backButton;
    private GroupAttendance groupAttendance;
    private Long depID;
    private VEmployeeLatecomers department;
    private EmployeeAbsent employeeLatecomers;
    private Button detalizationBtn,printBtn;
    private Label at, to;

    public DepartmentAbsent(VEmployeeLatecomers department, EmployeeAbsent employeeLatecomers){

        this.department = department;
        this.employeeLatecomers = employeeLatecomers;

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        buttonPanel = CommonUtils.createButtonPanel();

        topHL = new HorizontalLayout();
        topHL.setWidth(100, Sizeable.Unit.PERCENTAGE);
        topHL.setImmediate(true);

        init();
    }

    private void init(){

        backButton = new Button(CommonUtils.getUILocaleUtil().getCaption("backButton"));
        backButton.setImmediate(true);
        backButton.setVisible(false);
        backButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mainVL.removeComponent(employeeByDepartmentGW);
                mainVL.addComponent(employeeGW);
                backButton.setVisible(false);
                detalizationBtn.setVisible(false);
                dateField2.setVisible(false);
                at.setVisible(false);
                to.setVisible(false);
                employeeLatecomers.getBackButton().setVisible(true);
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

                if (mainVL.getComponentIndex(employeeByDepartmentGW) != -1) {
                    for (GridColumnModel gcm : ((DBGridModel) employeeByDepartmentGW.getWidgetModel()).getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < employeeByDepartmentGW.getAllEntities().size(); i++) {
                        VDetailEmployeeLate vEmployeeInfo = (VDetailEmployeeLate) employeeByDepartmentGW.getAllEntities().get(i);
                        if (employeeByDepartmentGW.getCaption() != null) {
                            fileName = employeeByDepartmentGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vEmployeeInfo.getFIO());
                        list.add(vEmployeeInfo.getPostName());
                        list.add(vEmployeeInfo.getAbsentSum().toString());
                        tableBody.add(list);
                    }
                } else if (mainVL.getComponentIndex(employeeGW) != -1) {
                    for (GridColumnModel gcm : ((DBGridModel) employeeGW.getWidgetModel()).getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < employeeGW.getAllEntities().size(); i++) {
                        VLateEmployee vDepartmentInfo = (VLateEmployee) employeeGW.getAllEntities().get(i);
                        if (employeeGW.getCaption() != null) {
                            fileName = employeeGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vDepartmentInfo.getDeptName());
                        list.add(vDepartmentInfo.getCount().toString());
                        list.add(vDepartmentInfo.getIsNotPresent().toString());
                        list.add(vDepartmentInfo.getPercantage() + " %");
                        tableBody.add(list);
                    }
                }

                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        dateField = new DateField();
        dateField.setValue(employeeLatecomers.getDateField().getValue());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeGW)>-1){
                    employeeGM.setEntities(getDepartment(dateField.getValue()));
                }if(mainVL.getComponentIndex(employeeByDepartmentGW)>-1){
                    employeeByDepartmentGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
                }
            }
        });

        dateField2 = new DateField();
        dateField2.setValue(new Date());
        dateField2.setVisible(false);
        dateField2.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeGW)>-1){
                    employeeGM.setEntities(getDepartment(dateField.getValue()));
                }if(mainVL.getComponentIndex(employeeByDepartmentGW)>-1){
                    employeeByDepartmentGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
                }
            }
        });

        detalizationBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("detalization"));
        detalizationBtn.setVisible(true);
        detalizationBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (employeeByDepartmentGW.getSelectedEntity() != null) {
                    try {
                        DetalizationDialog detalizationDialog = new DetalizationDialog(CommonUtils.getUILocaleUtil().getCaption("detalization"), SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class, (employeeByDepartmentGW.getSelectedEntity().getId())), dateField.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Message.showError("chooseARecord");
                }
            }
        });

        at = new Label(CommonUtils.getUILocaleUtil().getCaption("at"));
        at.setWidth("20");
        at.setVisible(false);
        to = new Label(CommonUtils.getUILocaleUtil().getCaption("to"));
        to.setWidth("30");
        to.setVisible(false);

        dateHL = CommonUtils.createButtonPanel();

        dateHL.addComponent(at);
        dateHL.addComponent(dateField);
        dateHL.addComponent(to);
        dateHL.addComponent(dateField2);

        buttonPanel.addComponent(dateHL);
        buttonPanel.setComponentAlignment(dateHL, Alignment.MIDDLE_CENTER);

        buttonPanel.addComponent(printBtn);
        buttonPanel.setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);

        buttonPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        buttonPanel.addComponent(detalizationBtn);

        topHL.addComponent(buttonPanel);
        topHL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);

        employeeGW = new GridWidget(VLateEmployee.class);
        employeeGW.setImmediate(true);
        employeeGW.showToolbar(false);

        employeeGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        employeeGW.addEntityListener(this);

        employeeGM = (DBGridModel) employeeGW.getWidgetModel();
        employeeGM.setRowNumberVisible(true);
        employeeGM.setRowNumberWidth(30);
        employeeGM.setMultiSelect(false);
        employeeGM.setEntities(getDepartment(dateField.getValue()));
        employeeGM.setRefreshType(ERefreshType.MANUAL);
        employeeGM.getFormModel().getFieldModel("time").setInView(FALSE);

        mainVL.addComponent(topHL);
        mainVL.addComponent(employeeGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public List<VLateEmployee> getDepartment(Date date) {

        List<VLateEmployee> list = new ArrayList<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT  dep.DEPT_NAME,\n" +
                "  count(empl.dept_id),\n" +
                "  count(empl.dept_id)- count(user_id),\n" +
                "  dep.id\n" +
                " FROM v_employee empl\n" +
                "  RIGHT JOIN department dep ON  dep.id = empl.dept_id\n" +
                " LEFT join (SELECT\n" +
                "                    arriv.user_id,\n" +
                "            count(arriv.user_id) as cameCount\n" +
                "            FROM user_arrival arriv\n" +
                "            WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'"+formattedDate+"')\n" +
                "            AND arriv.created = (SELECT max(max_arriv.created)\n" +
                "            FROM user_arrival max_arriv\n" +
                "            WHERE max_arriv.user_id = arriv.user_id)\n" +
                "            GROUP BY arriv.user_id)arriv on arriv.user_id=empl.id\n" +
                " WHERE dep.parent_id = \n" + department.getDepartmentID() +
                " GROUP BY  dep.dept_name,dep.id";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VLateEmployee ve = new VLateEmployee();
                    ve.setDeptName((String) oo[0]);
                    ve.setCount((long) oo[1]);
                    ve.setIsNotPresent((long) oo[2]);
                    if (ve.getCount() != 0 && ve.getIsNotPresent() != 0) {
                        ve.setPercantage((ve.getIsNotPresent() * 100) / ve.getCount());
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

    private void refreshList(List<VLateEmployee> list) {
        ((DBGridModel) employeeGW.getWidgetModel()).setEntities(list);
        try {
            employeeGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }
    private void refreshEmployeeList(List<VDetailEmployeeLate> employeelist) {
        ((DBGridModel) employeeByDepartmentGW.getWidgetModel()).setEntities(employeelist);
        try {
            employeeByDepartmentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh employee list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(employeeGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(employeeGW !=null){
                    mainVL.removeComponent(employeeGW);

                    employeeByDepartmentGW = new GridWidget(VDetailEmployeeLate.class);
                    employeeByDepartmentGW.showToolbar(false);
                    employeeByDepartmentGW.addEntityListener(this);
                    employeeByDepartmentGW.setImmediate(true);

                    employeeByDepartmentGM = (DBGridModel)employeeByDepartmentGW.getWidgetModel();
                    employeeByDepartmentGM.setRowNumberVisible(true);
                    employeeByDepartmentGM.setRowNumberWidth(30);
                    employeeByDepartmentGM.setRefreshType(ERefreshType.MANUAL);
                    employeeByDepartmentGM.setEntities(getEmployee( dateField.getValue(),dateField2.getValue()));

                    mainVL.addComponent(employeeByDepartmentGW);
                    backButton.setVisible(true);
                    detalizationBtn.setVisible(true);
                    employeeLatecomers.getBackButton().setVisible(false);
                    dateField2.setVisible(true);
                    at.setVisible(true);
                    to.setVisible(true);
                }
            }
        }
    }

    public List<VDetailEmployeeLate> getEmployee(Date date, Date date2) {
        List<VDetailEmployeeLate> emplList = new ArrayList<>();

        if (employeeGW.getSelectedEntity() != null) {
            depID = ((VLateEmployee) employeeGW.getSelectedEntity()).getDepartmentID();
        }
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        String formattedDate2 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date2);

        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT\n" +
                " empl.id," +
                "  trim(empl.LAST_NAME || ' ' || empl.FIRST_NAME || ' ' || coalesce(empl.MIDDLE_NAME, '')) FIO,\n" +
                "  empl.post_name,\n" +
                "  date_part('day' , age('"+formattedDate2+"', '"+ formattedDate +"')) - count(newtable.user_id) as kelmedi\n" +
                "FROM v_employee empl\n" +
                "  LEFT JOIN (\n" +
                "              SELECT DISTINCT ua.user_id , date_trunc('day',ua.created )\n" +
                "              FROM user_arrival ua\n" +
                "              WHERE\n" +
                "                date_trunc('day', ua.created) between\n" +
                "                date_trunc('day', TIMESTAMP '"+ formattedDate +"') and date_trunc('day',\n" +
                "                                                                         TIMESTAMP '"+formattedDate2+"') and (EXTRACT(ISODOW FROM ua.created) NOT IN (6, 7))\n" +
                "\n" +
                "              GROUP BY ua.user_id , date_trunc('day',ua.created )\n" +
                "            ) AS newtable ON newtable.user_id = empl.id\n" +
                "WHERE empl.dept_id = \n" + depID +
                "GROUP BY FIO, empl.post_name, empl.id\n" +
                "ORDER BY FIO;";

        try {
            List<Object> emplBDList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, pm);
            if (!emplBDList.isEmpty()) {
                for (Object o : emplBDList) {
                    Object[] oo = (Object[]) o;
                    VDetailEmployeeLate vempl = new VDetailEmployeeLate();
                    vempl.setId(ID.valueOf((long) oo[0]));
                    vempl.setFIO((String) oo[1]);
                    vempl.setPostName((String)oo[2]);
                    vempl.setAbsentSum((Double)oo[3]);
                    emplList.add(vempl);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load employee list", ex);
        }

        refreshEmployeeList(emplList);
        return emplList;
    }


    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
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
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

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

    public DateField getDateField() {
        return dateField;
    }

    public void setDateField(DateField dateField) {
        this.dateField = dateField;
    }

    public Button getBackButtonFaculty() {
        return backButton;
    }

    public void setBackButtonFaculty(Button backButtonFaculty) {
        this.backButton = backButtonFaculty;
    }

    public GridWidget getEmployeeByDepartmentGW() {
        return employeeByDepartmentGW;
    }

    public void setEmployeeByDepartmentGW(GridWidget employeeByDepartmentGM) {
        this.employeeByDepartmentGW = employeeByDepartmentGM;
    }
}
