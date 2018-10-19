package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VFaculty;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentInfo;
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

public class GroupAttendance implements EntityListener {
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private HorizontalLayout buttonPanel;
    private GridWidget vGroupGW;
    private DateField dateField;
    private Button printBtn;
    private DBGridModel vGroupGM;
    private Button backButton, backButtonFaculty;
    private GridWidget vStudentInfoGW;
    private DBGridModel vStudentInfoGM;
    private Button detalizationBtn;
    private VFaculty vFaculty;
    private FacultyAttendance facultyAttendance;

    public GroupAttendance(VFaculty vFaculty, FacultyAttendance facultyAttendance) {

        this.vFaculty = vFaculty;
        this.facultyAttendance = facultyAttendance;

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        buttonPanel = CommonUtils.createButtonPanel();

        topHL = new HorizontalLayout();
        topHL.setWidth(100, Sizeable.Unit.PERCENTAGE);
        topHL.setImmediate(true);

        init();
    }

    private void init() {

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
                facultyAttendance.getBackButtonFaculty().setVisible(true);
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

                if (mainVL.getComponentIndex(vGroupGW) != -1) {
                    for (GridColumnModel gcm : vGroupGM.getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < vGroupGW.getAllEntities().size(); i++) {
                        VGroup vGroup = (VGroup) vGroupGW.getAllEntities().get(i);
                        if (vGroupGW.getCaption() != null) {
                            fileName = vGroupGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vGroup.getGroupName());
                        if (vGroup.getCurator() != null) {
                            list.add(vGroup.getCurator().toString());
                        } else {
                            list.add("");
                        }
                        list.add(vGroup.getCount().toString());
                        list.add(vGroup.getIsPresent().toString());
                        list.add(vGroup.getAbsent().toString());
                        list.add(Math.round(vGroup.getPercantage()) + "%");
                        tableBody.add(list);
                    }

                } else if (mainVL.getComponentIndex(vStudentInfoGW) != -1) {
                    for (GridColumnModel gcm : vStudentInfoGM.getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < vStudentInfoGW.getAllEntities().size(); i++) {
                        VStudentInfo vStudentInfo = (VStudentInfo) vStudentInfoGW.getAllEntities().get(i);
                        if (vStudentInfoGW.getCaption() != null) {
                            fileName = vStudentInfoGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vStudentInfo.getStudent().toString());
                        list.add(vStudentInfo.getCode());
                        list.add(vStudentInfo.getComeIN() != null ? (vStudentInfo.getComeIN()) : "");
                        list.add(vStudentInfo.getComeOUT() != null ? (vStudentInfo.getComeOUT()) : "");
                        tableBody.add(list);
                    }
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        dateField = new DateField();
        dateField.setValue(facultyAttendance.getDateField().getValue());

        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (mainVL.getComponentIndex(vGroupGW) > -1) {
                    vGroupGM.setEntities(getList(dateField.getValue()));
                } else if (mainVL.getComponentIndex(vStudentInfoGW) > -1) {
                    if (vGroupGW.getSelectedEntity() != null) {
                        vStudentInfoGM.setEntities(getList((VGroup) vGroupGW.getSelectedEntity(), dateField.getValue()));
                    }
                }
            }
        });

        detalizationBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("detalization"));
        detalizationBtn.setVisible(false);
        detalizationBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (vStudentInfoGW.getSelectedEntity() != null) {
                    DetalizationDialog detalizationDialog = new DetalizationDialog(CommonUtils.getUILocaleUtil().getCaption("detalization"), ((VStudentInfo) vStudentInfoGW.getSelectedEntity()).getStudent(), dateField.getValue());
                } else {
                    Message.showError(CommonUtils.getUILocaleUtil().getCaption("chooseARecord"));
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
        vGroupGM.getFormModel().getFieldModel("time").setInView(FALSE);

        mainVL.addComponent(topHL);
        mainVL.addComponent(vGroupGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    public List<VGroup> getList(Date date) {
        List<VGroup> groupList = new ArrayList<>();

        Map<Integer, Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        String sql = "SELECT " +
                "  DISTINCT " +
                "  g.name                                                            AS group_name, " +
                "  g.id                                                              AS group_id, " +
                "  g.curator_id                                                      AS curator_id, " +
                "  COUNT(DISTINCT " +
                "      se2.student_id)                                               AS count_students_in_the_group, " +
                "  count(DISTINCT " +
                "      ua.user_id)                                                   AS come_in_students, " +
                "  (COUNT(DISTINCT se2.student_id) - count(DISTINCT " +
                "      ua.user_id))                                                  AS do_not_come_students, " +
                "  CAST(((count(DISTINCT ua.user_id) * 100)) AS FLOAT) / CAST(((COUNT(DISTINCT se2.student_id))) AS " +
                "                                                             FLOAT) AS percentage_of_come_in_students " +
                "FROM groups g " +
                "  INNER JOIN student_education se " +
                "    ON g.id = se.groups_id " +
                "  INNER JOIN user_arrival ua " +
                "    ON ua.user_id = se.student_id " +
                "  INNER JOIN student_education se2 " +
                "    ON g.id = se2.groups_id " +
                "  INNER JOIN speciality s2 " +
                "    ON g.speciality_id = s2.id " +
                "  INNER JOIN department d2 " +
                "    ON s2.chair_id = d2.id " +
                "WHERE " +
                "  date_trunc('day', ua.created) = date_trunc('day' , TIMESTAMP '" + formattedDate + "')" +
                "  AND " +
                "  ua.come_in = TRUE " +
                "  AND " +
                "  ua.created = (SELECT max(ua2.created) " +
                "                FROM user_arrival ua2 " +
                "                WHERE date_trunc('day', ua.created) = date_trunc('day' , TIMESTAMP '" + formattedDate + "')" +
                "                      and ua2.come_in = TRUE AND ua2.user_id = ua.user_id) " +
                "  AND d2.parent_id = " + vFaculty.getFacultyID() +
                " GROUP BY g.name, curator_id, g.id;";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VGroup vg = new VGroup();
                    vg.setGroupName((String) oo[0]);
                    vg.setGroupID((Long) oo[1]);
                    if (oo[2] != null) {
                        EMPLOYEE employee = null;
                        try {
                            employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, (ID) oo[2]);
                            vg.setCurator(employee);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    vg.setCount((Long) oo[3]);
                    vg.setIsPresent((Long) oo[4]);
                    vg.setAbsent((Long) oo[5]);
                    vg.setPercantage((Double) oo[6]);
                    groupList.add(vg);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        refreshList(groupList);
        return groupList;
    }


    public List<VStudentInfo> getList(VGroup vGroup, Date date) {
        List<VStudentInfo> groupList = new ArrayList<>();

        Map<Integer, Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "SELECT DISTINCT " +
                "  (se.student_id), " +
                "  u.code, " +
                "  CASE date_trunc('day', ua.created) " +
                "  WHEN date_trunc('day', TIMESTAMP '" + formattedDate + "') " +
                "    THEN TRUE " +
                "  ELSE FALSE END is_today " +
                "FROM student_education se " +
                "  LEFT JOIN user_arrival ua " +
                "    ON ua.user_id = se.student_id " +
                "  INNER JOIN student s " +
                "    ON s.id = se.student_id " +
                "  INNER JOIN users u " +
                "    ON s.id = u.id " +
                "WHERE se.groups_id = " + vGroup.getGroupID() +
                "ORDER BY is_today DESC;";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            Set<ID> userIds = new HashSet<>();
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudentInfo vs = new VStudentInfo();
                    vs.setId(ID.valueOf((Long) oo[0]));
                    ID userId = vs.getId();
                    if (userIds.contains(userId)) {
                        continue;
                    } else {
                        userIds.add(userId);
                    }
                    vs.setStudent(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT.class, userId));
                    vs.setCode((String) oo[1]);

                    Boolean flag = (Boolean) oo[2];
                    if (flag) {
                        String sqlMax = "select (date_trunc('second', max(created))::time)::text, come_in " +
                                "from user_arrival where user_id = " + userId.getId() +
                                " and date_trunc('day', created)= date_trunc('day' , TIMESTAMP '" +
                                formattedDate + "') and come_in = false GROUP BY come_in;";
                        String sqlMin = "select (date_trunc('second', min(created))::time)::text, come_in " +
                                "from user_arrival where user_id = " + userId.getId() +
                                " and date_trunc('day', created)= date_trunc('day' , TIMESTAMP '" +
                                formattedDate + "') and come_in = true GROUP BY come_in;";
                        List<Object> tmpMaxList = new ArrayList<>();
                        try {
                            tmpMaxList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlMax, params));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        List<Object> tmpMinList = new ArrayList<>();
                        try {
                            tmpMinList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlMin, params));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for (Object oMax : tmpMaxList) {
                            Object[] ooMax = (Object[]) oMax;
                            if (!(boolean) ooMax[1]) {
                                vs.setComeOUT((String) ooMax[0]);
                            }
                        }
                        for (Object oMin : tmpMinList) {
                            Object[] ooMin = (Object[]) oMin;
                            if ((boolean) ooMin[1]) {
                                vs.setComeIN((String) ooMin[0]);
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
        if (entityEvent.getSource().equals(vGroupGW)) {
            if (entityEvent.getAction() == EntityEvent.SELECTED) {
                if (vGroupGW != null) {
                    mainVL.removeComponent(vGroupGW);

                    vStudentInfoGW = new GridWidget(VStudentInfo.class);
                    vStudentInfoGW.showToolbar(false);
                    vStudentInfoGW.addEntityListener(this);
                    vStudentInfoGW.setImmediate(true);

                    vStudentInfoGM = (DBGridModel) vStudentInfoGW.getWidgetModel();
                    vStudentInfoGM.setRowNumberVisible(true);
                    vStudentInfoGM.setRowNumberWidth(30);
                    vStudentInfoGM.setRefreshType(ERefreshType.MANUAL);
                    vStudentInfoGM.setEntities(getList((VGroup) vGroupGW.getSelectedEntity(), dateField.getValue()));

                    mainVL.addComponent(vStudentInfoGW);
                    backButton.setVisible(true);
                    detalizationBtn.setVisible(true);
                    facultyAttendance.getBackButtonFaculty().setVisible(false);
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
