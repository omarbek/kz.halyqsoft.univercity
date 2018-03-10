package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 6, 2017 10:09:22 AM
 */
public final class FSubjectFilter extends AbstractFilterBean {

	private static final long serialVersionUID = 3280308708403197770L;
	
	private String code;
	private String subjectName;
	private DEPARTMENT chair;
	private CREDITABILITY creditability;
	private SUBJECT_CYCLE subjectCycle;
	private LEVEL level;
	
	public FSubjectFilter() {
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

	@Override
	public boolean hasFilter() {
		return (!(code == null && subjectName == null && chair == null && creditability == null && subjectCycle == null && level == null));
	}
}
