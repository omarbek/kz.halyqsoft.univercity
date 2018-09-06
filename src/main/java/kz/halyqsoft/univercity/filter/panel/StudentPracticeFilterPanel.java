package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FInformationPracticeFilter;
import kz.halyqsoft.univercity.filter.FStudentPracticeFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

@SuppressWarnings("serial")
public final class StudentPracticeFilterPanel extends AbstractFilterPanel {

	public StudentPracticeFilterPanel(FStudentPracticeFilter filterBean) {
		super(filterBean);
	}

	@Override
	protected void initWidget() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();
		
		AbstractField af = getFilterComponent("student");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("organization");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("comeInDate");
		if (af != null) {
			hl.addComponent(af);
		}

		
		af = getFilterComponent("comeOutDate");
		if (af != null) {
			hl.addComponent(af);
		}


		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

	}
}
