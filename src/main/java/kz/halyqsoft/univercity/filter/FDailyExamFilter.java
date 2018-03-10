package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Dinassil Omarbek
 * @created Apr 19, 2017 11:12:31 AM
 */
public final class FDailyExamFilter extends AbstractFilterBean {

	private static final long serialVersionUID = 5338142998400760891L;

	private ENTRANCE_YEAR year;
	private SEMESTER_PERIOD period;

	public ENTRANCE_YEAR getYear() {
		return year;
	}

	public void setYear(ENTRANCE_YEAR year) {
		this.year = year;
	}

	public SEMESTER_PERIOD getPeriod() {
		return period;
	}

	public void setPeriod(SEMESTER_PERIOD period) {
		this.period = period;
	}

	public FDailyExamFilter() {
	}

	@Override
	public boolean hasFilter() {
		return !(year == null && period == null);
	}
}
