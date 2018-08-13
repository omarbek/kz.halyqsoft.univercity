package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentInfo;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.DetalizationDialog;
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
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.text.SimpleDateFormat;
import java.util.*;

import kz.halyqsoft.univercity.utils.CommonUtils;

public class GroupAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private GridWidget vGroupGW;
    private DateField dateField;
    private DBGridModel vGroupGM;
    private Button backButton;
    private GridWidget vStudentInfoGW;
    private DBGridModel vStudentInfoGM;
    private Button detalizationBtn;

    public GroupAttendance(){
        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

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
                mainVL.removeComponent(vStudentInfoGW);
                mainVL.addComponent(vGroupGW);
                backButton.setVisible(false);
                detalizationBtn.setVisible(false);
            }
        });

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        dateField = new DateField();
        dateField.setValue(new Date());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(mainVL.getComponentIndex(vGroupGW)>-1){
                    vGroupGM.setEntities(getList(dateField.getValue()));
                }else if(mainVL.getComponentIndex(vStudentInfoGW)>-1){
                    if(vGroupGW.getSelectedEntity()!=null){
                        vStudentInfoGM.setEntities(getList((VGroup) vGroupGW.getSelectedEntity() , dateField.getValue()));
                    }
                }
            }
        });

        detalizationBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("detalization"));
        detalizationBtn.setVisible(false);
        detalizationBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(vStudentInfoGW.getSelectedEntity()!=null){
                    DetalizationDialog detalizationDialog = new DetalizationDialog(CommonUtils.getUILocaleUtil().getCaption("detalization") ,((VStudentInfo)vStudentInfoGW.getSelectedEntity()).getStudent() , dateField.getValue());
                }else{
                    Message.showError("chooseARecord");
                }
            }
        });
        topHL.addComponent(dateField);
        topHL.setComponentAlignment(dateField, Alignment.TOP_RIGHT);

        topHL.addComponent(detalizationBtn);
        topHL.setComponentAlignment(detalizationBtn, Alignment.TOP_RIGHT);

        vGroupGW = new GridWidget(VGroup.class);
        vGroupGW.setImmediate(true);
        vGroupGW.showToolbar(false);

        vGroupGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        vGroupGW.addEntityListener(this);

        vGroupGM = (DBGridModel) vGroupGW.getWidgetModel();
        vGroupGM.setRowNumberVisible(true);
        vGroupGM.setRowNumberWidth(30);
        vGroupGM.setMultiSelect(false);
        vGroupGM.setEntities(getList(dateField.getValue()));
        vGroupGM.setRefreshType(ERefreshType.MANUAL);


        mainVL.addComponent(topHL);
        mainVL.addComponent(vGroupGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public List<VGroup> getList(Date date){
        List<VGroup> groupList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "SELECT\n" +
                "  g.name as group_name,\n" +
                "  g.id as group_id,\n" +
                "  g.curator_id as curator_id,\n" +
                "  COUNT(DISTINCT se2.student_id) AS count_students_in_the_group,\n" +
                "  count(DISTINCT ua.user_id) AS come_in_students,\n" +
                "  (COUNT(DISTINCT se2.student_id)-count(DISTINCT ua.user_id)) as do_not_come_students,\n" +
                "  CAST ((  (count(DISTINCT ua.user_id)*100) )as FLOAT) /CAST ((   (COUNT (DISTINCT se2.student_id))  ) as FLOAT)  as percentage_of_come_in_students\n" +
                "FROM groups g\n" +
                "  INNER JOIN student_education se\n" +
                "    ON g.id = se.groups_id\n" +
                "  INNER JOIN user_arrival ua\n" +
                "    ON ua.user_id = se.student_id\n" +
                "  INNER JOIN student_education se2\n" +
                "    ON g.id = se2.groups_id\n" +
                "WHERE\n" +
                "  date_trunc('day', ua.created) = date_trunc('day' , TIMESTAMP '"+formattedDate+"')\n" +
                "  AND\n" +
                "  ua.come_in = TRUE\n" +
                "  AND\n" +
                "  ua.created = (select max(ua2.created) from user_arrival ua2 " +
                "WHERE date_trunc('day', ua2.created)= date_trunc('day' , TIMESTAMP '"+formattedDate+"') and ua2.user_id = ua.user_id)\n" +
                "GROUP BY g.name, curator_id, g.id";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VGroup vg = new VGroup();
                    vg.setGroupName((String)oo[0]);
                    vg.setGroupID((Long) oo[1]);
                    if(oo[2]!=null){
                        EMPLOYEE employee = null;
                        try{
                            employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, (ID) oo[2] );
                            vg.setCurator(employee);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    vg.setCount((Long)oo[3]);
                    vg.setIsPresent((Long)oo[4]);
                    vg.setAbsent((Long)oo[5]);
                    vg.setPercantage((Double) oo[6]);
                    groupList.add(vg);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
         sql = "SELECT\n" +
                 "  g.name as group_name,\n" +
                 "  g.id as group_id,\n" +
                 "  g.curator_id as curator_id,\n" +
                 "  COUNT(DISTINCT se.student_id) AS count_students_in_the_group,\n" +
                 "  0 AS come_in_students,\n" +
                 "  COUNT(DISTINCT se.student_id) AS  do_not_come_students,\n" +
                 "  0 as percentage_of_come_in_students\n" +
                 "FROM groups g\n" +
                 "  LEFT JOIN student_education se\n" +
                 "    ON g.id = se.groups_id\n" +
                 "GROUP BY g.name, curator_id, g.id;";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VGroup vg = new VGroup();
                    vg.setGroupName((String)oo[0]);
                    vg.setGroupID((Long) oo[1]);
                    if(oo[2]!=null){
                        EMPLOYEE employee = null;
                        try{
                            employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, (ID) oo[2] );
                            vg.setCurator(employee);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    vg.setCount((Long)oo[3]);
                    vg.setIsPresent(0L);
                    vg.setAbsent(0L);
                    vg.setPercantage(0L);

                    boolean flag = false;
                    for(VGroup vGroup : groupList){
                        if(vGroup.getGroupID()==vg.getGroupID()){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        groupList.add(vg);
                    }
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        refreshList(groupList);
        return groupList;
    }


    public List<VStudentInfo> getList(VGroup vGroup, Date date){
        List<VStudentInfo> groupList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "SELECT DISTINCT (se.student_id), u.code , case date_trunc('day', ua.created) when date_trunc('day' , TIMESTAMP '"+formattedDate+"') then true else false end\n" +
                "  FROM student_education se\n" +
                "    LEFT JOIN user_arrival ua\n" +
                "      ON ua.user_id = se.student_id\n" +
                "    INNER JOIN student s\n" +
                "      ON s.id = se.student_id\n" +
                "    INNER JOIN users u\n" +
                "      ON s.id = u.id\n" +
                "  where se.groups_id = " + vGroup.getGroupID();

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudentInfo vs = new VStudentInfo();
                    vs.setId(ID.valueOf((Long)oo[0]));
                    vs.setStudent(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT.class , vs.getId()));
                    vs.setCode((String) oo[1]);

                    Boolean flag = (Boolean) oo[2];
                    if(flag){
                        String sqlMax = "select max(created), come_in from user_arrival where user_id = "+vs.getId()+" and date_trunc('day', created)= date_trunc('day' , TIMESTAMP '"+formattedDate+"') GROUP BY come_in;";
                        String sqlMin = "select min(created), come_in from user_arrival where user_id = "+vs.getId()+" and date_trunc('day', created)= date_trunc('day' , TIMESTAMP '"+formattedDate+"') GROUP BY come_in;";
                        List<Object> tmpMaxList = new ArrayList<>();
                                try{
                                    tmpMaxList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlMax, params));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                        List<Object> tmpMinList =new ArrayList<>();
                                try{
                                    tmpMinList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlMin, params));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                        for(Object oMax : tmpMaxList){
                            Object[] ooMax = (Object[]) oMax;
                            if(!(boolean) ooMax[1]){
                                vs.setComeOUT((Date)ooMax[0]);
                            }
                        }
                        for(Object oMin : tmpMinList){
                            Object[] ooMin = (Object[]) oMin;
                            if((boolean) ooMin[1]){
                                vs.setComeIN((Date)ooMin[0]);
                            }
                        }
                    }
                    groupList.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vstudentinfo list", ex);
        }
        //refreshList(groupList);
        return groupList;
    }

    private void refreshList(List<VGroup> list) {
        ((DBGridModel) vGroupGW.getWidgetModel()).setEntities(list);
        try {
            vGroupGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(vGroupGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(vGroupGW!=null){
                    mainVL.removeComponent(vGroupGW);

                    vStudentInfoGW = new GridWidget(VStudentInfo.class);
                    vStudentInfoGW.showToolbar(false);
                    vStudentInfoGW.addEntityListener(this);
                    vStudentInfoGW.setImmediate(true);

                    vStudentInfoGM = (DBGridModel)vStudentInfoGW.getWidgetModel();
                    vStudentInfoGM.setRowNumberVisible(true);
                    vStudentInfoGM.setRowNumberWidth(30);
                    vStudentInfoGM.setRefreshType(ERefreshType.MANUAL);
                    vStudentInfoGM.setEntities(getList((VGroup) vGroupGW.getSelectedEntity(), dateField.getValue()));
                    mainVL.addComponent(vStudentInfoGW);

                    backButton.setVisible(true);
                    detalizationBtn.setVisible(true);
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
}