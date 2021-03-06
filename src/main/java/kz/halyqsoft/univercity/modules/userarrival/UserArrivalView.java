package kz.halyqsoft.univercity.modules.userarrival;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.userarrival.subview.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class UserArrivalView extends AbstractTaskView implements EntityListener {

    private HorizontalSplitPanel mainHSP;
    private HorizontalLayout mainHL, secondHL;
    private HorizontalLayout buttonPanel;
    private GridWidget absentsGW, lateGW;
    private USER_TYPE userType;
    private HorizontalLayout buttonsHL;
    private VerticalLayout tableVL;
    private Button printBtn;

    private DateField date;
    private GridWidget employeeGW, departmentGW;
    private GridWidget employeeByDepartmentGW;
    private Long depID;

    private Long emplID;
    private ComboBox absentDayCB;
    private Button detalizationBtn;
    private GridWidget vEmployeeInfoGW;
    private DBGridModel vEmployeeInfoGM, vGroupGM;
    private Button backButton;

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

        buttonPanel = CommonUtils.createButtonPanel();

        final TreeTable menuTT = new TreeTable();

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        String main = getUILocaleUtil().getCaption("main");
        String studentAttendance = getUILocaleUtil().getCaption("studentAttendance");
        String employeeAttendance = getUILocaleUtil().getCaption("employeeAttendance");
        String lateEmployees = getUILocaleUtil().getCaption("late.employees");
        String absentTooLong = getUILocaleUtil().getCaption("absent.too.long");
        String absentToday = getUILocaleUtil().getCaption("absent.today");
        String yearlyAttendance = getUILocaleUtil().getCaption("yearlyAttendance");
        String latecomers = getUILocaleUtil().getCaption("latecomers");
        String absent = getUILocaleUtil().getCaption("absent");
        String manuallySign = getUILocaleUtil().getCaption("manuallySign");
        String manuallySignedReport = getUILocaleUtil().getCaption("manuallySign.report");
        String pdfAbsent = getUILocaleUtil().getCaption("pdfAttendance");

        hierarchicalContainer.addItem(main);
        hierarchicalContainer.setChildrenAllowed(main, false);
        hierarchicalContainer.addItem(studentAttendance);
        hierarchicalContainer.setChildrenAllowed(studentAttendance, false);
        hierarchicalContainer.addItem(employeeAttendance);
        hierarchicalContainer.setChildrenAllowed(employeeAttendance, false);
//        hierarchicalContainer.addItem(latecomers);
//        hierarchicalContainer.setChildrenAllowed(latecomers, false);
        hierarchicalContainer.addItem(lateEmployees);
        hierarchicalContainer.setChildrenAllowed(lateEmployees, false);
        hierarchicalContainer.addItem(absentTooLong);
        hierarchicalContainer.setChildrenAllowed(absentTooLong, false);
        hierarchicalContainer.addItem(absentToday);
        hierarchicalContainer.setChildrenAllowed(absentToday, false);
        hierarchicalContainer.addItem(yearlyAttendance);
        hierarchicalContainer.addItem(latecomers);
        hierarchicalContainer.setChildrenAllowed(latecomers, false);
        hierarchicalContainer.addItem(absent);
        hierarchicalContainer.setChildrenAllowed(absent, false);
        hierarchicalContainer.addItem(pdfAbsent);
        hierarchicalContainer.setChildrenAllowed(pdfAbsent, false);
        hierarchicalContainer.setParent(latecomers,yearlyAttendance);
        hierarchicalContainer.setParent(absent,yearlyAttendance);


        if (CommonUtils.getCurrentUser().getId().getId().longValue() == 2) {
            hierarchicalContainer.addItem(manuallySign);
            hierarchicalContainer.addItem(manuallySignedReport);
            hierarchicalContainer.setChildrenAllowed(manuallySignedReport, false);
            hierarchicalContainer.setParent(manuallySignedReport, manuallySign);
        }

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
        menuTT.setColumnHeader("user arrival", getUILocaleUtil().getCaption("attendanceHeader"));

        menuTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                        mainHSP.removeComponent(mainHL);
                        mainHL.removeAllComponents();
                        String menu = event.getProperty().getValue().toString();
                        if (main.equals(menu)) {
                            MainSection mainSection = new MainSection();
                            mainHL.addComponent(mainSection.getMainVL());
                        } else if (employeeAttendance.equals(menu)) {
                            EmployeeAttendance employeeAttendance = new EmployeeAttendance();
                            mainHL.addComponent(employeeAttendance.getMainVL());
                        } else if (studentAttendance.equalsIgnoreCase(menu)) {
                            FacultyAttendance facultyAttendance = new FacultyAttendance();
                            mainHL.addComponent(facultyAttendance.getMainVL());
                        } else if (absentTooLong.equalsIgnoreCase(menu)) {
                            AbsentAttendance absentAttendance = new AbsentAttendance(tableVL);
                            mainHL.addComponent(absentAttendance.getAbsentsInfo());
                        } else if (manuallySign.equalsIgnoreCase(menu)) {
                            SigningSection signingSection = new SigningSection();
                            mainHL.addComponent(signingSection.getMainVL());
                            setAbsentsInfo();
                        } else if (latecomers.equalsIgnoreCase(menu)) {
                            EmployeeYearLatecomers groupLatecomers = new EmployeeYearLatecomers();
                            mainHL.addComponent(groupLatecomers.getMainVL());
                            setAbsentsInfo();
                        } else if (manuallySignedReport.equalsIgnoreCase(menu)
                                && CommonUtils.isCurrentUserHasAdminPrivileges()) {
                            SigningReport signingReport = new SigningReport();
                            mainHL.addComponent(signingReport.getMainVL());
                        } else if (lateEmployees.equalsIgnoreCase(menu)) {
                            LateEmployeesAttendance lateEmployeesAttendance = new LateEmployeesAttendance();
                            mainHL.addComponent(lateEmployeesAttendance.getMainVL());
                        } else if (absentToday.equalsIgnoreCase(menu)) {
                            AbsentTodayAttendance absentTodayAttendance = new AbsentTodayAttendance();
                            mainHL.addComponent(absentTodayAttendance.getMainVL());
                        }  else if (latecomers.equalsIgnoreCase(menu)) {
                            EmployeeYearLatecomers groupLatecomers = new EmployeeYearLatecomers();
                            mainHL.addComponent(groupLatecomers.getMainVL());
                            setAbsentsInfo();
                        }else if (absent.equalsIgnoreCase(menu)) {
                            EmployeeAbsent employeeLatecomers = new EmployeeAbsent();
                            mainHL.addComponent(employeeLatecomers.getMainVL());
                            setAbsentsInfo();
                        }
                        else if (pdfAbsent.equalsIgnoreCase(menu)) {
                            PdfAttendence facultyAttendance = new PdfAttendence();
                            mainHL.addComponent(facultyAttendance.getMainVL());
                        }
                        mainHSP.addComponent(mainHL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });

        mainHSP.addComponent(menuTT);
        getContent().addComponent(mainHSP);
    }

    private void setAbsentsInfo() {
        absentDayCB = new ComboBox(getUILocaleUtil().getCaption("absentDayCB"));
        absentDayCB.setNullSelectionAllowed(true);
        absentDayCB.setTextInputAllowed(true);
        absentDayCB.setFilteringMode(FilteringMode.CONTAINS);
        absentDayCB.setWidth(300, Unit.PIXELS);
    }

}