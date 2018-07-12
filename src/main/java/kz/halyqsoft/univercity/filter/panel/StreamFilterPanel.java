package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.FStreamFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

@SuppressWarnings("serial")
public final class StreamFilterPanel extends AbstractFilterPanel {

	public StreamFilterPanel(FStreamFilter filterBean) {
		super(filterBean);
	}

	@Override
	protected void initWidget() throws Exception {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();
		
		AbstractField af = getFilterComponent("semester");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("semesterData");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("code");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("name");
		if (af != null) {
			hl.addComponent(af);
		}
		
		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

	}
}
