package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VYearEmployeeLatecomers;
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

public class EmployeeYearLatecomers implements EntityListener {

    private VerticalLayout mainVL;
    private HorizontalLayout topHL, dateHL;
    private HorizontalLayout buttonPanel;
    private GridWidget employeeLatecomersGW;
    private DateField dateField, dateField2;
    private DBGridModel employeeLatecomersGM;
    private Button backButton;
    private GroupAttendance groupAttendance;
    private Long depID;
    private VDepartment department;
    private EmployeeAbsent employeeLatecomers;
    private Button detalizationBtn,printBtn;

    public EmployeeYearLatecomers(){

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

                if (mainVL.getComponentIndex(employeeLatecomersGW) != -1) {
                    for (GridColumnModel gcm : ((DBGridModel) employeeLatecomersGW.getWidgetModel()).getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < employeeLatecomersGW.getAllEntities().size(); i++) {
                        VYearEmployeeLatecomers vEmployeeInfo = (VYearEmployeeLatecomers) employeeLatecomersGW.getAllEntities().get(i);
                        if (employeeLatecomersGW.getCaption() != null) {
                            fileName = employeeLatecomersGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vEmployeeInfo.getFIO());
                        list.add(vEmployeeInfo.getFaculty());
                        list.add(vEmployeeInfo.getPostName());
                        tableBody.add(list);
                    }
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        dateField = new DateField();
        dateField.setValue(new Date());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeLatecomersGW)>-1){
                    employeeLatecomersGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
                }
            }
        });

        dateField2 = new DateField();
        dateField2.setValue(new Date());
        dateField2.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(employeeLatecomersGW)>-1){
                    employeeLatecomersGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
                }if(mainVL.getComponentIndex(employeeLatecomersGW)>-1){
                    employeeLatecomersGM.setEntities(getEmployee(dateField.getValue(),dateField2.getValue()));
                }
            }
        });

        detalizationBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("detalization"));
        detalizationBtn.setVisible(true);
        detalizationBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (employeeLatecomersGW.getSelectedEntity() != null) {
                    try {
                        DetalizationDialog detalizationDialog = new DetalizationDialog(CommonUtils.getUILocaleUtil().getCaption("detalization"), SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class, (employeeLatecomersGW.getSelectedEntity().getId())), dateField.getValue());
                        detalizationDialog.getDateField().setVisible(false);
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

        employeeLatecomersGW = new GridWidget(VYearEmployeeLatecomers.class);
        employeeLatecomersGW.showToolbar(false);
        employeeLatecomersGW.addEntityListener(this);
        employeeLatecomersGW.setImmediate(true);

        employeeLatecomersGM = (DBGridModel) employeeLatecomersGW.getWidgetModel();
        employeeLatecomersGM.setRowNumberVisible(true);
        employeeLatecomersGM.setRowNumberWidth(30);
        employeeLatecomersGM.setRefreshType(ERefreshType.MANUAL);
        employeeLatecomersGM.setEntities(getEmployee( dateField.getValue(),dateField2.getValue()));


        mainVL.addComponent(topHL);
        mainVL.addComponent(employeeLatecomersGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    private void refreshEmployeeList(List<VYearEmployeeLatecomers> employeelist) {
        ((DBGridModel) employeeLatecomersGW.getWidgetModel()).setEntities(employeelist);
        try {
            employeeLatecomersGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh employee list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    public List<VYearEmployeeLatecomers> getEmployee(Date date, Date date2) {
        List<VYearEmployeeLatecomers> emplList = new ArrayList<>();

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        String formattedDate2 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date2);

        Map<Integer, Object> pm = new HashMap<>();
        String sql = "select distinct  * from (SELECT empl.id, trim(empl.first_name||' '|| empl.last_name ||' '|| empl.middle_name)FIO,\n" +
                "                  first_value(empl.dept_name) over (partition by empl.id rows between unbounded  preceding  and unbounded following ),\n" +
                "  empl.post_name,\n" +
                "                   count(empl.created::time),\n" +
                "                    empl.created,\n" +
                "                  arriv.user_id,\n" +
                "                  first_value(empl.dept_id) over (partition by empl.id rows between unbounded  preceding  and unbounded following ) as dept_id\n" +
                "                  FROM user_arrival arriv\n" +
                "                    INNER JOIN v_employee empl ON empl.id = arriv.user_id\n" +
                "                    INNER JOIN department dep ON dep.id = empl.dept_id\n" +
                "                  WHERE date_trunc('day', arriv.created) between\n" +
                "                        date_trunc('day', TIMESTAMP '"+formattedDate+"') and date_trunc('day',\n" +
                "                                                                                              TIMESTAMP '"+formattedDate2+"')\n" +
                "                       AND arriv.created = (SELECT min(max_arriv.created)\n" +
                "                                             FROM user_arrival max_arriv\n" +
                "                                             WHERE max_arriv.user_id = arriv.user_id\n" +
                "                                                   AND date_trunc('day', max_arriv.created) between\n" +
                "                                                   date_trunc('day', TIMESTAMP '"+formattedDate+"') and date_trunc('day',\n" +
                "                                                                                                            TIMESTAMP '"+formattedDate2+"')\n" +
                "                                                  )\n" +
                "                        AND come_in = TRUE AND arriv.created :: TIME > '8:40:00'\n" +
                "                 GROUP BY FIO,arriv.user_id,empl.dept_id,empl.dept_name,empl.id,empl.created,empl.post_name) as foo";

        try {
            List<Object> emplBDList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, pm);
            if (!emplBDList.isEmpty()) {
                for (Object o : emplBDList) {
                    Object[] oo = (Object[]) o;
                    VYearEmployeeLatecomers vempl = new VYearEmployeeLatecomers();
                    vempl.setId(ID.valueOf((long) oo[0]));
                    vempl.setFIO((String) oo[1]);
                    vempl.setFaculty((String)oo[2]);
                    vempl.setPostName((String)oo[3]);
                    vempl.setLateSum((long)oo[4]);
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
