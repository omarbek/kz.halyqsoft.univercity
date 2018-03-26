package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ACADEMIC_CALENDAR_ITEM;
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
 * @created Oct 22, 2016 2:12:14 PM
 */
@Entity
public class ACADEMIC_CALENDAR_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -3750409735345307840L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ACADEMIC_CALENDAR_ID", referencedColumnName = "ID")})
    private ACADEMIC_CALENDAR academicCalendar;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")})
    private ACADEMIC_CALENDAR_ITEM academicCalendarItem;
	
	@FieldInfo(type = EFieldType.DATE, order = 3)
	@Column(name = "DATE1")
    @Temporal(TemporalType.DATE)
    private Date date1;
	
	@FieldInfo(type = EFieldType.DATE, order = 4)
	@Column(name = "DATE2")
    @Temporal(TemporalType.DATE)
    private Date date2;
	
	@FieldInfo(type = EFieldType.DATE, order = 5)
	@Column(name = "DATE3")
    @Temporal(TemporalType.DATE)
    private Date date3;
	
	@FieldInfo(type = EFieldType.DATE, order = 6)
	@Column(name = "DATE4")
    @Temporal(TemporalType.DATE)
    private Date date4;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 7)
	@Column(name = "DESCR")
	private String descr;
	
	public ACADEMIC_CALENDAR_DETAIL() {
	}

	public ACADEMIC_CALENDAR getAcademicCalendar() {
		return academicCalendar;
	}

	public void setAcademicCalendar(ACADEMIC_CALENDAR academicCalendar) {
		this.academicCalendar = academicCalendar;
	}

	public ACADEMIC_CALENDAR_ITEM getAcademicCalendarItem() {
		return academicCalendarItem;
	}

	public void setAcademicCalendarItem(ACADEMIC_CALENDAR_ITEM academicCalendarItem) {
		this.academicCalendarItem = academicCalendarItem;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Date getDate3() {
		return date3;
	}

	public void setDate3(Date date3) {
		this.date3 = date3;
	}

	public Date getDate4() {
		return date4;
	}

	public void setDate4(Date date4) {
		this.date4 = date4;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
