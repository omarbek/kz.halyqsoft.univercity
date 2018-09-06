package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.data.Property;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VTopGroupArrival;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VTopUserArrival;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSection {
    private VerticalLayout mainVL;
    private HorizontalLayout mainHL;
    private DateField dateField;

    private Long allUsers = 0L;
    private Long allStudents = 0L;
    private Long allEmployees = 0L;
    GridWidget topTenGroup;

    public MainSection(){
        dateField = new DateField();
        dateField.setImmediate(true);
        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                setValues();
            }
        });
        dateField.setValue(new Date());

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);
        mainVL.setSizeFull();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        mainHL = new HorizontalLayout();
        mainHL.setImmediate(true);
        mainHL.setSizeFull();
        mainHL.setSpacing(true);
        mainHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setSizeFull();
        gridLayout.setColumns(2);
        gridLayout.setRows(1);
        gridLayout.setSpacing(true);

        GridLayout gridLayoutNO = new GridLayout();
        gridLayoutNO.setSizeFull();
        gridLayoutNO.setColumns(2);
        gridLayoutNO.setRows(1);
        gridLayoutNO.setSpacing(true);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        Component verticalBar = getVerticalBar(dateField.getValue());
        verticalBar.setSizeFull();

        Component verticalBar1 = getVerticalBar1();
        verticalBar1.setSizeFull();

        Component verticalBar2 = getVerticalBar2();
        verticalBar1.setSizeFull();

        Component verticalBar3 = getVerticalBar3();
        verticalBar1.setSizeFull();

        Component verticalBar4 = getVerticalBar4();
        verticalBar1.setSizeFull();

        Component verticalBar5 = getVerticalBar5();
        verticalBar1.setSizeFull();

        Component tablewidget = setTopTen();
        tablewidget.setSizeFull();
        tablewidget.setCaption("Top student");

        Component tablewidget1 = setTopGroup();
        tablewidget1.setSizeFull();
        tablewidget1.setCaption("Top group");

        mainVL.addComponent(dateField);
        mainVL.setComponentAlignment(dateField ,Alignment.TOP_RIGHT);
        setValues();
        mainVL.addComponent(mainHL);

        gridLayout.addComponent(verticalBar,0,0);
        gridLayout.addComponent(verticalBar1);
        mainVL.addComponent(gridLayout);

        //Component verticalBar = getVerticalBar(dateField.getValue());
        //verticalBar.setSizeFull();
        Component pieChart = getPieChart();
        pieChart.setSizeFull();

        verticalLayout.addComponent(verticalBar2);
        verticalLayout.addComponent(verticalBar3);
        verticalLayout.addComponent(verticalBar4);
        verticalLayout.addComponent(verticalBar5);
        mainVL.addComponent(verticalLayout);

        gridLayoutNO.addComponent(tablewidget);
        gridLayoutNO.addComponent(tablewidget1);
        mainVL.addComponent(gridLayoutNO);

        //mainVL.addComponent(verticalBar);
        mainVL.addComponent(pieChart);


        setFooterTables();
    }

    public Component setTopTen(){
        GridWidget topTen = new GridWidget(VTopUserArrival.class);
        topTen.setSizeFull();

        DBGridModel topTenMOdel = (DBGridModel) topTen.getWidgetModel();
        topTenMOdel.setRefreshType(ERefreshType.MANUAL);

        return topTen;
    }

    public Component setTopGroup(){
        topTenGroup = new GridWidget(VTopGroupArrival.class);
        topTenGroup.setSizeFull();

        DBGridModel topTenMOdel = (DBGridModel) topTenGroup.getWidgetModel();
        topTenMOdel.setRefreshType(ERefreshType.MANUAL);
        topTenMOdel.setEntities(getList(dateField.getValue()));

        return topTenGroup;
    }
    public List<VTopGroupArrival> getList(Date date){
        List<VTopGroupArrival> groupList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
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
                    VTopGroupArrival topGroup = new VTopGroupArrival();
                    topGroup.setGroup((String)oo[0]);
                    if(oo[1]!=null){
                        EMPLOYEE employee = null;
                        try{
                            employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, (ID) oo[2] );
                            topGroup.setTeacher(employee.toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    topGroup.setSumOfStudent((Long)oo[2]);
                    topGroup.setAttend((Long)oo[3]);
                    topGroup.setPercent((double)oo[4]);
                    groupList.add(topGroup);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        refreshList(groupList);
        return groupList;
    }

    private void refreshList(List<VTopGroupArrival> list) {
        ((DBGridModel) topTenGroup.getWidgetModel()).setEntities(list);
        try {
            topTenGroup.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }

    private Component getVerticalBar(Date date){

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        Map<Integer,Object> params = new HashMap<>();
        BarChartConfig barConfig = new BarChartConfig();

        String sql = "select sum(come_in_all) , sum(come_in_true),sum(come_in_false) from (select\n" +
                "  count(ua.come_in) as come_in_all,\n" +
                "  count(nullif(ua.come_in = false, true)) as come_in_true,\n" +
                "  count(nullif(ua.come_in = true, true)) as come_in_false\n" +
                "from student s\n" +
                "  inner join user_arrival ua\n" +
                "    on ua.user_id = s.id\n" +
                "  inner join users u\n" +
                "    on s.id = u.id\n" +
                "where date_trunc('day', ua.created) = date_trunc('day', timestamp '"+formattedDate+"')\n" +
                "      and u.deleted = true\n" +
                "group by s.id) newselect";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    barConfig.
                            data()
                            .labels("student attends")
                            .addDataset(
                                    new BarDataset().backgroundColor(
                                            ColorUtils.randomColor(1.0)).label((BigDecimal) oo[1]+"").yAxisID("y-axis-1"))
                            .and();
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
                    barConfig.
                            options()
                            .responsive(true)
                            .hover()
                            .mode(InteractionMode.INDEX)
                            .intersect(true)
                            .animationDuration(400)
                            .and()
                            .title()
                            .display(true)
                            .text("by day")
                            .and()
                            .scales()
                            .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                            .and()
                            .done();

                    List<String> labels = barConfig.data().getLabels();
                    for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
                        BarDataset lds = (BarDataset) ds;

                        List<Double> data = new ArrayList<>();
                        for (int i = 0; i < labels.size(); i++) {
                            data.add(i,3.0);
                        }
                        lds.dataAsList(data);
                    }
        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar1(){
        QueryModel<STUDY_YEAR> group = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
        List<STUDY_YEAR> course = null;
        try{
            course = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(group);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(STUDY_YEAR year:course) {
            barConfig.
                    data()
                    .labels("info about course attend in day")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(year.getStudyYear().toString()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("by week")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,3.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar2(){
        QueryModel<STUDY_YEAR> group = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
        List<STUDY_YEAR> course = null;
        try{
            course = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(group);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(STUDY_YEAR year:course) {
            barConfig.
                    data()
                    .labels("info about course attend in week")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(year.getStudyYear().toString()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("Chart.js Bar Chart - Multi Axis")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar3(){
        QueryModel<GROUPS> groupQM = new QueryModel<GROUPS>(GROUPS.class);
        groupQM.addWhere("deleted",ECriteria.EQUAL,false);
        List<GROUPS> groups = null;
        try{
            groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(GROUPS gr:groups) {
            barConfig.
                    data()
                    .labels("info about groups attend in day")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(gr.getName()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("Chart.js Bar Chart - Multi Axis")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-2"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-3"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar4(){
        QueryModel<GROUPS> groupQM = new QueryModel<GROUPS>(GROUPS.class);
        groupQM.addWhere("deleted",ECriteria.EQUAL,false);
        List<GROUPS> groups = null;
        try{
            groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(GROUPS gr:groups) {
            barConfig.
                    data()
                    .labels("info about groups attend in week")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(gr.getName()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("Chart.js Bar Chart - Multi Axis")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-2"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-3"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar5(){
    public void setFooterTables(){
        GridLayout gridLayoutNO = new GridLayout();
        gridLayoutNO.setSizeFull();
        gridLayoutNO.setColumns(2);
        gridLayoutNO.setRows(1);
        gridLayoutNO.setSpacing(true);

        Component tablewidget = setTopTen();
        tablewidget.setSizeFull();
        tablewidget.setCaption("Top student");

        Component tablewidget1 = setTopGroup();
        tablewidget1.setSizeFull();
        tablewidget1.setCaption("Top group");

        gridLayoutNO.addComponent(tablewidget);
        gridLayoutNO.addComponent(tablewidget1);
        mainVL.addComponent(gridLayoutNO);
    }

    public Component setTopTen(){
        GridWidget topTen = new GridWidget(VTopUserArrival.class);
        topTen.setSizeFull();

        DBGridModel topTenMOdel = (DBGridModel) topTen.getWidgetModel();
        topTenMOdel.setRefreshType(ERefreshType.MANUAL);

        return topTen;
    }

    public Component setTopGroup(){
        topTenGroup = new GridWidget(VTopGroupArrival.class);
        topTenGroup.setSizeFull();

        DBGridModel topTenMOdel = (DBGridModel) topTenGroup.getWidgetModel();
        topTenMOdel.setRefreshType(ERefreshType.MANUAL);
        topTenMOdel.setEntities(getList(dateField.getValue()));

        return topTenGroup;
    }

    public List<VTopGroupArrival> getList(Date date){
        List<VTopGroupArrival> groupList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
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
                    VTopGroupArrival topGroup = new VTopGroupArrival();
                    topGroup.setGroup((String)oo[0]);
                    if(oo[1]!=null){
                        EMPLOYEE employee = null;
                        try{
                            employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, (ID) oo[2] );
                            topGroup.setTeacher(employee.toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    topGroup.setSumOfStudent((Long)oo[2]);
                    topGroup.setAttend((Long)oo[3]);
                    topGroup.setPercent((double)oo[4]);
                    groupList.add(topGroup);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        refreshList(groupList);
        return groupList;
    }

    private void refreshList(List<VTopGroupArrival> list) {
        ((DBGridModel) topTenGroup.getWidgetModel()).setEntities(list);
        try {
            topTenGroup.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }

    private Component getPieChart(){
        PieChartConfig config = new PieChartConfig();
        config
                .data()
                .labels("All", "Green", "Yellow", "Grey", "Dark Grey")
                .addDataset(new PieDataset().label("Dataset 1"))
                .and();

        config.
                options()
                .responsive(true)
                .title()
                .display(true)
                .text("Chart.js Single Pie Chart")
                .and()
                .animation()
                .animateScale(true)
                .animateRotate(true)
                .and()
                .done();

        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {
            PieDataset lds = (PieDataset) ds;
            List<Double> data = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add((double) (Math.round(Math.random() * 100)));
                colors.add(ColorUtils.randomColor(0.7));
            }
            lds.backgroundColor(colors.toArray(new String[colors.size()]));
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);

        return chart;
    }

    private Component getVerticalBar(Date date){

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
        Map<Integer,Object> params = new HashMap<>();
        BarChartConfig barConfig = new BarChartConfig();

        String sql = "select sum(come_in_all) , sum(come_in_true),sum(come_in_false) from (select\n" +
                "  count(ua.come_in) as come_in_all,\n" +
                "  count(nullif(ua.come_in = false, true)) as come_in_true,\n" +
                "  count(nullif(ua.come_in = true, true)) as come_in_false\n" +
                "from student s\n" +
                "  inner join user_arrival ua\n" +
                "    on ua.user_id = s.id\n" +
                "  inner join users u\n" +
                "    on s.id = u.id\n" +
                "where date_trunc('day', ua.created) = date_trunc('day', timestamp '"+formattedDate+"')\n" +
                "      and u.deleted = true\n" +
                "group by s.id) newselect";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    barConfig.
                            data()
                            .labels("student attends")
                            .addDataset(
                                    new BarDataset().backgroundColor(
                                            ColorUtils.randomColor(1.0)).label(oo[1] +"").yAxisID("y-axis-1"))
                            .and();
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load vgroup list", ex);
        }
        barConfig.
                options()
                .responsive(true)
                .hover()
                .mode(InteractionMode.INDEX)
                .intersect(true)
                .animationDuration(400)
                .and()
                .title()
                .display(true)
                .text("by day")
                .and()
                .scales()
                .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                .and()
                .done();

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,3.0);
            }
            lds.dataAsList(data);
        }
        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar1(){
        QueryModel<STUDY_YEAR> group = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
        List<STUDY_YEAR> course = null;
        try{
            course = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(group);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(STUDY_YEAR year:course) {
            barConfig.
                    data()
                    .labels("info about course attend in day")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(year.getStudyYear().toString()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("by week")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,3.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar2(){
        QueryModel<STUDY_YEAR> group = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
        List<STUDY_YEAR> course = null;
        try{
            course = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(group);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(STUDY_YEAR year:course) {
            barConfig.
                    data()
                    .labels("info about course attend in week")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(year.getStudyYear().toString()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("Chart.js Bar Chart - Multi Axis")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar3(){
        QueryModel<GROUPS> groupQM = new QueryModel<GROUPS>(GROUPS.class);
        groupQM.addWhere("deleted",ECriteria.EQUAL,false);
        List<GROUPS> groups = null;
        try{
            groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(GROUPS gr:groups) {
            barConfig.
                    data()
                    .labels("info about groups attend in day")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(gr.getName()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("Chart.js Bar Chart - Multi Axis")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-2"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-3"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar4(){
        QueryModel<GROUPS> groupQM = new QueryModel<GROUPS>(GROUPS.class);
        groupQM.addWhere("deleted",ECriteria.EQUAL,false);
        List<GROUPS> groups = null;
        try{
            groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupQM);
        }catch(Exception e){
            e.printStackTrace();
        }
        BarChartConfig barConfig = new BarChartConfig();
        for(GROUPS gr:groups) {
            barConfig.
                    data()
                    .labels("info about groups attend in week")
                    .addDataset(
                            new BarDataset().backgroundColor(
                                    ColorUtils.randomColor(1.0)).label(gr.getName()).yAxisID("y-axis-1"))
                    .and();
            barConfig.
                    options()
                    .responsive(true)
                    .hover()
                    .mode(InteractionMode.INDEX)
                    .intersect(true)
                    .animationDuration(400)
                    .and()
                    .title()
                    .display(true)
                    .text("Chart.js Bar Chart - Multi Axis")
                    .and()
                    .scales()
                    .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-2"))
                    .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-3"))
                    .and()
                    .done();
        }

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;

            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private Component getVerticalBar5(){
        BarChartConfig barConfig = new BarChartConfig();
        barConfig.
                data()
                .labels("January", "February", "March", "April", "May", "June", "July")
                .
        addDataset(
                new BarDataset().backgroundColor(
                        ColorUtils.randomColor(1.0)).label("INFO").yAxisID("y-axis-1"))
                .addDataset(
                        new BarDataset().backgroundColor(
                                ColorUtils.randomColor(2.0)).label("INFO").yAxisID("y-axis-2"))
                .addDataset(
                        new BarDataset().backgroundColor(
                                ColorUtils.randomColor(3.0)).label("INFO").yAxisID("y-axis-3"))
                .and();
        barConfig.
                options()
                .responsive(true)
                .hover()
                .mode(InteractionMode.INDEX)
                .intersect(true)
                .animationDuration(400)
                .and()
                .title()
                .display(true)
                .text("Chart.js Bar Chart - Multi Axis")
                .and()
                .scales()
                .add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
                .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-2"))
                .add(Axis.Y, new LinearScale().display(false).position(Position.LEFT).id("y-axis-3"))
                .and()
                .done();

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;
            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(i,7.0);
            }
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);
        chart.setSizeFull();
        return chart;
    }

    private void setValues(){
        try {
            if(mainHL!=null){
                if(mainHL.getComponentCount()>0){
                    mainHL.removeAllComponents();
                }
                mainHL.addComponent(setStudents());
                mainHL.addComponent(setLaters());
                mainHL.addComponent(setEmployees());
                mainHL.addComponent(setNoCards());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    private Panel setNoCards() {
        Object [] info = getCardInfo();
        long all = (Long) info[0];
        long withcards =(Long) info[1];
        long nocards = all - withcards;

        Double dall = new Double(all);
        Double dwithcards = new Double(withcards);

        Panel noCardButton = new Panel("<html><b>"+ CommonUtils.getUILocaleUtil().getCaption("noCardButton") +"</b><br>" +
                Math.round(dwithcards * 100 / dall)+"%<br>" +
                "("+nocards+" из "+all+")</html>");
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
                "WHERE date_trunc('day', arriv.created) = date_trunc('day', TIMESTAMP '"+CommonUtils.getFormattedDate(dateField.getValue())+"') " +
                "      AND arriv.created = (SELECT max(max_arriv.created) " +
                "                           FROM user_arrival max_arriv " +
                " WHERE max_arriv.user_id = arriv.user_id) " +
        "      AND come_in = TRUE";
        Long inEmployees = (Long) SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
        long inPercent = inEmployees * 100 / allEmployees;

        Panel employeesPanel = new Panel("<b>"+ CommonUtils.getUILocaleUtil().getCaption("employeesPanel")+" - " + allEmployees + "</b><br>" +
                CommonUtils.getUILocaleUtil().getCaption("attendanceOf")+" - " + inEmployees + " (" + inPercent + "%)<br>" +
                CommonUtils.getUILocaleUtil().getCaption("absentOf")+" - " + (allEmployees - inEmployees) + " (" + (100 - inPercent) + "%)" +
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

    private Object[] getCardInfo(){
        Map<Integer,Object> params = new HashMap<>();
        String sql = "select\n" +
                "  (select count(id) FROM users where deleted = FALSE ) as all,\n" +
                "  (select count(u.id) from users u INNER JOIN card c2 ON u.card_id = c2.id where u.deleted = FALSE ) as havecards;";

        try{
            Object o = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql , params);

            return (Object[])((Vector) o).get(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Panel setLaters() {
        Panel latersPanel = new Panel("<html><b>"+ CommonUtils.getUILocaleUtil().getCaption("latersPanel") +"</b><br>" +
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
                "WHERE date_trunc('day', arriv.created) = date_trunc('day',  TIMESTAMP '"+CommonUtils.getFormattedDate(dateField.getValue())+"') " +
                "      AND arriv.created = (SELECT max(max_arriv.created) " +
                "                           FROM user_arrival max_arriv " +
                "                           WHERE max_arriv.user_id = arriv.user_id) " +
                "      AND come_in = TRUE";
        Long inStudents = (Long) SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
        long inPercent = inStudents * 100 / allStudents;

        Panel studentsPanel = new Panel("<b>"+ CommonUtils.getUILocaleUtil().getCaption("studentsPanel") +" - " + allStudents + "</b><br>" +
                ""+ CommonUtils.getUILocaleUtil().getCaption("attendanceOf")+" - " + inStudents + " (" + inPercent + "%)<br>" +
                ""+ CommonUtils.getUILocaleUtil().getCaption("absentOf")+" - " + (allStudents - inStudents) + " (" + (100 - inPercent) + "%)" +
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
}