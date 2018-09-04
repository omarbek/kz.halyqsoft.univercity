package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VLatecomers;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import kz.halyqsoft.univercity.utils.CommonUtils;

public class GroupLatecomers implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private GridWidget vGroupGW;
    private DateField dateField;
    private DBGridModel vGroupGM;
    private Button backButton;
    private GridWidget vStudentInfoGW;
    private DBGridModel vStudentInfoGM;
    private Button detalizationBtn;

    public GroupLatecomers(){
        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        topHL = new HorizontalLayout();
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
                       // vStudentInfoGM.setEntities(getList((VLatecomers) vGroupGW.getSelectedEntity() , dateField.getValue()));
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

        vGroupGW = new GridWidget(VLatecomers.class);
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

    public List<VLatecomers> getList(Date date){
        List<VLatecomers> vLatecomersList=new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql =
                "SELECT g.name ,trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                        "  t.time_value "+
                "FROM groups g\n" +
                "  INNER JOIN student_education se\n" +
                "    ON g.id = se.groups_id\n" +
                "  INNER JOIN users u\n" +
                "    ON u.id = se.student_id\n" +
                "  INNER JOIN student_education se2\n" +
                "    ON g.id = se2.groups_id\n" +
                "  INNER JOIN user_arrival ua\n" +
                "    ON ua.user_id = se.student_id\n" +
                "  INNER JOIN schedule_detail sd\n" +
                "    ON g.id = sd.group_id\n" +
                "  INNER JOIN lesson_time lt\n" +
                "    ON sd.lesson_time_id = lt.id\n" +
                "  INNER JOIN time t\n" +
                "    ON lt.begin_time_id = t.id\n" +
                "  INNER JOIN week_day day2\n" +
                "    ON sd.week_day_id = day2.id\n" +
                "  INNER JOIN subject s3 ON sd.subject_id = s3.id\n" +
                "WHERE u.deleted = FALSE AND\n" +
                "      ua.come_in = TRUE AND\n" +
                "      sd.week_day_id = (select extract(ISODOW FROM TIMESTAMP '"+formattedDate+"')) AND\n" +
                "      date_trunc('day' , ua.created ) = date_trunc('day' , TIMESTAMP '"+formattedDate+"') AND\n" +
                "      t.time_value=\n" +
                "      (\n" +
                "        select\n" +
                "          max(t3.time_value)\n" +
                "        FROM schedule_detail sdinner\n" +
                "          INNER JOIN groups g2\n" +
                "            ON sdinner.group_id = g2.id\n" +
                "          INNER JOIN student_education s2\n" +
                "            ON g2.id = s2.groups_id\n" +
                "          INNER JOIN lesson_time t2\n" +
                "            ON sdinner.lesson_time_id = t2.id\n" +
                "          INNER JOIN time t3\n" +
                "            ON t2.begin_time_id = t3.id\n" +
                "          INNER JOIN week_day day3\n" +
                "            ON sdinner.week_day_id = day3.id\n" +
                "          INNER JOIN user_arrival ua3\n" +
                "            ON ua3.user_id = s2.student_id\n" +
                "        WHERE s2.student_id = u.id AND\n" +
                "              sdinner.week_day_id = (select extract(ISODOW FROM TIMESTAMP '"+formattedDate+"')) AND\n" +
                "              date_trunc('day' , ua3.created ) = date_trunc('day' , TIMESTAMP '"+formattedDate+"'))\n" +
                "GROUP BY g.name,FIO,t.time_value ;";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VLatecomers vl = new VLatecomers();
                    vl.setGroupName((String)oo[0]);
                    vl.setFio((String)oo[1]);
                    vl.setTime((BigDecimal)(oo[2]));
                    vLatecomersList.add(vl);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }

        refreshList(vLatecomersList);
        return vLatecomersList;
    }


    public List<VStudentInfo> getList(VLatecomers vGroup, Date date){
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

    private void refreshList(List<VLatecomers> list) {
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
//            if(entityEvent.getAction()==EntityEvent.SELECTED){
//                if(vGroupGW!=null){
//                    mainVL.removeComponent(vGroupGW);
//
//                    vStudentInfoGW = new GridWidget(VStudentInfo.class);
//                    vStudentInfoGW.showToolbar(false);
//                    vStudentInfoGW.addEntityListener(this);
//                    vStudentInfoGW.setImmediate(true);
//
//                    vStudentInfoGM = (DBGridModel)vStudentInfoGW.getWidgetModel();
//                    vStudentInfoGM.setRowNumberVisible(true);
//                    vStudentInfoGM.setRowNumberWidth(30);
//                    vStudentInfoGM.setRefreshType(ERefreshType.MANUAL);
//                   //vStudentInfoGM.setEntities(getList((VLatecomers) vGroupGW.getSelectedEntity(), dateField.getValue()));
//                    mainVL.addComponent(vStudentInfoGW);
//
//                    backButton.setVisible(true);
//                    detalizationBtn.setVisible(true);
//                }
//            }
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
