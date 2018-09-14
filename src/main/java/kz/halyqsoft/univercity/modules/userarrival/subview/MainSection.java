package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.elements.Rectangle;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.filter.FStatisticsFilter;
import kz.halyqsoft.univercity.filter.panel.StatisticsFilterPanel;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainSection implements FilterPanelListener {
    private VerticalLayout mainVL;
    private DateField dateField;

    private Long allUsers = 0L;
    private Long allStudents = 0L;
    private Long allEmployees = 0L;

    public MainSection() {
        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);
        mainVL.setSizeFull();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        dateField = new DateField();
        dateField.setImmediate(true);
        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                setValues();
            }
        });
        dateField.setValue(new Date());

        setValues();
    }

    public List<VTopUserArrival> getUserList(Date date) {
        List<VTopUserArrival> userList = new ArrayList<>();

        Map<Integer, Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "SELECT\n" +
                "  id,\n" +
                "  fio ,\n" +
                "  (CASE WHEN present_lesson > 0 THEN all_lessons/present_lesson*100 ELSE 0 END)::DOUBLE PRECISION  as percentage,\n" +
                "  group_name \n" +
                "  from (SELECT\n" +
                "  vs.id,\n" +
                "  concat(vs.first_name, ' ', vs.last_name, ' ', vs.middle_name) AS fio,\n" +
                "  l.lesson_date,\n" +
                "  count(l.id)                                                   AS all_lessons,\n" +
                "  sum(CASE WHEN ld.attendance_mark = 1\n" +
                "    THEN 1\n" +
                "      ELSE 0 END)                                               AS present_lesson,\n" +
                "  sum(CASE WHEN ld.attendance_mark = 0\n" +
                "    THEN 1\n" +
                "      ELSE 0 END)                                               AS absent_lesson,\n" +
                "  vs.group_name\n" +
                "FROM v_student vs\n" +
                "  INNER JOIN student_education se\n" +
                "    ON vs.id = se.student_id\n" +
                "  INNER JOIN lesson_detail ld\n" +
                "    ON se.id = ld.student_id\n" +
                "  INNER JOIN lesson l\n" +
                "    ON ld.lesson_id = l.id\n" +
                "WHERE date_trunc('month', l.lesson_date) IN (date_trunc('month', TIMESTAMP '"+CommonUtils.getFormattedDate(date)+"'))\n" +
                "      AND l.canceled = FALSE\n" +
                "GROUP BY vs.id, fio, l.lesson_date, vs.group_name) newselect ORDER BY CASE WHEN present_lesson > 0\n" +
                "    THEN all_lessons / present_lesson * 100\n" +
                "   ELSE 0 END DESC;";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VTopUserArrival topUser = new VTopUserArrival();
                    topUser.setId(ID.valueOf((Long) oo[0]));
                    topUser.setFio((String) oo[1]);
                    topUser.setPercentage((Double) oo[2]);
                    topUser.setGroup((String) oo[3]);
                    userList.add(topUser);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        return userList;
    }

    public List<VTopGroupArrival> getGroupList(Date date) {
        List<VTopGroupArrival> groupList = new ArrayList<>();

        Map<Integer, Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "SELECT\n" +
                "  g.name as group_name,\n" +
                "  g.curator_id as curator_id,\n" +
                "  COUNT(DISTINCT se2.student_id) AS count_students_in_the_group,\n" +
                "  count(DISTINCT ua.user_id) AS come_in_students,\n" +
                "  CAST ((  (count(DISTINCT ua.user_id)*100) )as FLOAT) /CAST ((   (COUNT (DISTINCT se2.student_id))  ) as FLOAT)  as percentage_of_come_in_students\n" +
                "FROM groups g\n" +
                "  INNER JOIN student_education se\n" +
                "    ON g.id = se.groups_id\n" +
                "  INNER JOIN user_arrival ua\n" +
                "    ON ua.user_id = se.student_id\n" +
                "  INNER JOIN student_education se2\n" +
                "    ON g.id = se2.groups_id\n" +
                "WHERE\n" +
                "  date_trunc('day', ua.created) = date_trunc('day' , TIMESTAMP '" + formattedDate + "')\n" +
                "  AND\n" +
                "  ua.come_in = TRUE\n" +
                "  AND\n" +
                "  ua.created = (select max(ua2.created) from user_arrival ua2 " +
                "WHERE date_trunc('day', ua2.created)= date_trunc('day' , TIMESTAMP '" + formattedDate + "') and ua2.user_id = ua.user_id)\n" +
                "GROUP BY g.name, curator_id, g.id";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VTopGroupArrival topGroup = new VTopGroupArrival();
                    topGroup.setGroup((String) oo[0]);
                    if (oo[1] != null) {
                        EMPLOYEE employee = null;
                        try {
                            employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, ID.valueOf((Long)oo[2]));
                            topGroup.setTeacher(employee.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    topGroup.setSumOfStudent((Long) oo[2]);
                    topGroup.setAttend((Long) oo[3]);
                    topGroup.setPercent((double) oo[4]);
                    groupList.add(topGroup);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        return groupList;
    }


    private Component getStatistics() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainVL.setImmediate(true);
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label label = new Label();
        label.setSizeFull();
        label.setCaptionAsHtml(true);
        label.setImmediate(true);
        label.setCaption("<h2>    " + CommonUtils.getUILocaleUtil().getCaption("attendance") + "</h2>" );
        mainVL.addComponent(label);
        try{
            mainVL.addComponent(getFilter());
        }catch (Exception e){
            e.printStackTrace();
        }

        mainVL.addComponent(getVerticalBar());

        return mainVL;
    }

    private AbstractFilterPanel getFilter() throws Exception{
        StatisticsFilterPanel statisticsFP = new StatisticsFilterPanel(new FStatisticsFilter());
        statisticsFP.addFilterPanelListener(this);
        statisticsFP.setImmediate(true);

        ComboBox facultyCB = new ComboBox();
        facultyCB.setNullSelectionAllowed(false);
        facultyCB.setTextInputAllowed(true);
        facultyCB.setFilteringMode(FilteringMode.CONTAINS);
        facultyCB.setWidth(200, Sizeable.Unit.PIXELS);
        QueryModel<DEPARTMENT> facultyQM = new QueryModel<>(DEPARTMENT.class);
        facultyQM.addWhere("deleted" , ECriteria.EQUAL,false);
        facultyQM.addWhereNullAnd("parent");
        BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(facultyQM));
        facultyCB.setContainerDataSource(facultyBIC);
        statisticsFP.addFilterComponent("department", facultyCB);

        DateField startingDateDF = new DateField();
        statisticsFP.addFilterComponent("startingDate", startingDateDF);

        DateField endingDateDF = new DateField();
        statisticsFP.addFilterComponent("endingDate", endingDateDF);
        return statisticsFP;
    }

    private Component getVerticalBar(){
        BarChartConfig barConfig = new BarChartConfig();
        barConfig.horizontal();
        barConfig.
                data()
                .labels("January", "February", "March", "April", "May", "June", "July")
                .addDataset(new BarDataset().backgroundColor("rgba(220,220,220,0.5)").label("Dataset 1"))
                .addDataset(new BarDataset().backgroundColor("rgba(151,187,205,0.5)").label("Dataset 2").hidden(true))
                .addDataset(new BarDataset().backgroundColor("rgba(151,187,205,0.5)").label("Dataset 3"))
                .and()
                .options()
                .responsive(true)
                .title()
                .display(true)
                .text("Chart.js Horizontal Bar Chart")
                .and()
                .elements()
                .rectangle()
                .borderWidth(2)
                .borderColor("rgb(0, 255, 0)")
                .borderSkipped(Rectangle.RectangleEdge.LEFT)
                .and()
                .and()
                .legend()
                .fullWidth(false)
                .position(Position.LEFT)
                .and()
                .done();

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;
            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add((Math.random() > 0.5 ? 1.0 : -1.0) * Math.round(Math.random() * 100));
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setImmediate(true);
        chart.setSizeFull();

        return chart;
    }

    public Component setFooterTables() {
        VerticalLayout innerVL = new VerticalLayout();
        innerVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        innerVL.setSizeFull();
        innerVL.setImmediate(true);

        Label label = new Label();
        label.setImmediate(true);
        label.setCaptionAsHtml(true);
        label.setCaption("<h2>    " + CommonUtils.getUILocaleUtil().getCaption("best.attendance") +"</h2>");
        label.setSizeFull();


        GridLayout topPerformanceGL = new GridLayout();
        topPerformanceGL.setSizeFull();
        topPerformanceGL.setColumns(2);
        topPerformanceGL.setRows(2);
        topPerformanceGL.setSpacing(true);

        GridWidget bestStudentsGW = new GridWidget(VTopUserArrival.class);
        bestStudentsGW.setSizeFull();
        bestStudentsGW.showToolbar(false);
        bestStudentsGW.setCaption(CommonUtils.getUILocaleUtil().getCaption("top.ten.students"));

        DBGridModel bestStudentsGM = (DBGridModel) bestStudentsGW.getWidgetModel();
        bestStudentsGM.setRefreshType(ERefreshType.MANUAL);
        bestStudentsGM.setEntities(getUserList(dateField.getValue()));



        GridWidget bestGroupsGW = new GridWidget(VTopGroupArrival.class);
        bestGroupsGW.setSizeFull();
        bestGroupsGW.showToolbar(false);
        bestGroupsGW.setCaption(CommonUtils.getUILocaleUtil().getCaption("top.ten.groups"));

        DBGridModel bestGroupsGM = (DBGridModel) bestGroupsGW.getWidgetModel();
        bestGroupsGM.setRefreshType(ERefreshType.MANUAL);
        bestGroupsGM.setEntities(getGroupList(dateField.getValue()));

        Button printBtn1 = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn1.setImmediate(true);
        printBtn1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody= new ArrayList<>();

                String fileName = "document";

                for(GridColumnModel gcm : bestStudentsGM.getColumnModels()){
                    tableHeader.add(gcm.getLabel());
                }
                for(int i = 0 ; i < bestStudentsGW.getAllEntities().size(); i++){
                    VTopUserArrival vUser = (VTopUserArrival) bestStudentsGW.getAllEntities().get(i);
                    if(bestStudentsGW.getCaption()!=null){
                        fileName = bestStudentsGW.getCaption();
                    }
                    List<String> list = new ArrayList<>();
                    list.add(vUser.getFio());
                    list.add(vUser.getGroup());
                    list.add(vUser.getPercentage().toString());
                    tableBody.add(list);
                }



                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody , CommonUtils.getUILocaleUtil().getCaption("print"),fileName);
            }
        });

        Button printBtn2 = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn2.setImmediate(true);
        printBtn2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody= new ArrayList<>();

                String fileName = "document";

                for(GridColumnModel gcm : bestGroupsGM.getColumnModels()){
                    tableHeader.add(gcm.getLabel());
                }
                for(int i = 0 ; i < bestGroupsGW.getAllEntities().size(); i++){
                    VTopGroupArrival vGroup = (VTopGroupArrival) bestGroupsGW.getAllEntities().get(i);
                    if(bestGroupsGW.getCaption()!=null){
                        fileName = bestGroupsGW.getCaption();
                    }
                    List<String> list = new ArrayList<>();
                    list.add(vGroup.getGroup());
                    list.add(vGroup.getTeacher());
                    list.add(vGroup.getAttend()+"");
                    list.add(vGroup.getPercent()+"");
                    list.add(vGroup.getSumOfStudent()+"");
                    tableBody.add(list);
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody , CommonUtils.getUILocaleUtil().getCaption("print"),fileName);
            }
        });

        topPerformanceGL.addComponent(printBtn1);
        topPerformanceGL.addComponent(printBtn2);
        topPerformanceGL.addComponent(bestStudentsGW);
        topPerformanceGL.addComponent(bestGroupsGW);

        innerVL.addComponent(label);
        innerVL.addComponent(topPerformanceGL);

        return innerVL;
    }


    private void setValues() {

        try {
            if(mainVL.getComponentCount()>0){
                mainVL.removeAllComponents();
            }

            mainVL.addComponent(dateField);
            mainVL.setComponentAlignment(dateField,Alignment.TOP_RIGHT);

            HorizontalLayout mainHL;
            mainHL = new HorizontalLayout();
            mainHL.setImmediate(true);
            mainHL.setSizeFull();
            mainHL.setSpacing(true);
            mainHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

            mainHL.addComponent(setStudents());
            mainHL.addComponent(setLaters());
            mainHL.addComponent(setEmployees());
            mainHL.addComponent(setNoCards());

            mainVL.addComponent(mainHL);
            mainVL.addComponent(getStatistics());
            mainVL.addComponent(setFooterTables());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    private Panel setNoCards() {
        Object[] info = getCardInfo();
        long all = (Long) info[0];
        long withcards = (Long) info[1];
        long nocards = all - withcards;

        Double dall = new Double(all);
        Double dwithcards = new Double(withcards);

        Panel noCardButton = new Panel("<html><b>" + CommonUtils.getUILocaleUtil().getCaption("noCardButton") + "</b><br>" +
                Math.round(dwithcards * 100 / dall) + "%<br>" +
                "(" + nocards + " из " + all + ")</html>");
        noCardButton.setIcon(new ThemeResource("img/card.png"));
        noCardButton.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent event) {
                Message.showInfo("no card");
            }
        });

        return noCardButton;
    }

    private Panel setEmployees() throws Exception {
        QueryModel<V_EMPLOYEE> employeeQM = new QueryModel<>(V_EMPLOYEE.class);
        employeeQM.addSelect("code", EAggregate.COUNT);
        employeeQM.addWhere("deleted", Boolean.FALSE);
        Long allEmployees = (Long) SessionFacadeFactory.
                getSessionFacade(CommonEntityFacadeBean.class).lookupItems(employeeQM);

        String sql = "SELECT count(1) " +
                "FROM user_arrival arriv INNER JOIN v_employee empl ON empl.id = arriv.user_id " +
                "WHERE date_trunc('day', arriv.created) = date_trunc('day', TIMESTAMP '" + CommonUtils.getFormattedDate(dateField.getValue()) + "') " +
                "      AND arriv.created = (SELECT max(max_arriv.created) " +
                "                           FROM user_arrival max_arriv " +
                " WHERE max_arriv.user_id = arriv.user_id) " +
                "      AND come_in = TRUE";
        Long inEmployees = (Long) SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
        long inPercent = inEmployees * 100 / allEmployees;

        Panel employeesPanel = new Panel("<b>" + CommonUtils.getUILocaleUtil().getCaption("employeesPanel") + " - " + allEmployees + "</b><br>" +
                CommonUtils.getUILocaleUtil().getCaption("attendanceOf") + " - " + inEmployees + " (" + inPercent + "%)<br>" +
                CommonUtils.getUILocaleUtil().getCaption("absentOf") + " - " + (allEmployees - inEmployees) + " (" + (100 - inPercent) + "%)" +
                "<br>");
        employeesPanel.setIcon(new ThemeResource("img/employee.png"));
        employeesPanel.setHeight(155, Sizeable.Unit.PIXELS);
        employeesPanel.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent event) {
                Message.showInfo("employees");
            }
        });
        return employeesPanel;
    }

    private Object[] getCardInfo() {
        Map<Integer, Object> params = new HashMap<>();
        String sql = "select\n" +
                "  (select count(id) FROM users where deleted = FALSE ) as all,\n" +
                "  (select count(u.id) from users u INNER JOIN card c2 ON u.card_id = c2.id where u.deleted = FALSE ) as havecards;";

        try {
            Object o = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

            return (Object[]) ((Vector) o).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Panel setLaters() {
        Panel latersPanel = new Panel("<html><b>" + CommonUtils.getUILocaleUtil().getCaption("latersPanel") + "</b><br>" +
                "9.8%<br>" +
                "(313 из 3209)</html>");
        latersPanel.setIcon(new ThemeResource("img/clock.png"));
        latersPanel.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent event) {
                Message.showInfo("laters");
            }
        });
        return latersPanel;
    }

    private Panel setStudents() throws Exception {
        QueryModel<V_STUDENT> studentQM = new QueryModel<>(V_STUDENT.class);
        studentQM.addSelect("userCode", EAggregate.COUNT);
        studentQM.addWhere("deleted", Boolean.FALSE);
        Long allStudents = (Long) SessionFacadeFactory.
                getSessionFacade(CommonEntityFacadeBean.class).lookupItems(studentQM);

        String sql = "SELECT count(1) " +
                "FROM user_arrival arriv INNER JOIN v_student stu ON stu.id = arriv.user_id " +
                "WHERE date_trunc('day', arriv.created) = date_trunc('day',  TIMESTAMP '" + CommonUtils.getFormattedDate(dateField.getValue()) + "') " +
                "      AND arriv.created = (SELECT max(max_arriv.created) " +
                "                           FROM user_arrival max_arriv " +
                "                           WHERE max_arriv.user_id = arriv.user_id) " +
                "      AND come_in = TRUE";
        Long inStudents = (Long) SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
        long inPercent = inStudents * 100 / allStudents;

        Panel studentsPanel = new Panel("<b>" + CommonUtils.getUILocaleUtil().getCaption("studentsPanel") + " - " + allStudents + "</b><br>" +
                "" + CommonUtils.getUILocaleUtil().getCaption("attendanceOf") + " - " + inStudents + " (" + inPercent + "%)<br>" +
                "" + CommonUtils.getUILocaleUtil().getCaption("absentOf") + " - " + (allStudents - inStudents) + " (" + (100 - inPercent) + "%)" +
                "<br>");
        studentsPanel.setIcon(new ThemeResource("img/student.png"));
        studentsPanel.addClickListener(new MouseEvents.ClickListener() {

            @Override
            public void click(MouseEvents.ClickEvent event) {
                Message.showInfo("Students");
            }
        });
        return studentsPanel;
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {

    }

    @Override
    public void clearFilter() {

    }
}