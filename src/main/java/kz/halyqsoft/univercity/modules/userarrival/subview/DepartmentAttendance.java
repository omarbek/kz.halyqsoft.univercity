package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartmentInfo;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployeeInfo;
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

public class DepartmentAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private HorizontalLayout buttonPanel;
    private GridWidget employeeGW,employeeByDepartmentGW;
    private DateField dateField;
    private DBGridModel employeeGM,employeeByDepartmentGM;
    private Button  backButton;
    private GroupAttendance groupAttendance;
    private Long depID;
    private VDepartment department;
    private EmployeeAttendance employeeAttendance;
    private Button detalizationBtn,printBtn;

    public DepartmentAttendance(VDepartment department,EmployeeAttendance employeeAttendance){

        this.department = department;
        this.employeeAttendance = employeeAttendance;

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
                employeeAttendance.getBackButton().setVisible(true);
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
                        VEmployeeInfo vEmployeeInfo = (VEmployeeInfo) employeeByDepartmentGW.getAllEntities().get(i);
                        if (employeeByDepartmentGW.getCaption() != null) {
                            fileName = employeeByDepartmentGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vEmployeeInfo.getFIO());
                        list.add(vEmployeeInfo.getCode());
                        list.add(vEmployeeInfo.getComeIN() != null ? vEmployeeInfo.getComeIN() : " ");
                        list.add(vEmployeeInfo.getComeOUT() != null ? vEmployeeInfo.getComeOUT() : " ");
                        tableBody.add(list);
                    }
                } else if (mainVL.getComponentIndex(employeeGW) != -1) {
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

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        dateField = new DateField();
        dateField.setValue(employeeAttendance.getDateField().getValue());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeGW)>-1){
                    employeeGM.setEntities(getDepartment(dateField.getValue()));
                }if(mainVL.getComponentIndex(employeeByDepartmentGW)>-1){
                    employeeByDepartmentGM.setEntities(getEmployee(dateField.getValue()));
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

        buttonPanel.addComponent(dateField);
        buttonPanel.setComponentAlignment(dateField, Alignment.MIDDLE_CENTER);

        buttonPanel.addComponent(printBtn);
        buttonPanel.setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);

        buttonPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        buttonPanel.addComponent(detalizationBtn);

        topHL.addComponent(buttonPanel);
        topHL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);

        employeeGW = new GridWidget(VDepartmentInfo.class);
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

    public List<VDepartmentInfo> getDepartment(Date date) {

        List<VDepartmentInfo> list = new ArrayList<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT  dep.DEPT_NAME," +
        " count(empl.dept_id)," +
                " count(user_id)," +
                " dep.id" +
                " FROM v_employee empl" +
                " RIGHT JOIN department dep ON  dep.id = empl.dept_id " +
                " LEFT join (SELECT\n" +
                "               arriv.user_id,\n" +
                "               count(arriv.user_id) as cameCount\n" +
                "             FROM user_arrival arriv\n" +
                "             WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'" + formattedDate + "')\n" +
                "                   AND arriv.created = (SELECT max(max_arriv.created)\n" +
                "                                        FROM user_arrival max_arriv\n" +
                "                                        WHERE max_arriv.user_id = arriv.user_id)\n" +
                "                   AND come_in = TRUE\n" +
                "             GROUP BY arriv.user_id)arriv on arriv.user_id=empl.id" +
                " WHERE dep.parent_id = " + department.getDepartmentID() +
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
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
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
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(employeeGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(employeeGW !=null){
                    mainVL.removeComponent(employeeGW);

                    employeeByDepartmentGW = new GridWidget(VEmployeeInfo.class);
                    employeeByDepartmentGW.showToolbar(false);
                    employeeByDepartmentGW.addEntityListener(this);
                    employeeByDepartmentGW.setImmediate(true);

                    employeeByDepartmentGM = (DBGridModel)employeeByDepartmentGW.getWidgetModel();
                    employeeByDepartmentGM.setRowNumberVisible(true);
                    employeeByDepartmentGM.setRowNumberWidth(30);
                    employeeByDepartmentGM.setRefreshType(ERefreshType.MANUAL);
                    employeeByDepartmentGM.setEntities(getEmployee( dateField.getValue()));

                     mainVL.addComponent(employeeByDepartmentGW);
                    backButton.setVisible(true);
                    detalizationBtn.setVisible(true);
                    employeeAttendance.getBackButton().setVisible(false);
                }
            }
        }
    }

    public List<VEmployeeInfo> getEmployee(Date date) {
        List<VEmployeeInfo> emplList = new ArrayList<>();

        if (employeeGW.getSelectedEntity() != null) {
            depID = ((VDepartmentInfo) employeeGW.getSelectedEntity()).getDepartmentID();
        }
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT\n" +
                "                empl.id, trim(empl.LAST_NAME || ' ' || empl.FIRST_NAME || ' ' || coalesce(empl.MIDDLE_NAME, '')) FIO,\n" +
                "                empl.code,\n" +
                "                (arriv.created::time)::text,\n" +
                "                (arrivF.created::time)::text as false\n" +
                "                FROM v_employee empl\n" +
                "                  left join (SELECT\n" +
                "                     arriv.created,\n" +
                "                               arriv.user_id\n" +
                "                       FROM user_arrival arriv\n" +
                "                       WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'" + formattedDate + "')\n" +
                "                       AND come_in = TRUE\n" +
                "                       GROUP BY arriv.created,arriv.user_id)arriv on arriv.user_id=empl.id\n" +
                "                  left join (SELECT\n" +
                "                               arrivF.created,\n" +
                "                               arrivF.user_id\n" +
                "                             FROM user_arrival arrivF\n" +
                "                             WHERE date_trunc('day', arrivF.created) = date_trunc('day', timestamp'" + formattedDate + "')\n" +
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
                    vempl.setComeIN((String) oo[3]);
                    vempl.setComeOUT((String) oo[4]);
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
}
