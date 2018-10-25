package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartment;
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

public class AdministrationAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private HorizontalLayout buttonPanel;
    private GridWidget employeeGW;
    private DateField dateField;
    private DBGridModel employeeGM;
    private Button  backButton;
    private GroupAttendance groupAttendance;
    private Long depID;
    private VDepartment department;
    private EmployeeAttendance employeeAttendance;
    private Button detalizationBtn,printBtn;

    public AdministrationAttendance(EmployeeAttendance employeeAttendance){

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

        printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String fileName = "document";

                if (mainVL.getComponentIndex(employeeGW) != -1) {
                    for (GridColumnModel gcm : ((DBGridModel) employeeGW.getWidgetModel()).getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < employeeGW.getAllEntities().size(); i++) {
                        VEmployeeInfo vEmployeeInfo = (VEmployeeInfo) employeeGW.getAllEntities().get(i);
                        if (employeeGW.getCaption() != null) {
                            fileName = employeeGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vEmployeeInfo.getFIO());
                        list.add(vEmployeeInfo.getCode());
                        list.add(vEmployeeInfo.getComeIN() != null ? vEmployeeInfo.getComeIN() : " ");
                        list.add(vEmployeeInfo.getComeOUT() != null ? vEmployeeInfo.getComeOUT() : " ");
                        tableBody.add(list);
                    }
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        dateField = new DateField();
        dateField.setValue(employeeAttendance.getDateField().getValue());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeGW)>-1){
                    employeeGM.setEntities(getEmployee(dateField.getValue()));
                }
            }
        });

        detalizationBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("detalization"));
        detalizationBtn.setVisible(true);
        detalizationBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (employeeGW.getSelectedEntity() != null) {
                    try {
                        DetalizationDialog detalizationDialog = new DetalizationDialog(CommonUtils.getUILocaleUtil().getCaption("detalization"), SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class, (employeeGW.getSelectedEntity().getId())), dateField.getValue());
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

        employeeGW = new GridWidget(VEmployeeInfo.class);
        employeeGW.setImmediate(true);
        employeeGW.showToolbar(false);

        employeeGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        employeeGW.addEntityListener(this);

        employeeGM = (DBGridModel) employeeGW.getWidgetModel();
        employeeGM.setRowNumberVisible(true);
        employeeGM.setRowNumberWidth(30);
        employeeGM.setMultiSelect(false);
        employeeGM.setEntities(getEmployee(dateField.getValue()));
        employeeGM.setRefreshType(ERefreshType.MANUAL);
        employeeGM.getFormModel().getFieldModel("time").setInView(FALSE);

        mainVL.addComponent(topHL);
        mainVL.addComponent(employeeGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    private void refreshEmployeeList(List<VEmployeeInfo> employeelist) {
        ((DBGridModel) employeeGW.getWidgetModel()).setEntities(employeelist);
        try {
            employeeGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh employee list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    public List<VEmployeeInfo> getEmployee(Date date) {
        List<VEmployeeInfo> emplList = new ArrayList<>();

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT\n" +
                "  empl.id, trim(empl.LAST_NAME || ' ' || empl.FIRST_NAME || ' ' || coalesce(empl.MIDDLE_NAME, '')) FIO,\n" +
                "  empl.code,\n" +
                "  (arriv.created::time)::text,\n" +
                "           (arrivF.created::time)::text as false\n" +
                "FROM v_employee empl\n" +
                "  left join (SELECT\n" +
                "               arriv.created,\n" +
                "               arriv.user_id\n" +
                "             FROM user_arrival arriv\n" +
                "             WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'"+ formattedDate +"')\n" +
                "                   AND come_in = TRUE\n" +
                "             GROUP BY arriv.created,arriv.user_id)arriv on arriv.user_id=empl.id\n" +
                "  left join (SELECT\n" +
                "               arrivF.created,\n" +
                "               arrivF.user_id\n" +
                "             FROM user_arrival arrivF\n" +
                "             WHERE date_trunc('day', arrivF.created) = date_trunc('day', timestamp'"+ formattedDate +"')\n" +
                "                   AND come_in = FALSE\n" +
                "             GROUP BY arrivF.created,arrivF.user_id)arrivF on arrivF.user_id=empl.id\n" +
                "  INNER JOIN department d ON d.id = empl.dept_id\n" +
                "WHERE d.parent_id = 20\n" +
                "GROUP BY  FIO, empl.code,empl.created,arriv.created,arrivF.created, empl.id;";

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
}
