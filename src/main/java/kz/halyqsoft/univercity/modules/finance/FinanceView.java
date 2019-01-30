package kz.halyqsoft.univercity.modules.finance;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_PAYMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentPayment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT_DEBTS;
import kz.halyqsoft.univercity.filter.FPaymentFilter;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.PaymentFilterPanel;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.workflow.MyItem;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.FacultyChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.*;

public class FinanceView extends AbstractTaskView implements FilterPanelListener, EntityListener {
    private VerticalLayout mainVL;

    private StudentFilterPanel studentDebtPanel;
    private GridWidget studentDebtGW;
    private DBGridModel studentDebtGM;

    private PaymentFilterPanel paymentFilterPanel;
    private GridWidget studentPaymentGW;
    private DBGridModel studentPaymentGM;

    private HorizontalSplitPanel mainHS;
    private static String FIRST_ROW = getUILocaleUtil().getEntityLabel(V_STUDENT_DEBTS.class);
    private static String SECOND_ROW = getUILocaleUtil().getEntityLabel(VStudentPayment.class);
    List<Entity> practiceInformations;
    private StudentInfo studentInfo;

    private Button  backButton;
    private HorizontalLayout topHL;
    private HorizontalLayout buttonPanel;

    public FinanceView(AbstractTask task) throws Exception {
        super(task);

        mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        mainHS = new HorizontalSplitPanel();
        mainHS.setImmediate(true);
        mainHS.setSizeFull();
        mainHS.setSplitPosition(15);

        buttonPanel = CommonUtils.createButtonPanel();

        topHL = new HorizontalLayout();
        topHL.setWidth(100, Sizeable.Unit.PERCENTAGE);
        topHL.setImmediate(true);

    }

