package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDetailEmployeeLate;
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

import java.text.SimpleDateFormat;
import java.util.*;

public class AdministrationEmployeeAbsent implements EntityListener {

    private VerticalLayout mainVL;
    private HorizontalLayout topHL,dateHL;
    private HorizontalLayout buttonPanel;
    private GridWidget employeeByDepartmentGW;
    private DateField dateField,dateField2;
    private DBGridModel employeeByDepartmentGM;
    private Button backButton;
    private GroupAttendance groupAttendance;
    private Long depID;
    private VDepartment department;
    private EmployeeAbsent employeeLatecomers;
    private Button detalizationBtn,printBtn;

    public AdministrationEmployeeAbsent(EmployeeAbsent employeeLatecomers){

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
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        dateField = new DateField();
        dateField.setValue(employeeLatecomers.getDateField().getValue());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeByDepartmentGW)>-1){
                    employeeByDepartmentGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
                }
            }
        });

        dateField2 = new DateField();
        dateField2.setValue(new Date());
        dateField2.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeByDepartmentGW)>-1){
                    employeeByDepartmentGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
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

        dateHL = CommonUtils.createButtonPanel();

        dateHL.addComponent(new Label(CommonUtils.getUILocaleUtil().getCaption("at")));
        dateHL.addComponent(dateField);
        dateHL.addComponent(new Label(CommonUtils.getUILocaleUtil().getCaption("to")));
        dateHL.addComponent(dateField2);

        buttonPanel.addComponent(dateHL);
        buttonPanel.setComponentAlignment(dateHL, Alignment.MIDDLE_CENTER);

        buttonPanel.addComponent(printBtn);
        buttonPanel.setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);

        buttonPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        buttonPanel.addComponent(detalizationBtn);

        topHL.addComponent(buttonPanel);
        topHL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);

        employeeByDepartmentGW = new GridWidget(VDetailEmployeeLate.class);
        employeeByDepartmentGW.showToolbar(false);
        employeeByDepartmentGW.addEntityListener(this);
        employeeByDepartmentGW.setImmediate(true);

        employeeByDepartmentGM = (DBGridModel)employeeByDepartmentGW.getWidgetModel();
        employeeByDepartmentGM.setRowNumberVisible(true);
        employeeByDepartmentGM.setRowNumberWidth(30);
        employeeByDepartmentGM.setRefreshType(ERefreshType.MANUAL);
        employeeByDepartmentGM.setEntities(getEmployee( dateField.getValue(),dateField2.getValue()));


        mainVL.addComponent(topHL);
        mainVL.addComponent(employeeByDepartmentGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
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

    }

    public List<VDetailEmployeeLate> getEmployee(Date date, Date date2) {
        List<VDetailEmployeeLate> emplList = new ArrayList<>();

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        String formattedDate2 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date2);

        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT\n" +
                " empl.id,  trim(empl.LAST_NAME || ' ' || empl.FIRST_NAME || ' ' || coalesce(empl.MIDDLE_NAME, '')) FIO,\n" +
                "  empl.post_name,\n" +
                "  date_part('day' , age('"+formattedDate2+"', '"+formattedDate+"'))  - count(newtable.user_id) as kelmedi\n" +
                "FROM v_employee empl\n" +
                " INNER JOIN department dep ON empl.dept_id = dep.id" +
                "  LEFT JOIN (\n" +
                "              SELECT DISTINCT ua.user_id , date_trunc('day',ua.created )\n" +
                "              FROM user_arrival ua\n" +
                "              WHERE\n" +
                "                date_trunc('day', ua.created) between\n" +
                "                date_trunc('day', TIMESTAMP '"+formattedDate+"') and date_trunc('day',\n" +
                "                                                                         TIMESTAMP '"+formattedDate2+"') " +
                " and (EXTRACT(ISODOW FROM ua.created) NOT IN (6, 7))\n" +
                "              GROUP BY ua.user_id , date_trunc('day',ua.created )\n" +
                "            ) AS newtable ON newtable.user_id = empl.id\n" +
                " WHERE dep.parent_id = 43\n" +
                " GROUP BY FIO, empl.post_name, empl.id\n" +
                " ORDER BY FIO;\n";

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
}
