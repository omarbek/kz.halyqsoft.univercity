package kz.halyqsoft.univercity.modules.finance;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_FIN_DEBT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_PAYMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentFinDebt;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentPayment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT_DEBTS;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
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
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Omarbek
 * @created on 30.07.2018
 */
public class FinanceView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private final StudentFilterPanel filterPanel;
    private GridWidget studentDebtGW;
    private GridWidget studentPaymentGW;

    public FinanceView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new StudentFilterPanel(new FStudentFilter());
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        filterPanel.addFilterPanelListener(this);
        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("firstname", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("lastname", tf);

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
        filterPanel.addFilterComponent("card", cb);

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
        filterPanel.addFilterComponent("studentStatus", cb);

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
        filterPanel.addFilterComponent("faculty", cb);

        ComboBox specialtyCB = new ComboBox();
        specialtyCB.setNullSelectionAllowed(true);
        specialtyCB.setTextInputAllowed(true);
        specialtyCB.setFilteringMode(FilteringMode.CONTAINS);
        specialtyCB.setPageLength(0);
        specialtyCB.setWidth(250, Unit.PIXELS);
        cb.addValueChangeListener(new FacultyChangeListener(specialtyCB));
        filterPanel.addFilterComponent("speciality", specialtyCB);

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
        filterPanel.addFilterComponent("studyYear", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        filterPanel.addFilterComponent("educationType", cb);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        studentDebtGW = new GridWidget(V_STUDENT_DEBTS.class);
        studentDebtGW.addEntityListener(this);
        studentDebtGW.showToolbar(false);
        DBGridModel studentDebtGM = (DBGridModel) studentDebtGW.getWidgetModel();
        studentDebtGM.setHeightByRows(5);
        studentDebtGM.setRefreshType(ERefreshType.MANUAL);
        studentDebtGM.setMultiSelect(false);
        studentDebtGM.setRowNumberVisible(true);
        studentDebtGM.setRowNumberWidth(50);

        studentPaymentGW = new GridWidget(VStudentPayment.class);
        studentPaymentGW.addEntityListener(this);
        studentPaymentGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        DBGridModel studentPaymentGM = (DBGridModel) studentPaymentGW.getWidgetModel();
        studentPaymentGM.setHeightByRows(5);
        studentPaymentGM.setCrudEntityClass(STUDENT_PAYMENT.class);
        studentPaymentGM.setRefreshType(ERefreshType.MANUAL);
        studentPaymentGM.setMultiSelect(false);
        studentPaymentGM.setRowNumberVisible(true);
        studentPaymentGM.setRowNumberWidth(50);

        fillTables();

        getContent().addComponent(studentDebtGW);
        getContent().setComponentAlignment(studentDebtGW, Alignment.MIDDLE_CENTER);

        getContent().addComponent(studentPaymentGW);
        getContent().setComponentAlignment(studentPaymentGW, Alignment.MIDDLE_CENTER);
    }

    private void fillTables() {
        FStudentFilter ef = (FStudentFilter) filterPanel.getFilterBean();
        doFilter(ef);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED
                || ev.getAction() == EntityEvent.MERGED
                || ev.getAction() == EntityEvent.REMOVED) {
            fillTables();
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        STUDENT_PAYMENT studentPayment = (STUDENT_PAYMENT) e;
        studentPayment.setCreated(new Date());
        return true;
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FStudentFilter sf = (FStudentFilter) abstractFilterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<>();
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
            sb.append(i);
        }

        try {
            filterFinDebt(sb, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterPayment(sb, params);
    }

    private void filterPayment(StringBuilder sb, Map<Integer, Object> params) {
        List<VStudentPayment> list = new ArrayList<>();
        String sql = "select x2.id, trim(x.LAST_NAME||' '||x.FIRST_NAME||' '||coalesce(x.MIDDLE_NAME, '')) fio, " +
                "x.user_code, x2.created, x2.payment_sum paymentSum"
                + " from v_student x"
                + " inner join STUDENT_PAYMENT x2 on x2.student_id=x.id"
                + " where x.deleted=false "
                + sb.toString()
                + " order by x2.created desc";
        fillList(list, sql, params);

        refreshPayment(list);
    }

    private void refreshPayment(List<VStudentPayment> list) {
        ((DBGridModel) studentPaymentGW.getWidgetModel()).setEntities(list);
        try {
            studentPaymentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students' payment grid", ex);
        }
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
                    sp.setCreated((Date) oo[3]);
                    sp.setPaymentSum(((BigDecimal) oo[4]).doubleValue());
                    list.add(sp);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load students' payment list", ex);
        }
    }

    private void filterFinDebt(StringBuilder sb, Map<Integer, Object> params) throws Exception {
        QueryModel<V_STUDENT_DEBTS> studentDebtsQM = new QueryModel<>(V_STUDENT_DEBTS.class);
        String sql = "SELECT * FROM V_STUDENT_DEBTS sd " +
                "INNER JOIN v_student x on sd.user_code=x.user_code" +
                " WHERE x.deleted=false " + sb.toString();
        List<V_STUDENT_DEBTS> scheduleDetails = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql,params,V_STUDENT_DEBTS.class);
        refreshFinDebt(scheduleDetails);
    }

    private void refreshFinDebt(List<V_STUDENT_DEBTS> list) {
        ((DBGridModel) studentDebtGW.getWidgetModel()).setEntities(list);
        try {
            studentDebtGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students' debt grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        fillTables();
    }
}
