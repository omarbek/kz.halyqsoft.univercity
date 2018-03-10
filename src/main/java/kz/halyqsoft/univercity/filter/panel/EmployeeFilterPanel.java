package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FEmployeeFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 10, 2017 5:25:59 PM
 */
@SuppressWarnings("serial")
public final class EmployeeFilterPanel extends AbstractFilterPanel {

	public EmployeeFilterPanel(FEmployeeFilter filterBean) {
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
		
		af = getFilterComponent("department");
		if (af != null) {
			hl.addComponent(af);
		}
		
		af = getFilterComponent("post");
		if (af != null) {
			hl.addComponent(af);
		}
		
		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
	}
}
