package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

public class ChairFilterPanel extends AbstractFilterPanel {

    public ChairFilterPanel(AbstractFilterBean filterBean) {
        super(filterBean);
    }

    @Override
    protected void initWidget() throws Exception {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        AbstractField af = getFilterComponent("chair");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("studentDiplomaType");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("studyYear");
        if (af != null) {
            hl.addComponent(af);
        }

        af = getFilterComponent("semesterPeriod");
        if (af != null) {
            hl.addComponent(af);
        }

        getContent().addComponent(hl);
        getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
    }
}
