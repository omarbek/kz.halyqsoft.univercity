package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
 * @created Apr 6, 2018 10:09:22 AM
 */
public final class FSubjectFilter extends AbstractFilterBean {

	private static final long serialVersionUID = 3280308708403197770L;
	
//	private String code;
	private String subjectName;
	private DEPARTMENT chair;
	private CREDITABILITY creditability;
	private SUBJECT_CYCLE subjectCycle;
	private LEVEL level;
	private SUBJECT_MODULE subjectModule;
	
//	public String getCode() {
//		return code;
//	}
//
//	public void setCode(String code) {
//		this.code = code;
//	}
//
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

	public SUBJECT_MODULE getSubjectModule() {
		return subjectModule;
	}

	public void setSubjectModule(SUBJECT_MODULE subjectModule) {
		this.subjectModule = subjectModule;
	}

	@Override
	public boolean hasFilter() {
		return (!(/*code == null &&*/ subjectName == null && chair == null && creditability == null
				&& subjectCycle == null && level == null && subjectModule == null));
	}
}
