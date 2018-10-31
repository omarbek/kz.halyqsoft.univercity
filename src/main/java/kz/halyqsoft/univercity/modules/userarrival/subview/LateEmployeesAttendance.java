package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmploeeDoc;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VLateEmployees;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 16.10.2018
 */
public class LateEmployeesAttendance implements EntityListener {

    private VerticalLayout mainVL;
    private DateField dateField;
    private GridWidget usersGW;
    private DBGridModel usersGM;
    private ComboBox departmentCB;

    public LateEmployeesAttendance() {

        departmentCB = new ComboBox();
        departmentCB.setCaption(CommonUtils.getUILocaleUtil().getCaption("departmentCB"));
        departmentCB.setNullSelectionAllowed(true);
        departmentCB.setTextInputAllowed(true);
        departmentCB.setFilteringMode(FilteringMode.CONTAINS);
        departmentCB.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<DEPARTMENT> departmentQM = new QueryModel<>(DEPARTMENT.class);
        departmentQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> departmentBIC = null;
        try {
            departmentBIC = new BeanItemContainer<>(DEPARTMENT.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(departmentQM));
        } catch (Exception e) {
            e.printStackTrace();
        }
        departmentCB.setContainerDataSource(departmentBIC);
        departmentCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                ((DBGridModel) usersGW.getWidgetModel()).setEntities(getLateList((DEPARTMENT) departmentCB.getValue(), dateField.getValue()));
                refreshAbsentList(getLateList((DEPARTMENT) departmentCB.getValue(), dateField.getValue()));
            }
        });


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,8);
        cal.set(Calendar.MINUTE,40);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        dateField = new DateField();
        dateField.setCaption(CommonUtils.getUILocaleUtil().getCaption("dateField"));
        dateField.setValue(cal.getTime());
        dateField.setShowISOWeekNumbers(true);
        dateField.setStyleName("time-only");
        dateField.setResolution(Resolution.SECOND);
        dateField.setLocale(Locale.GERMANY);
        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(usersGW)>-1){
                    ((DBGridModel) usersGW.getWidgetModel()).setEntities(getLateList((DEPARTMENT) departmentCB.getValue(), dateField.getValue()));
                    refreshAbsentList(getLateList((DEPARTMENT) departmentCB.getValue(), dateField.getValue()));
                }
            }
        });

        usersGW = new GridWidget(VLateEmployees.class);
        usersGW.setCaption(CommonUtils.getUILocaleUtil().getCaption("userGW"));
        usersGW.showToolbar(false);
        usersGW.setImmediate(true);
        usersGW.showToolbar(false);
        usersGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);

        usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setEntities(getLateList((DEPARTMENT) departmentCB.getValue(), dateField.getValue()));
        usersGM.setTitleVisible(false);
        usersGM.setMultiSelect(false);
        usersGM.setRefreshType(ERefreshType.MANUAL);



        Button printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String fileName = "document";

                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("user"));
                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("time"));
                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("department"));
                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("post"));

                for (int i = 0; i < usersGW.getAllEntities().size(); i++) {
                    VLateEmployees userArrival = (VLateEmployees) usersGW.getAllEntities().get(i);
                    String slqEmpl = "SELECT\n" +
                            "  dep.DEPT_NAME,\n" +
                            "  post.post_name\n" +
                            "FROM EMPLOYEE empl INNER JOIN USERS usr ON empl.ID = usr.ID\n" +
                            "  LEFT JOIN EMPLOYEE_DEPT empl_dept ON empl_dept.EMPLOYEE_ID = empl.ID AND empl_dept.DISMISS_DATE IS NULL\n" +
                            "  LEFT JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID\n" +
                            "  LEFT JOIN POST post ON empl_dept.POST_ID = post.id\n" +
                            "  LEFT JOIN child c2 on empl.id = c2.employee_id\n" +
                            "where    usr.id =" + userArrival.getUserId() + " and usr.deleted = FALSE";

                    if (usersGW.getCaption() != null) {
                        fileName = usersGW.getCaption();
                    }
                    List<String> list = new ArrayList<>();
                    list.add(userArrival.getFIO());
                    list.add(userArrival.getDate().toString());
                    //list.add(userArrival.getFaculty());
                    try {
                        Map<Integer, Object> para = null;
                        List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(slqEmpl,
                                para);
                        if (!tmpList.isEmpty()) {
                            for (Object o : tmpList) {
                                Object[] oo = (Object[]) o;
                                VEmploeeDoc vd = new VEmploeeDoc();
                                list.add((String) oo[0]);
                                list.add((String) oo[1]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tableBody.add(list);
                }


                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });
        HorizontalLayout buttonPanel = CommonUtils.createButtonPanel();

        buttonPanel.addComponent(dateField);
        buttonPanel.setComponentAlignment(dateField, Alignment.MIDDLE_CENTER);

        buttonPanel.addComponent(departmentCB);
        buttonPanel.addComponent(printBtn);
        buttonPanel.setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);

        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();


        mainVL.addComponent(buttonPanel);
        mainVL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);
        mainVL.addComponent(usersGW);
    }

    public List<VLateEmployees> getLateList(DEPARTMENT department, Date date) {

        department = (DEPARTMENT) departmentCB.getValue();

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        List<VLateEmployees> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "select distinct  * from (SELECT trim(empl.first_name||' '|| empl.last_name ||' '|| empl.middle_name),\n" +
                "  first_value(empl.dept_name) over (partition by empl.id rows between unbounded  preceding  and unbounded following ),\n" +
                "   (arriv.created::time)::text\n," +
                "  arriv.user_id," +
                "  first_value(empl.dept_id) over (partition by empl.id rows between unbounded  preceding  and unbounded following ) as dept_id\n" +
                "  FROM user_arrival arriv\n" +
                "    INNER JOIN v_employee empl ON empl.id = arriv.user_id\n" +
                "    INNER JOIN department dep ON dep.id = empl.dept_id\n" +
                "  WHERE date_trunc('day', arriv.created) = date_trunc('day', TIMESTAMP '"+ CommonUtils.getFormattedDate(dateField.getValue())+"')\n" +
                "        AND arriv.created = (SELECT min(max_arriv.created)\n" +
                "                             FROM user_arrival max_arriv\n" +
                "                             WHERE max_arriv.user_id = arriv.user_id\n" +
                "                                   AND date_trunc('day', max_arriv.created) = date_trunc('day', TIMESTAMP '"+ CommonUtils.getFormattedDate(dateField.getValue()) +"')\n" +
                "                                   AND come_in = TRUE)\n" +
                "        AND come_in = TRUE AND arriv.created :: TIME > '"+CommonUtils.getTimeFromDate(dateField.getValue())+"'\n" +
                " ) as foo";

        if(department!=null){
            sql = sql + "   where dept_id = " + department.getId();
        }

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VLateEmployees vAbsent = new VLateEmployees();
                    vAbsent.setFIO((String) oo[0]);
                    vAbsent.setFaculty((String) oo[1]);
                    vAbsent.setDate((String)oo[2]);
                    vAbsent.setUserId((Long) oo[3]);
                    list.add(vAbsent);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
        }
        refreshAbsentList(list);
        return list;
    }

    private void refreshAbsentList(List<VLateEmployees> list) {
        //usersGM.setEntities(getLateList((DEPARTMENT)departmentCB.getValue(),dateField.getValue()));
        try {
            usersGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh department list", ex);
        }
    }



    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

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

    public VerticalLayout getMainVL() {
        return mainVL;
    }
}
