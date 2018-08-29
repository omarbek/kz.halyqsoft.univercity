package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FChangeToNextYearGroupFilter;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

@SuppressWarnings("serial")
public final class ChangeToNextYearGroupFilterPanel extends AbstractFilterPanel {

	public ChangeToNextYearGroupFilterPanel(FChangeToNextYearGroupFilter filterBean) {
		super(filterBean);
	}

	@Override
	protected void initWidget() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();
		
		AbstractField af = getFilterComponent("speciality");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("group");
		if (af != null) {
			hl.addComponent(af);
		}

		
		af = getFilterComponent("studyYear");
		if (af != null) {
			hl.addComponent(af);
		}
		
		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

	}
}
