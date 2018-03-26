package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * @created Sep 15, 2016 1:39:14 PM
 */
@Entity
public class V_SEMESTER_DATA extends AbstractEntity {

	private static final long serialVersionUID = -660129315662920065L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR year;
	
	@FieldInfo(type = EFieldType.TEXT, min = 9, max = 9, order = 2)
	@Column(name = "ENTRANCE_YEAR", nullable = false)
	private String entranceYear;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4)
	@Column(name = "PERIOD_NAME", nullable = false)
	private String periodName;
	
	@FieldInfo(type = EFieldType.DATE, order = 5)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.DATE)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 6)
	@Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;
	
	public V_SEMESTER_DATA() {
	}

	public ENTRANCE_YEAR getYear() {
		return year;
	}

	public void setYear(ENTRANCE_YEAR year) {
		this.year = year;
	}

	public String getEntranceYear() {
		return entranceYear;
	}

	public void setEntranceYear(String entranceYear) {
		this.entranceYear = entranceYear;
	}

	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
