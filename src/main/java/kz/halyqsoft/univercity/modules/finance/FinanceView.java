package kz.halyqsoft.univercity.modules.finance;

import com.vaadin.ui.Alignment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentFinDebt;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentPayment;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 30.07.2018
 */
public class FinanceView extends AbstractTaskView {

    private GridWidget studentDebtGW;
    private GridWidget studentPaymentGW;

    public FinanceView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        studentDebtGW = new GridWidget(VStudentFinDebt.class);
        studentDebtGW.showToolbar(false);
        DBGridModel studentDebtGM = (DBGridModel) studentDebtGW.getWidgetModel();
        studentDebtGM.setReadOnly(true);
        studentDebtGM.setRefreshType(ERefreshType.MANUAL);
        studentDebtGM.setMultiSelect(false);
        studentDebtGM.setRowNumberVisible(true);
        studentDebtGM.setRowNumberWidth(50);

        studentPaymentGW = new GridWidget(VStudentPayment.class);
        studentPaymentGW.showToolbar(false);
        DBGridModel studentPaymentGM = (DBGridModel) studentPaymentGW.getWidgetModel();
        studentPaymentGM.setReadOnly(true);
        studentPaymentGM.setRefreshType(ERefreshType.MANUAL);
        studentPaymentGM.setMultiSelect(false);
        studentPaymentGM.setRowNumberVisible(true);
        studentPaymentGM.setRowNumberWidth(50);

        getStudentFinDebt();
        getStudentPayment();

        getContent().addComponent(studentDebtGW);
        getContent().setComponentAlignment(studentDebtGW, Alignment.MIDDLE_CENTER);

        getContent().addComponent(studentPaymentGW);
        getContent().setComponentAlignment(studentPaymentGW, Alignment.MIDDLE_CENTER);
    }

    private void getStudentFinDebt() {
        List<VStudentFinDebt> list = new ArrayList<>();
        String sql = "select x2.id, trim(x.LAST_NAME||' '||x.FIRST_NAME||' '||coalesce(x.MIDDLE_NAME, '')) fio, x.code," +
                " x2.report_date reportDate, x2.debt_sum debtSum, x2.retake"//
                + " from USERS x inner join STUDENT x3 on x3.id=x.id" //
                + " inner join STUDENT_FIN_DEBT x2 on x2.student_id=x3.id" //
                + " where x2.deleted=false and x.deleted=false";
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql,
                    new HashMap<>());
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudentFinDebt sfd = new VStudentFinDebt();
                    sfd.setId(ID.valueOf((long) oo[0]));
                    sfd.setFio((String) oo[1]);
                    sfd.setCode((String) oo[2]);
                    sfd.setReportDate((Date) oo[3]);
                    sfd.setDebtSum(((BigDecimal) oo[4]).doubleValue());
                    sfd.setRetake((Boolean) oo[5]);
                    list.add(sfd);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load students' debt list", ex);
        }
        ((DBGridModel) studentDebtGW.getWidgetModel()).setEntities(list);
        try {
            studentDebtGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students' debt grid", ex);
        }
    }

    private void getStudentPayment() {
        List<VStudentPayment> list = new ArrayList<>();
        String sql = "select x2.id, trim(x.LAST_NAME||' '||x.FIRST_NAME||' '||coalesce(x.MIDDLE_NAME, '')) fio, " +
                "x.code, x2.payment_date date, x2.payment_sum paymentSum"//
                + " from USERS x inner join STUDENT x3 on x3.id=x.id" //
                + " inner join STUDENT_PAYMENT x2 on x2.student_id=x3.id"//
                + " where x.deleted=false"//
                + " order by x2.created desc";
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql,
                    new HashMap<>());
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudentPayment sp = new VStudentPayment();
                    sp.setId(ID.valueOf((long) oo[0]));
                    sp.setFio((String) oo[1]);
                    sp.setCode((String) oo[2]);
                    sp.setDate((Date) oo[3]);
                    sp.setPaymentSum(((BigDecimal) oo[4]).doubleValue());
                    list.add(sp);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load students' payment list", ex);
        }
        ((DBGridModel) studentPaymentGW.getWidgetModel()).setEntities(list);
        try {
            studentPaymentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students' payment grid", ex);
        }
    }
}
