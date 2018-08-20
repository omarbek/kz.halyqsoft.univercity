package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
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
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.*;

public class MainSection {
    private VerticalLayout mainVL;
    private HorizontalLayout mainHL;
    private DateField dateField;

    private Long allUsers = 0L;
    private Long allStudents = 0L;
    private Long allEmployees = 0L;

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

        Component verticalBar = getVerticalBar();
        verticalBar.setSizeFull();

        mainVL.addComponent(dateField);
        mainVL.setComponentAlignment(dateField ,Alignment.TOP_RIGHT);
        setValues();

        mainVL.addComponent(mainHL);

        gridLayout.addComponent(verticalBar,0,0);

        mainVL.addComponent(gridLayout);

    }

    private Component getVerticalBar(){
        BarChartConfig barConfig = new BarChartConfig();
        barConfig.
                data()
                .labels("January", "February", "March", "April", "May", "June", "July")
                .addDataset(
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
                "                           WHERE max_arriv.user_id = arriv.user_id) " +
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
