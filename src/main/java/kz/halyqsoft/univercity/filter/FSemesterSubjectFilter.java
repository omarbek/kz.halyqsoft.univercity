package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Dinassil Omarbek
 * @created Apr 28, 2018 3:07:53 PM
 */
public final class FSemesterSubjectFilter extends AbstractFilterBean {

	private static final long serialVersionUID = 3280308708403197770L;

	private String code;
	private String subjectName;
	private DEPARTMENT chair;
	private CREDITABILITY creditability;
	private SUBJECT_CYCLE subjectCycle;
	private LEVEL level;
	private ENTRANCE_YEAR year;
	private SEMESTER_PERIOD period;

	public FSemesterSubjectFilter() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
	}

	public CREDITABILITY getCreditability() {
		return creditability;
	}

	public void setCreditability(CREDITABILITY creditability) {
		this.creditability = creditability;
	}

	public SUBJECT_CYCLE getSubjectCycle() {
		return subjectCycle;
	}

	public void setSubjectCycle(SUBJECT_CYCLE subjectCycle) {
		this.subjectCycle = subjectCycle;
	}

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

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

	@Override
	public boolean hasFilter() {
		return !(code == null && subjectName == null && chair == null && creditability == null && subjectCycle == null && level == null && year == null && period == null);
	}
}
