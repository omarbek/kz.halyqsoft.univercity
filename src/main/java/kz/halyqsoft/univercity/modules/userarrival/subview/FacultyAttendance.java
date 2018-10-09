package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VFaculty;
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

import static java.lang.Boolean.FALSE;

public class FacultyAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private HorizontalLayout buttonPanel;
    private GridWidget vFacultyGW;
    private DateField dateField;
    private DBGridModel vFacultyGM;
    private Button  backButtonFaculty;
    private GroupAttendance groupAttendance;

    public FacultyAttendance(){


        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        buttonPanel = CommonUtils.createButtonPanel();

        topHL = new HorizontalLayout();
        topHL.setWidth(100, Sizeable.Unit.PERCENTAGE);
        topHL.setImmediate(true);

        init();
    }

    private void init(){

        backButtonFaculty = new Button(CommonUtils.getUILocaleUtil().getCaption("backButton"));
        backButtonFaculty.setImmediate(true);
        backButtonFaculty.setVisible(false);
        backButtonFaculty.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mainVL.removeComponent(groupAttendance.getMainVL());
                mainVL.addComponent(vFacultyGW);
                dateField.setVisible(true);
                backButtonFaculty.setVisible(false);
            }
        });

        topHL.addComponent(backButtonFaculty);
        topHL.setComponentAlignment(backButtonFaculty, Alignment.TOP_LEFT);

        dateField = new DateField();
        dateField.setValue(new Date());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(vFacultyGW)>-1){
                    vFacultyGM.setEntities(getList(dateField.getValue()));
                }
            }
        });

        buttonPanel.addComponent(dateField);
        buttonPanel.setComponentAlignment(dateField, Alignment.MIDDLE_CENTER);

        topHL.addComponent(buttonPanel);
        topHL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);

        vFacultyGW = new GridWidget(VFaculty.class);
        vFacultyGW.setImmediate(true);
        vFacultyGW.showToolbar(false);

        vFacultyGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        vFacultyGW.addEntityListener(this);

        vFacultyGM = (DBGridModel) vFacultyGW.getWidgetModel();
        vFacultyGM.setRowNumberVisible(true);
        vFacultyGM.setRowNumberWidth(30);
        vFacultyGM.setMultiSelect(false);
        vFacultyGM.setEntities(getList(dateField.getValue()));
        vFacultyGM.setRefreshType(ERefreshType.MANUAL);
        vFacultyGM.getFormModel().getFieldModel("time").setInView(FALSE);

        mainVL.addComponent(topHL);
        mainVL.addComponent(vFacultyGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public List<VFaculty> getList(Date date){
        List<VFaculty> facultyList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "SELECT   dep.DEPT_NAME,\n" +
                "         count(student.id),\n" +
                "         count(arriv.user_id),\n" +
                "  (COUNT( student.id)-count( arriv.user_id)),\n" +
                " case when (COUNT ( student.id))<>0 then CAST((count( arriv.user_id)*100)as FLOAT)  /CAST((COUNT ( student.id))as FLOAT) else 0 end,\n" +
                "        dep.id\n" +
                "         FROM v_student student\n" +
                "         RIGHT JOIN department dep ON  dep.id = student.faculty_id\n" +
                "         LEFT join (SELECT\n" +
                "                       arriv.user_id,\n" +
                "                       count(arriv.user_id) as cameCount\n" +
                "                     FROM user_arrival arriv\n" +
                "                     WHERE date_trunc('day', arriv.created) = date_trunc('day', timestamp'"+formattedDate+"')\n" +
                "                           AND arriv.created = (SELECT max(max_arriv.created)\n" +
                "                                                FROM user_arrival max_arriv\n" +
                "                                                WHERE max_arriv.user_id = arriv.user_id)\n" +
                "                           AND come_in = TRUE\n" +
                "                     GROUP BY arriv.user_id)arriv on arriv.user_id=student.id\n" +
                " WHERE dep.parent_id is null and dep.deleted is false\n" +
                "         GROUP BY  dep.dept_name,dep.id";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VFaculty vf = new VFaculty();
                    vf.setFacultyName((String) oo[0]);
                    vf.setCount((long) oo[1]);
                    vf.setIsPresent((long) oo[2]);
                    vf.setAbsent((long) oo[3]);
                    vf.setPercantage((double) oo[4]);
                    vf.setFacultyID((long) oo [5]);
                    facultyList.add(vf);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }

        refreshList(facultyList);
        return facultyList;
    }

    private void refreshList(List<VFaculty> list) {
        ((DBGridModel) vFacultyGW.getWidgetModel()).setEntities(list);
        try {
            vFacultyGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(vFacultyGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(vFacultyGW !=null){
                    mainVL.removeComponent(vFacultyGW);
                    groupAttendance = new GroupAttendance((VFaculty) vFacultyGW.getSelectedEntity(),this);
                    mainVL.addComponent(groupAttendance.getMainVL());
                    dateField.setVisible(false);
                    backButtonFaculty.setVisible(true);
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

    public Button getBackButtonFaculty() {
        return backButtonFaculty;
    }

    public void setBackButtonFaculty(Button backButtonFaculty) {
        this.backButtonFaculty = backButtonFaculty;
    }
}
