package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORDER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 16, 2015 10:16:11 AM
 */
@Entity
@DiscriminatorValue(value = "10")
public class ORDER_DOC extends USER_DOCUMENT {

	private static final long serialVersionUID = -2044181973284468895L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ORDER_TYPE_ID", referencedColumnName = "ID")})
    private ORDER_TYPE orderType;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 6, required = false)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 7)
	@Column(name = "DESCR")
	private String descr;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 8, required = false)
	@Column(name = "HIDE_TRANSCRIPT")
    private boolean hideTranscript;

	public ORDER_DOC() {
	}

	public ORDER_TYPE getOrderType() {
		return orderType;
	}

	public void setOrderType(ORDER_TYPE orderType) {
		this.orderType = orderType;
	}
	
	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public boolean isHideTranscript() {
		return hideTranscript;
	}

	public void setHideTranscript(boolean hideTranscript) {
		this.hideTranscript = hideTranscript;
	}
}
