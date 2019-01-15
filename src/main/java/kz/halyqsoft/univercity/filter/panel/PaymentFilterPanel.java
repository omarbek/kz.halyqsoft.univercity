package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FPaymentFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

/**
 * @author Omarbek
 * @created Mar 28, 2018 9:40:44 AM
 */
@SuppressWarnings("serial")
public final class PaymentFilterPanel extends AbstractFilterPanel {

    public PaymentFilterPanel(FPaymentFilter filterBean) {
        super(filterBean);
    }

    @Override
    protected void initWidget() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        AbstractField af = getFilterComponent("code");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("card");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("firstname");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("lastname");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("studentStatus");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("lockReason");
        if (af != null) {
            hl.addComponent(af);
        }

        getContent().addComponent(hl);
        getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

        hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        af = getFilterComponent("faculty");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("speciality");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("studyYear");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("educationType");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("studentDiplomaType");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("group");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("date");
        if (af != null) {
            hl.addComponent(af);
        }

        getContent().addComponent(hl);
        getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
    }
}