    @Override
    public void initView(boolean b) throws Exception {

        HierarchicalContainer optionHC = new HierarchicalContainer();
        ArrayList<MyItem> items = new ArrayList<>();
        items.add(new MyItem(optionHC.addItem(FIRST_ROW), FIRST_ROW, null));
        items.add(new MyItem(optionHC.addItem(SECOND_ROW), SECOND_ROW, null));

        TreeTable treeTable = new TreeTable();
        treeTable.setContainerDataSource(optionHC);
        treeTable.setColumnReorderingAllowed(false);
        treeTable.setMultiSelect(false);
        treeTable.setNullSelectionAllowed(false);
        treeTable.setSizeFull();
        treeTable.setImmediate(true);
        treeTable.setSelectable(true);

        treeTable.setChildrenAllowed(FIRST_ROW, false);
        treeTable.setChildrenAllowed(SECOND_ROW, false);
        MenuColumn firstColumn = new MenuColumn();
        treeTable.addGeneratedColumn("first", firstColumn);
        treeTable.setColumnHeader("first", getUILocaleUtil().getCaption("menu"));
        treeTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mainVL.removeAllComponents();

                if (valueChangeEvent.getProperty().getValue().toString().equals(FIRST_ROW)) {
                    mainVL.addComponent(studentDebtPanel);
                    mainVL.addComponent(studentDebtGW);
                    doFilter(studentDebtPanel.getFilterBean());
                } else if (valueChangeEvent.getProperty().getValue().toString().equals(SECOND_ROW)) {
                    mainVL.addComponent(paymentFilterPanel);
                    mainVL.addComponent(topHL);
                    mainVL.addComponent(studentPaymentGW);
                    doFilter(paymentFilterPanel.getFilterBean());
                }
            }
        });
        initGridWidget();
        initFilter();

        mainHS.setFirstComponent(treeTable);
        mainHS.setSecondComponent(mainVL);
        getContent().addComponent(mainHS);
    }

    private void initGridWidget() {

        studentDebtGW = new GridWidget(V_STUDENT_DEBTS.class);
        studentDebtGW.setCaption(getUILocaleUtil().getCaption("studentDebtGW"));
        studentDebtGW.addEntityListener(this);
        studentDebtGW.showToolbar(false);

        studentDebtGM = (DBGridModel) studentDebtGW.getWidgetModel();
        studentDebtGM.setHeightByRows(5);
        studentDebtGM.setRefreshType(ERefreshType.MANUAL);
        studentDebtGM.setMultiSelect(false);
        studentDebtGM.setRowNumberVisible(true);
        studentDebtGM.setRowNumberWidth(50);

        backButton = new Button(CommonUtils.getUILocaleUtil().getCaption("backButton"));
        backButton.setImmediate(true);
        backButton.setVisible(false);
        backButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mainVL.removeComponent(studentInfo.getMainVL());
                mainVL.addComponent(studentPaymentGW);
                backButton.setVisible(false);
            }
        });

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        studentPaymentGW = new GridWidget(VStudentPayment.class);
        studentPaymentGW.setCaption(getUILocaleUtil().getCaption("studentPaymentGW"));
        studentPaymentGW.addEntityListener(this);
        studentPaymentGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        DBGridModel studentPaymentGM = (DBGridModel) studentPaymentGW.getWidgetModel();
        studentPaymentGM.setHeightByRows(5);
        studentPaymentGM.setCrudEntityClass(STUDENT_PAYMENT.class);

        studentPaymentGM.setRefreshType(ERefreshType.MANUAL);
        studentPaymentGM.setMultiSelect(false);
        studentPaymentGM.setRowNumberVisible(true);
        studentPaymentGM.setRowNumberWidth(50);

    }

    private void initFilter() throws Exception {

        studentDebtPanel = new StudentFilterPanel(new FStudentFilter());
        studentDebtPanel.addFilterPanelListener(this);
        studentDebtPanel.setImmediate(true);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentDebtPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentDebtPanel.addFilterComponent("firstname", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentDebtPanel.addFilterComponent("lastname", tf);

        ComboBox cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<CARD> cardQM = new QueryModel<>(CARD.class);
        FromItem userFI = cardQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "card");
        cardQM.addWhere(userFI, "typeIndex", ECriteria.EQUAL, 2);
        BeanItemContainer<CARD> cardBIC = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQM));
        cb.setContainerDataSource(cardBIC);
        studentDebtPanel.addFilterComponent("card", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        List<ID> idList = new ArrayList<>(4);
        idList.add(ID.valueOf(1));
        idList.add(ID.valueOf(2));
        idList.add(ID.valueOf(3));
        idList.add(ID.valueOf(5));
        QueryModel<STUDENT_STATUS> ssQM = new QueryModel<>(STUDENT_STATUS.class);
        ssQM.addWhereIn("id", idList);
        ssQM.addOrder("id");
        BeanItemContainer<STUDENT_STATUS> ssBIC = new BeanItemContainer<>(STUDENT_STATUS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ssQM));
        cb.setContainerDataSource(ssBIC);
        studentDebtPanel.addFilterComponent("studentStatus", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        cb.setPageLength(0);
        cb.setWidth(250, Unit.PIXELS);
        QueryModel<DEPARTMENT> facultyQM = new QueryModel<>(DEPARTMENT.class);
        facultyQM.addWhereNull("parent");
        facultyQM.addWhereAnd("deleted", Boolean.FALSE);
        facultyQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(facultyQM));
        cb.setContainerDataSource(facultyBIC);
        studentDebtPanel.addFilterComponent("faculty", cb);

        ComboBox specialtyCB = new ComboBox();
        specialtyCB.setNullSelectionAllowed(true);
        specialtyCB.setTextInputAllowed(true);
        specialtyCB.setFilteringMode(FilteringMode.CONTAINS);
        specialtyCB.setPageLength(0);
        specialtyCB.setWidth(250, Unit.PIXELS);
        cb.addValueChangeListener(new FacultyChangeListener(specialtyCB));
        studentDebtPanel.addFilterComponent("speciality", specialtyCB);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(70, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
        studyYearQM.addWhere("studyYear", ECriteria.LESS_EQUAL, 7);
        studyYearQM.addOrder("studyYear");
        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        cb.setContainerDataSource(studyYearBIC);
        studentDebtPanel.addFilterComponent("studyYear", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        studentDebtPanel.addFilterComponent("educationType", cb);
        studentDebtPanel.setVisible(true);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<GROUPS> groupsQueryModel = new QueryModel<>(GROUPS.class);
        BeanItemContainer<GROUPS> groupsBeanItemContainer = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQueryModel));
        cb.setContainerDataSource(groupsBeanItemContainer);
        studentDebtPanel.addFilterComponent("group", cb);


        paymentFilterPanel = new PaymentFilterPanel(new FPaymentFilter());
        paymentFilterPanel.addFilterPanelListener(this);
        paymentFilterPanel.setImmediate(true);

        TextField textField = new TextField();
        textField.setNullRepresentation("");
        textField.setNullSettingAllowed(true);
        paymentFilterPanel.addFilterComponent("code", textField);

        textField = new TextField();
        textField.setNullRepresentation("");
        textField.setNullSettingAllowed(true);
        paymentFilterPanel.addFilterComponent("firstname", textField);

        textField = new TextField();
        textField.setNullRepresentation("");
        textField.setNullSettingAllowed(true);
        paymentFilterPanel.addFilterComponent("lastname", textField);

        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(true);
        comboBox.setTextInputAllowed(true);
        comboBox.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<CARD> cardQueryModel = new QueryModel<>(CARD.class);
        FromItem userItem = cardQueryModel.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "card");
        cardQueryModel.addWhere(userItem, "typeIndex", ECriteria.EQUAL, 2);
        BeanItemContainer<CARD> card = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQueryModel));
        comboBox.setContainerDataSource(card);
        paymentFilterPanel.addFilterComponent("card", comboBox);

        comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(true);
        comboBox.setTextInputAllowed(false);
        comboBox.setFilteringMode(FilteringMode.OFF);
        List<ID> list = new ArrayList<>(4);
        list.add(ID.valueOf(1));
        list.add(ID.valueOf(2));
        list.add(ID.valueOf(3));
        list.add(ID.valueOf(5));
        QueryModel<STUDENT_STATUS> studentStatusQM = new QueryModel<>(STUDENT_STATUS.class);
        studentStatusQM.addWhereIn("id", list);
        studentStatusQM.addOrder("id");
        BeanItemContainer<STUDENT_STATUS> studentStatusBIC = new BeanItemContainer<>(STUDENT_STATUS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentStatusQM));
        comboBox.setContainerDataSource(studentStatusBIC);
        paymentFilterPanel.addFilterComponent("studentStatus", comboBox);

        comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(true);
        comboBox.setTextInputAllowed(true);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        comboBox.setPageLength(0);
        comboBox.setWidth(250, Unit.PIXELS);
        QueryModel<DEPARTMENT> departmentQueryModel = new QueryModel<>(DEPARTMENT.class);
        departmentQueryModel.addWhereNull("parent");
        departmentQueryModel.addWhereAnd("deleted", Boolean.FALSE);
        departmentQueryModel.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> departmentBeanItemContainer = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(departmentQueryModel));
        comboBox.setContainerDataSource(departmentBeanItemContainer);
        paymentFilterPanel.addFilterComponent("faculty", comboBox);

        ComboBox specialty = new ComboBox();
        specialty.setNullSelectionAllowed(true);
        specialty.setTextInputAllowed(true);
        specialty.setFilteringMode(FilteringMode.CONTAINS);
        specialty.setPageLength(0);
        specialty.setWidth(250, Unit.PIXELS);
        comboBox.addValueChangeListener(new FacultyChangeListener(specialty));
        paymentFilterPanel.addFilterComponent("speciality", specialty);

        comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(true);
        comboBox.setTextInputAllowed(false);
        comboBox.setFilteringMode(FilteringMode.OFF);
        comboBox.setPageLength(0);
        comboBox.setWidth(70, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQueryModel = new QueryModel<>(STUDY_YEAR.class);
        studyYearQueryModel.addWhere("studyYear", ECriteria.LESS_EQUAL, 7);
        studyYearQueryModel.addOrder("studyYear");
        BeanItemContainer<STUDY_YEAR> studyYearBeanItemContainer = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQueryModel));
        comboBox.setContainerDataSource(studyYearBeanItemContainer);
        paymentFilterPanel.addFilterComponent("studyYear", comboBox);

        comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(true);
        comboBox.setTextInputAllowed(false);
        comboBox.setFilteringMode(FilteringMode.STARTSWITH);
        comboBox.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> studentEducationTypeQueryModel = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> studentEducationTypeBeanItemContainer = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationTypeQueryModel));
        comboBox.setContainerDataSource(studentEducationTypeBeanItemContainer);
        paymentFilterPanel.addFilterComponent("educationType", comboBox);

        comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(true);
        comboBox.setTextInputAllowed(false);
        comboBox.setFilteringMode(FilteringMode.STARTSWITH);
        comboBox.setPageLength(0);
        QueryModel<GROUPS> queryModel = new QueryModel<>(GROUPS.class);
        queryModel.addOrder("name");
        BeanItemContainer<GROUPS> container = new BeanItemContainer<>(GROUPS.class,
               SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(queryModel));
        comboBox.setContainerDataSource(container);
        paymentFilterPanel.addFilterComponent("group", comboBox);

        DateField comeInDF = new DateField();
        comeInDF.setHeight("23");
        comeInDF.setWidth("150");
        paymentFilterPanel.addFilterComponent("date", comeInDF);
    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        if (abstractFilterBean instanceof FStudentFilter) {
            FStudentFilter sf = (FStudentFilter) abstractFilterBean;
            Map<Integer, Object> params = new HashMap<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();

            if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
                sb.append(" and x.user_code ilike '");
                sb.append(sf.getCode().trim());
                sb.append("%'");
            }
            if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
                sb.append(" and x.FIRST_NAME ilike '");
                sb.append(sf.getFirstname().trim());
                sb.append("%'");
            }
            if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
                sb.append(" and x.LAST_NAME ilike '");
                sb.append(sf.getLastname().trim());
                sb.append("%'");
            }
            if (sf.getCard() != null) {
                params.put(i, sf.getCard().getId().getId());
                sb.append(" and x.card_id = ?");
                sb.append(i++);
            }
            if (sf.getStudentStatus() != null) {
                params.put(i, sf.getStudentStatus().getId().getId());
                sb.append(" and x.student_status_id = ?");
                sb.append(i++);
            }
            if (sf.getFaculty() != null) {
                params.put(i, sf.getFaculty().getId().getId());
                sb.append(" and x.faculty_id = ?");
                sb.append(i++);
            }
            if (sf.getSpeciality() != null) {
                params.put(i, sf.getSpeciality().getId().getId());
                sb.append(" and x.speciality_id = ?");
                sb.append(i++);
            }
            if (sf.getStudyYear() != null) {
                params.put(i, sf.getStudyYear().getId().getId());
                sb.append(" and x.study_year_id = ?");
                sb.append(i++);
            }
            if (sf.getEducationType() != null) {
                params.put(i, sf.getEducationType().getId().getId());
                sb.append(" and x.education_type_id = ?");
                sb.append(i++);
            }
            if (sf.getGroup() != null) {
                params.put(i, sf.getGroup().getId().getId());
                sb.append(" and x.groups_id = ?");
                sb.append(i);
            }

            try {
                filterFinDebt(sb, params);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (abstractFilterBean instanceof FPaymentFilter) {
            FPaymentFilter fPaymentFilter = (FPaymentFilter) abstractFilterBean;
            Map<Integer, Object> params = new HashMap<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();

            if (fPaymentFilter.getCode() != null && fPaymentFilter.getCode().trim().length() >= 2) {
                sb.append(" and x3.user_code ilike '");
                sb.append(fPaymentFilter.getCode().trim());
                sb.append("%'");
            }
            if (fPaymentFilter.getFirstname() != null && fPaymentFilter.getFirstname().trim().length() >= 3) {
                sb.append(" and x3.FIRST_NAME ilike '");
                sb.append(fPaymentFilter.getFirstname().trim());
                sb.append("%'");
            }
            if (fPaymentFilter.getLastname() != null && fPaymentFilter.getLastname().trim().length() >= 3) {
                sb.append(" and x3.LAST_NAME ilike '");
                sb.append(fPaymentFilter.getLastname().trim());
                sb.append("%'");
            }

            if (fPaymentFilter.getCard() != null) {
                params.put(i, fPaymentFilter.getCard().getId().getId());
                sb.append(" and x3.card_id = ?");
                sb.append(i++);
            }
            if (fPaymentFilter.getStudentStatus() != null) {
                params.put(i, fPaymentFilter.getStudentStatus().getId().getId());
                sb.append(" and x3.student_status_id = ?");
                sb.append(i++);
            }
            if (fPaymentFilter.getFaculty() != null) {
                params.put(i, fPaymentFilter.getFaculty().getId().getId());
                sb.append(" and x3.faculty_id = ?");
                sb.append(i++);
            }
            if (fPaymentFilter.getSpeciality() != null) {
                params.put(i, fPaymentFilter.getSpeciality().getId().getId());
                sb.append(" and x3.speciality_id = ?");
                sb.append(i++);
            }
            if (fPaymentFilter.getStudyYear() != null) {
                params.put(i, fPaymentFilter.getStudyYear().getId().getId());
                sb.append(" and x3.study_year_id = ?");
                sb.append(i++);
            }
            if (fPaymentFilter.getEducationType() != null) {
                params.put(i, fPaymentFilter.getEducationType().getId().getId());
                sb.append(" and x3.education_type_id = ?");
                sb.append(i++);
            }
            if (fPaymentFilter.getGroup() != null) {
                params.put(i, fPaymentFilter.getGroup().getId().getId());
                sb.append(" and x3.groups_id = ?");
                sb.append(i);
            }
            if (fPaymentFilter.getDate() != null) {
                params.put(i, fPaymentFilter.getDate());
                sb.append(" and date_trunc ('day' ,  x2.created ) = date_trunc('day' , TIMESTAMP  '" + CommonUtils.getFormattedDate(fPaymentFilter.getDate()) + "') ");
                //sb.append(i);

            }

            try {
                filterPayment(sb, params);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void filterFinDebt(StringBuilder sb, Map<Integer, Object> params) throws Exception {
        QueryModel<V_STUDENT_DEBTS> studentDebtsQM = new QueryModel<>(V_STUDENT_DEBTS.class);
        String sql = "SELECT * FROM V_STUDENT_DEBTS sd " +
                "INNER JOIN v_student x on sd.user_code=x.user_code" +
                " WHERE x.deleted=false " + sb.toString();

        String sqlStudent = "SELECT * FROM V_STUDENT_DEBTS sd " +
                "INNER JOIN v_student x on sd.user_code=x.user_code" +
                " WHERE x.deleted=false " + sb.toString() +
                "  and x.id = " + CommonUtils.getCurrentUser().getId();

        List<V_STUDENT_DEBTS> scheduleDetails = null;
        if(CommonUtils.getCurrentUser().getTypeIndex()==2) {
            scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sqlStudent, params, V_STUDENT_DEBTS.class);

        }else{
            scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql, params, V_STUDENT_DEBTS.class);

        }
        refresh(scheduleDetails);
    }


    private void filterPayment(StringBuilder sb, Map<Integer, Object> params) {
        List<VStudentPayment> list = new ArrayList<>();

        String sql  = "select  " +
                "   x2.id," +
                "  trim(x3.LAST_NAME || ' ' || x3.FIRST_NAME || ' ' || coalesce(x3.MIDDLE_NAME, '')) fio, " +
                "  x3.user_code," +
                "  (sum(payment_sum)/(vsd.debt_sum+sum(payment_sum)))*100," +
                "  (x2.created::date)::text," +
                "  data.begin_date,\n" +
                "  data.end_date, " +
                "  (vsd.debt_sum - x2.payment_sum)::text" +
                " from student_payment x2 " +
                "  inner join v_student x3 on x3.id = x2.student_id " +
                "  inner join v_student_debts vsd on x3.user_code = vsd.user_code " +
                "  inner join semester_data data on data.year_id = x3.entrance_year_id\n" +
                " where x3.deleted = false " + sb.toString() +
                " group by  x2.id , x3.LAST_NAME, x3.FIRST_NAME, x3.MIDDLE_NAME ,x3.user_code, vsd.debt_sum,x2.created,data.begin_date,\n" +
                "        data.end_date, vsd.debt_sum ,x2.payment_sum;";

        String sqlStudent  = "select\n" +
                "   x2.id,\n" +
                "  trim(x3.LAST_NAME || ' ' || x3.FIRST_NAME || ' ' || coalesce(x3.MIDDLE_NAME, '')) fio,\n" +
                "  x3.user_code,\n" +
                "  (sum(payment_sum)/(vsd.debt_sum+sum(payment_sum)))*100  paymentSum," +
                "  (x2.created::date)::text," +
                "   data.begin_date,\n" +
                "   data.end_date,\n" +
                "  (vsd.debt_sum - x2.payment_sum)::text" +
                "from student_payment x2\n" +
                "  inner join v_student x3 on x3.id = x2.student_id\n" +
                "  inner join v_student_debts vsd on x3.user_code = vsd.user_code\n" +
                "  inner join semester_data data on data.year_id = x3.entrance_year_id\n" +
                "where x3.deleted = false " + sb.toString() + " and student_id = " + CommonUtils.getCurrentUser().getId() +
                " group by  x2.id , x3.LAST_NAME,x3.FIRST_NAME,x3.MIDDLE_NAME ,x3.user_code, vsd.debt_sum,x2.created,data.begin_date,\n" +
                "        data.end_date, vsd.debt_sum ,x2.payment_sum;";

        if(CommonUtils.getCurrentUser().getTypeIndex()==2) {
            fillList(list, sqlStudent , params);
        }else{
            fillList(list, sql, params);
        }
        refresh(list);
    }


    static void fillList(List<VStudentPayment> list, String sql, Map<Integer, Object> params) {
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql,
                    params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudentPayment sp = new VStudentPayment();
                    sp.setId(ID.valueOf((long) oo[0]));
                    sp.setFio((String) oo[1]);
                    sp.setCode((String) oo[2]);
                    sp.setPaymentSum(((BigDecimal) oo[3]).doubleValue());
                    sp.setTime((String)oo[4]);
                    sp.setBeginDate((Date) oo[5]);
                    sp.setEndDate((Date) oo[6]);
                    sp.setDebt((String) oo[7]);
                    list.add(sp);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load students' payment list", ex);
        }
    }

    @Override
    public void clearFilter() {
        if (paymentFilterPanel.isAttached()) {
            doFilter(paymentFilterPanel.getFilterBean());
        } else if (studentDebtPanel.isAttached()) {
            doFilter(studentDebtPanel.getFilterBean());
        }
    }

    private void refresh(List list) {
        if (studentDebtPanel.isAttached()) {
            ((DBGridModel) studentDebtGW.getWidgetModel()).setEntities(list);
            try {
                studentDebtGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh students' debt grid", ex);
            }
        } else if (paymentFilterPanel.isAttached()) {
            ((DBGridModel) studentPaymentGW.getWidgetModel()).setEntities(list);
            try {
                studentPaymentGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh students' payment grid", ex);
            }
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        STUDENT_PAYMENT pi = (STUDENT_PAYMENT) e;
            if (isNew) {
                try {
                    pi.setCreated(new Date());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pi);
                } catch (NoResultException nre) {
                    System.out.println("GOOD");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pi);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge payment", ex);
                }
            }
        try {
            refresh(source);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return false;
    }


    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(studentPaymentGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED) {
                if (studentPaymentGW != null) {
                    mainVL.removeComponent(studentPaymentGW);
                    studentInfo = new StudentInfo((VStudentPayment) studentPaymentGW.getSelectedEntity(),this);
                    mainVL.addComponent(studentInfo.getMainVL());
                    backButton.setVisible(true);
                }
            }
        }
        super.handleEntityEvent(entityEvent);

    }


    @Override
    public void onRefresh(Object source, List<Entity> entities) {
        super.onRefresh(source, entities);
    }


    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        List<STUDENT_PAYMENT> delList = new ArrayList<>();
        try {
            for (Entity entity : entities) {
                delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(STUDENT_PAYMENT.class, entity.getId()));
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to delete student payment", ex);
        }
        try {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
            studentPaymentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.LOG.error("Unable to delete student payment: ", ex);
            Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
        }
        return false;
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {

        return true;
    }

    public void refresh(Object source) {
        if (source.equals(studentDebtGW)) {
            doFilter(studentDebtPanel.getFilterBean());
        } else if (source.equals(studentPaymentGW)) {
            doFilter(paymentFilterPanel.getFilterBean());
        }
    }

    public GridWidget getStudentPaymentGW() {
        return studentPaymentGW;
    }

    public void setStudentPaymentGW(GridWidget studentPaymentGW) {
        this.studentPaymentGW = studentPaymentGW;
    }
}
