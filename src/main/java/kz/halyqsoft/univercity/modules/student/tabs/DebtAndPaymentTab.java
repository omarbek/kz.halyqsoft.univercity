package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_FIN_DEBT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_PAYMENT;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

/**
 * @author Omarbek
 * @created 17.05.2017.
 */
@SuppressWarnings("serial")
public class DebtAndPaymentTab extends VerticalLayout {

    public DebtAndPaymentTab(ID studentId, boolean readOnly) {
        super();
        setSpacing(true);
        setSizeFull();

        TableWidget debtTW = new TableWidget(STUDENT_FIN_DEBT.class);
        debtTW.setWidth(50, Unit.PERCENTAGE);
        //		debtTW.setHeight(100, Unit.PERCENTAGE);
        //		debtTW.showToolbar(false);
        DBTableModel debtTM = (DBTableModel) debtTW.getWidgetModel();
        debtTM.setReadOnly(true);
        QueryModel debtQM = debtTM.getQueryModel();
        debtQM.addWhere("student", ECriteria.EQUAL, studentId);
        debtQM.addWhere("deleted", Boolean.FALSE);
        debtQM.addOrder("reportDate");

        addComponent(debtTW);
        setComponentAlignment(debtTW, Alignment.MIDDLE_CENTER);

        TableWidget paymentTW = new TableWidget(STUDENT_PAYMENT.class);
        paymentTW.setWidth(50, Unit.PERCENTAGE);
        //		paymentTW.setHeight(100, Unit.PERCENTAGE);
        //		paymentTW.showToolbar(false);
        DBTableModel paymentTM = (DBTableModel) paymentTW.getWidgetModel();
        paymentTM.setReadOnly(true);
        QueryModel paymentQM = paymentTM.getQueryModel();
        paymentQM.addWhere("student", ECriteria.EQUAL, studentId);
        paymentQM.addOrder("paymentDate");

        addComponent(paymentTW);
        setComponentAlignment(paymentTW, Alignment.MIDDLE_CENTER);
    }
}
