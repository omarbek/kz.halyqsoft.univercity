package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

/**
 * @author Dinassil Omarbek
 * @created Apr 28, 2017 3:17:17 PM
 */
@SuppressWarnings("serial")
public final class SemesterSubjectFilterPanel extends AbstractFilterPanel {

	public SemesterSubjectFilterPanel(AbstractFilterBean filterBean) {
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

		af = getFilterComponent("subjectName");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("chair");
		if (af != null) {
			hl.addComponent(af);
		}

		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

		hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();

		af = getFilterComponent("creditability");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("subjectCycle");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("level");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("year");
		if (af != null) {
			hl.addComponent(af);
		}

		af = getFilterComponent("period");
		if (af != null) {
			hl.addComponent(af);
		}

		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
	}

}
