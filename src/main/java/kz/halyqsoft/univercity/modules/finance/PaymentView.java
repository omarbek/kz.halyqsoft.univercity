package kz.halyqsoft.univercity.modules.finance;

import com.vaadin.ui.Alignment;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_PAYMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentPayment;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Omarbek
 * @created on 01.08.2018
 */
public class PaymentView extends AbstractTaskView implements EntityListener {

    private GridWidget studentPaymentGW;

    public PaymentView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        studentPaymentGW = new GridWidget(VStudentPayment.class);
        studentPaymentGW.addEntityListener(this);
        studentPaymentGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        DBGridModel studentDebtGM = (DBGridModel) studentPaymentGW.getWidgetModel();
        studentDebtGM.setCrudEntityClass(STUDENT_PAYMENT.class);
        studentDebtGM.setRefreshType(ERefreshType.MANUAL);
        studentDebtGM.setMultiSelect(false);
        studentDebtGM.setRowNumberVisible(true);
        studentDebtGM.setRowNumberWidth(50);

        refresh();

        getContent().addComponent(studentPaymentGW);
        getContent().setComponentAlignment(studentPaymentGW, Alignment.MIDDLE_CENTER);
    }

    private void refresh() {
        List<VStudentPayment> list = new ArrayList<>();
        String sql = "select x2.id, trim(x.LAST_NAME||' '||x.FIRST_NAME||' '||coalesce(x.MIDDLE_NAME, '')) fio, " +
                "x.code, x2.created, x2.payment_sum paymentSum"//
                + " from USERS x inner join STUDENT x3 on x3.id=x.id" //
                + " inner join STUDENT_PAYMENT x2 on x2.student_id=x3.id"//
                + " where x.deleted=false"//
                + " order by x2.created desc";
        //FinanceView.fillList(list, sql, new HashMap<>());

        ((DBGridModel) studentPaymentGW.getWidgetModel()).setEntities(list);
        try {
            studentPaymentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students' payment grid", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED
                || ev.getAction() == EntityEvent.MERGED
                || ev.getAction() == EntityEvent.REMOVED) {
            refresh();
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        STUDENT_PAYMENT studentPayment = (STUDENT_PAYMENT) e;
        studentPayment.setCreated(new Date());
        return true;
    }
}
