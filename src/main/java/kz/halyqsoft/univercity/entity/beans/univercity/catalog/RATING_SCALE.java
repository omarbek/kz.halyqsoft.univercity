package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Jun 21, 2016 2:48:47 PM
 */
@Entity
public class RATING_SCALE extends AbstractEntity {

	private static final long serialVersionUID = -2509742922409478048L;

	@FieldInfo(type = EFieldType.TEXT, order = 1, max = 2)
	@Column(name = "ALPHA_GRADE")
	private String alphaGrade;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 2, max = 4)
	@Column(name = "DIGIT_GRADE", nullable = false)
	private Double digitGrade;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, max = 6)
	@Column(name = "MARK_BEFORE_01092008")
	private String markBefore01092008;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, max = 6)
	@Column(name = "MARK_AFTER_01092008")
	private String markAfter01092008;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5)
	@Column(name = "TOTAL_FROM", nullable = false)
	private Integer totalFrom;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 6)
	@Column(name = "TOTAL_TO", nullable = false)
	private Integer totalTo;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7, max = 65)
	@Column(name = "DESCR")
	private String descr;
	
	public RATING_SCALE() {
	}

	public String getAlphaGrade() {
		return alphaGrade;
	}

	public void setAlphaGrade(String alphaGrade) {
		this.alphaGrade = alphaGrade;
	}

	public Double getDigitGrade() {
		return digitGrade;
	}

	public void setDigitGrade(Double digitGrade) {
		this.digitGrade = digitGrade;
	}

	public String getMarkBefore01092008() {
		return markBefore01092008;
	}

	public void setMarkBefore01092008(String markBefore01092008) {
		this.markBefore01092008 = markBefore01092008;
	}

	public String getMarkAfter01092008() {
		return markAfter01092008;
	}

	public void setMarkAfter01092008(String markAfter01092008) {
		this.markAfter01092008 = markAfter01092008;
	}
	
	public Integer getTotalFrom() {
		return totalFrom;
	}

	public void setTotalFrom(Integer totalFrom) {
		this.totalFrom = totalFrom;
	}

	public Integer getTotalTo() {
		return totalTo;
	}

	public void setTotalTo(Integer totalTo) {
		this.totalTo = totalTo;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
