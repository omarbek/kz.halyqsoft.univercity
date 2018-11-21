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
	private AbstractField department;
	private AbstractField startingDate;
	private AbstractField endingDate;
	@Override
	protected void initWidget() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();

		AbstractField department = getFilterComponent("department");
		if (department != null) {
			hl.addComponent(department);
		}

		startingDate = getFilterComponent("startingDate");
		if (startingDate != null) {
			hl.addComponent(startingDate);
		}

		endingDate = getFilterComponent("endingDate");
		if (endingDate != null) {
			hl.addComponent(endingDate);
		}

		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
	}

	public AbstractField getDepartment() {
		return department;
	}

	public AbstractField getStartingDate() {
		return startingDate;
	}

	public AbstractField getEndingDate() {
		return endingDate;
	}
}

