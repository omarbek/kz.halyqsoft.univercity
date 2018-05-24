package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FAccountantFilter;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

public class AccountantFilterPanel extends AbstractFilterPanel {


    public AccountantFilterPanel(FAccountantFilter filterBean) {
        super(filterBean);
    }

    @Override
    protected void initWidget() throws Exception {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        AbstractField af = getFilterComponent("diplomaType");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("level");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("contractPaymentType");
        if (af != null) {
            hl.addComponent(af);
        }


        getContent().addComponent(hl);
        getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
    }
}
