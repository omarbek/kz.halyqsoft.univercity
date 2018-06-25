package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CURRICULUM_SCHEDULE_SYMBOL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MONTH;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Mar 1, 2016 10:37:56 AM
 */
@Entity
public class CURRICULUM_SCHEDULE extends AbstractEntity {

	private static final long serialVersionUID = -2108931801847237151L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "MONTH_ID", referencedColumnName = "ID")})
    private MONTH month;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "WEEK_ID", referencedColumnName = "ID")})
    private WEEK week;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SYMBOL_ID", referencedColumnName = "ID")})
    private CURRICULUM_SCHEDULE_SYMBOL symbol;
	
	public CURRICULUM_SCHEDULE() {
	}

	public CURRICULUM getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(CURRICULUM curriculum) {
		this.curriculum = curriculum;
	}

	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}

	public MONTH getMonth() {
		return month;
	}

	public void setMonth(MONTH month) {
		this.month = month;
	}

	public WEEK getWeek() {
		return week;
	}

	public void setWeek(WEEK week) {
		this.week = week;
	}

	public CURRICULUM_SCHEDULE_SYMBOL getSymbol() {
		return symbol;
	}

	public void setSymbol(CURRICULUM_SCHEDULE_SYMBOL symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol.toString();
	}

}
