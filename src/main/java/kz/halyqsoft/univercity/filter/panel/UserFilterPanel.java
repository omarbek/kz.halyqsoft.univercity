package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public final class UserFilterPanel extends AbstractFilterPanel {

    public UserFilterPanel(AbstractFilterBean filterBean) {
        super(filterBean);
    }

    @Override
    protected void initWidget() throws Exception {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        AbstractField af = getFilterComponent("code");
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

        af = getFilterComponent("student");
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
