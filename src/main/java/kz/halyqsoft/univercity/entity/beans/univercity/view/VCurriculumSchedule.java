package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

/**
 * @author Omarbek
 * @created Feb 22, 2018 2:21:13 PM
 */
public final class VCurriculumSchedule extends AbstractEntity {

	private static final long serialVersionUID = 5596716420047672169L;
	
	private int weekCode;
	private String symbolCode;
	
	public VCurriculumSchedule() {
	}

	public int getWeekCode() {
		return weekCode;
	}

	public void setWeekCode(int weekCode) {
		this.weekCode = weekCode;
	}

	public String getSymbolCode() {
		return symbolCode;
	}

	public void setSymbolCode(String symbolCode) {
		this.symbolCode = symbolCode;
	}
}
