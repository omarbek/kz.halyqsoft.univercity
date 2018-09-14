package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FStatisticsFilter;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;


@SuppressWarnings("serial")
public final class StatisticsFilterPanel extends AbstractFilterPanel {

	public StatisticsFilterPanel(FStatisticsFilter filterBean) {
		super(filterBean);
	}

	@Override
	protected void initWidget() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();

		AbstractField af = getFilterComponent("department");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("startingDate");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("endingDate");
		if (af != null) {
			hl.addComponent(af);
		}

		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
	}
}

