package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartment;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.vaadin.jsconsole.ClientLog;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Boolean.FALSE;

public class EmployeeAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private HorizontalLayout buttonPanel;
    private GridWidget departmentGW;
    private DateField dateField;
    private DBGridModel departmentGM;
    private Button  backButton, backButtonAdministration,backButtonAE;
    private DepartmentAttendance attendance;
    private AdministrationAttendance administrationAttendance;
    private AdministrationEmployeeAttendance administrationEA;

    public EmployeeAttendance( ){
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
                    mainVL.removeComponent(attendance.getMainVL());
                    mainVL.addComponent(departmentGW);
                dateField.setVisible(true);
                backButton.setVisible(false);
            }
        });

        backButtonAdministration = new Button(CommonUtils.getUILocaleUtil().getCaption("backButton"));
        backButtonAdministration.setImmediate(true);
        backButtonAdministration.setVisible(false);
        backButtonAdministration.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mainVL.removeComponent(administrationAttendance.getMainVL());
                mainVL.addComponent(departmentGW);

                dateField.setVisible(true);
                backButtonAdministration.setVisible(false);
            }
        });

        backButtonAE = new Button(CommonUtils.getUILocaleUtil().getCaption("backButton"));
        backButtonAE.setImmediate(true);
        backButtonAE.setVisible(false);
        backButtonAE.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mainVL.removeComponent(administrationEA.getMainVL());
                mainVL.addComponent(departmentGW);

                dateField.setVisible(true);
                backButtonAE.setVisible(false);
            }
        });

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        topHL.addComponent(backButtonAdministration);
        topHL.setComponentAlignment(backButtonAdministration, Alignment.TOP_LEFT);

        topHL.addComponent(backButtonAE);
        topHL.setComponentAlignment(backButtonAE, Alignment.TOP_LEFT);

        dateField = new DateField();
        dateField.setValue(new Date());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(departmentGW)>-1){
                    departmentGM.setEntities(getDepartment(dateField.getValue()));
                }
            }
        });

        buttonPanel.addComponent(dateField);
        buttonPanel.setComponentAlignment(dateField, Alignment.MIDDLE_CENTER);

        topHL.addComponent(buttonPanel);
        topHL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);

        departmentGW = new GridWidget(VDepartment.class);
        departmentGW.setImmediate(true);
        departmentGW.showToolbar(false);

        departmentGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        departmentGW.addEntityListener(this);

        departmentGM = (DBGridModel) departmentGW.getWidgetModel();
        departmentGM.setRowNumberVisible(true);
        departmentGM.setRowNumberWidth(30);
        departmentGM.setMultiSelect(false);
        departmentGM.setEntities(getDepartment(dateField.getValue()));
        departmentGM.setRefreshType(ERefreshType.MANUAL);
        departmentGM.getFormModel().getFieldModel("time").setInView(FALSE);

        mainVL.addComponent(topHL);
        mainVL.addComponent(departmentGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public List<VDepartment> getDepartment(Date date) {

        List<VDepartment> list = new ArrayList<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT\n" +
                "  d1.dept_name,\n" +
                "  count(ve.id),\n" +
                "  count(user_id),\n" +
                "  d1.id\n" +
                "FROM department d1\n" +
                "  INNER JOIN department d2\n" +
                "    ON d1.id = d2.parent_id\n" +
                "  INNER JOIN v_employee ve\n" +
                "    ON ve.dept_id = d2.id\n" +
                "  LEFT JOIN (SELECT\n" +
                "               arriv.user_id,\n" +
                "               count(arriv.user_id) AS cameCount\n" +
                "             FROM user_arrival arriv\n" +
                "               INNER JOIN (SELECT\n" +
                "                             max(max_arriv.created) AS max,\n" +
                "                             user_id\n" +
                "                           FROM user_arrival max_arriv\n" +
                "                           GROUP BY user_id) AS foo\n" +
                "ON foo.user_id = arriv.user_id AND date_trunc('day', TIMESTAMP '"+formattedDate+"') = date_trunc('day', foo.max)\n" +
                "             WHERE date_trunc('day', arriv.created) = date_trunc('day', foo.max)\n" +
       //         "                   AND come_in = TRUE\n" +
                "             GROUP BY arriv.user_id) arriv ON arriv.user_id = ve.id\n" +
                "WHERE d1.deleted = FALSE AND d2.deleted = FALSE\n" +
                "GROUP BY d1.dept_name, d1.id;\n";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VDepartment ve = new VDepartment();
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

    private void refreshList(List<VDepartment> list) {
        ((DBGridModel) departmentGW.getWidgetModel()).setEntities(list);
        try {
            departmentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(departmentGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(departmentGW !=null){
                    mainVL.removeComponent(departmentGW);
                    if(((VDepartment)departmentGW.getSelectedEntity()).getDepartmentID()==20){
                        administrationAttendance = new AdministrationAttendance(this);
                        mainVL.addComponent(administrationAttendance.getMainVL());
                        backButtonAdministration.setVisible(true);
                    }else if(((VDepartment)departmentGW.getSelectedEntity()).getDepartmentID()==43){
                        administrationEA = new AdministrationEmployeeAttendance(this);
                        mainVL.addComponent(administrationEA.getMainVL());
                        backButtonAE.setVisible(true);
                    }else {
                        attendance = new DepartmentAttendance((VDepartment) departmentGW.getSelectedEntity(),this);
                        mainVL.addComponent(attendance.getMainVL());
                        backButton.setVisible(true);
                    }
                    dateField.setVisible(false);

                }
            }
        }
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

    public Button getBackButton() {
        return backButton;
    }

    public void setBackButtonFaculty(Button backButton) {
        this.backButton = backButton;
    }

    public GridWidget getDepartmentGW() {
        return departmentGW;
    }

    public void setDepartmentGW(GridWidget departmentGW) {
        this.departmentGW = departmentGW;
    }
}
