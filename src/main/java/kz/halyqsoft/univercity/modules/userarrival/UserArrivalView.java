package kz.halyqsoft.univercity.modules.userarrival;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.HashMap;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class UserArrivalView extends AbstractTaskView {

    private HorizontalSplitPanel mainHSP;
    private HorizontalLayout mainHL;

    private USER_TYPE userType;

    public UserArrivalView(AbstractTask task) throws Exception {
        super(task);
        userType = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(USER_TYPE.class, ID.valueOf(UserType.STUDENT_INDEX));
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(20);

        mainHL = new HorizontalLayout();
        mainHL.setSpacing(true);
        mainHL.setSizeFull();

        final TreeTable menuTT = new TreeTable();

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        String main = "Главная";
        hierarchicalContainer.addItem(main);
        hierarchicalContainer.addItem("Студенты");
        hierarchicalContainer.addItem("Сотрудники");
        hierarchicalContainer.addItem("Опаздавшие");
        hierarchicalContainer.addItem("Отсутсвующие");
        hierarchicalContainer.addItem("Годовая");

        menuTT.setContainerDataSource(hierarchicalContainer);
        menuTT.setSizeFull();
        menuTT.addStyleName("schedule");
        menuTT.setSelectable(true);
        menuTT.setMultiSelect(false);
        menuTT.setNullSelectionAllowed(false);
        menuTT.setImmediate(true);
        menuTT.setColumnReorderingAllowed(false);
        menuTT.setPageLength(20);
        MenuColumn menuColumn = new MenuColumn();
        menuTT.addGeneratedColumn("user arrival", menuColumn);
        menuTT.setColumnHeader("user arrival", "Посещаемость");//TODO kk,ru
        menuTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                        mainHSP.removeComponent(mainHL);
                        mainHL.removeAllComponents();
                        if (main.equals(event.getProperty().getValue().toString())) {
                            setStudents();
                            setLaters();
                            setEmployees();
                            setNoCards();
                        } else {
                            mainHL.addComponent(new Button("asd"));
                        }
                        mainHSP.addComponent(mainHL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void setNoCards() {
                Panel noCardButton = new Panel("<html><b>Без карточки</b><br>" +
                        "0%<br>" +
                        "(0 из 3209)</html>");
                noCardButton.setIcon(new ThemeResource("img/card.png"));
                noCardButton.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("no card");
                    }
                });
                mainHL.addComponent(noCardButton);
                mainHL.setComponentAlignment(noCardButton, Alignment.MIDDLE_CENTER);
            }

            private void setEmployees() throws Exception {
                QueryModel<V_EMPLOYEE> employeeQM = new QueryModel<>(V_EMPLOYEE.class);
                employeeQM.addSelect("code", EAggregate.COUNT);
                employeeQM.addWhere("deleted", Boolean.FALSE);
                Long allEmployees = (Long) SessionFacadeFactory.
                        getSessionFacade(CommonEntityFacadeBean.class).lookupItems(employeeQM);

                String sql = "SELECT count(1) " +
                        "FROM user_arrival arriv INNER JOIN v_employee empl ON empl.id = arriv.user_id " +
                        "WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                        "      AND arriv.created = (SELECT max(max_arriv.created) " +
                        "                           FROM user_arrival max_arriv " +
                        "                           WHERE max_arriv.user_id = arriv.user_id) " +
                        "      AND come_in = TRUE";
                Long inEmployees = (Long) SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
                long inPercent = inEmployees * 100 / allEmployees;

                Panel employeesPanel = new Panel("<b>Сотрудники - " + allEmployees + "</b><br>" +
                        "Присутс. - " + inEmployees + " (" + inPercent + "%)<br>" +
                        "Отсутс. - " + (allEmployees - inEmployees) + " (" + (100 - inPercent) + "%)" +
                        "<br>");
                employeesPanel.setIcon(new ThemeResource("img/employee.png"));
                employeesPanel.setHeight(155, Unit.PIXELS);
                employeesPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("employees");
                    }
                });
                mainHL.addComponent(employeesPanel);
                mainHL.setComponentAlignment(employeesPanel, Alignment.MIDDLE_CENTER);
            }

            private void setLaters() {
                Panel latersPanel = new Panel("<html><b>Опоздавшие</b><br>" +
                        "9.8%<br>" +
                        "(313 из 3209)</html>");
                latersPanel.setIcon(new ThemeResource("img/clock.png"));
                latersPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("laters");
                    }
                });
                mainHL.addComponent(latersPanel);
                mainHL.setComponentAlignment(latersPanel, Alignment.MIDDLE_CENTER);
            }

            private void setStudents() throws Exception {
                QueryModel<V_STUDENT> studentQM = new QueryModel<>(V_STUDENT.class);
                studentQM.addSelect("userCode", EAggregate.COUNT);
                studentQM.addWhere("deleted", Boolean.FALSE);
                Long allStudents = (Long) SessionFacadeFactory.
                        getSessionFacade(CommonEntityFacadeBean.class).lookupItems(studentQM);

                String sql = "SELECT count(1) " +
                        "FROM user_arrival arriv INNER JOIN v_student stu ON stu.id = arriv.user_id " +
                        "WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                        "      AND arriv.created = (SELECT max(max_arriv.created) " +
                        "                           FROM user_arrival max_arriv " +
                        "                           WHERE max_arriv.user_id = arriv.user_id) " +
                        "      AND come_in = TRUE";
                Long inStudents = (Long) SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
                long inPercent = inStudents * 100 / allStudents;

                Panel studentsPanel = new Panel("<b>Учащиеся - " + allStudents + "</b><br>" +
                        "Присутс. - " + inStudents + " (" + inPercent + "%)<br>" +
                        "Отсутс. - " + (allStudents - inStudents) + " (" + (100 - inPercent) + "%)" +
                        "<br>");
                studentsPanel.setIcon(new ThemeResource("img/student.png"));
                studentsPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("Students");
                    }
                });
                mainHL.addComponent(studentsPanel);
                mainHL.setComponentAlignment(studentsPanel, Alignment.MIDDLE_CENTER);
            }
        });

        mainHSP.addComponent(menuTT);

        getContent().addComponent(mainHSP);
    }
}
