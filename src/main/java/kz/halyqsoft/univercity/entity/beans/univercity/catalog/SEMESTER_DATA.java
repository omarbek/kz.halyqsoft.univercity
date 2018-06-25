package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.*;

/**
 * @author Omarbek
 * Created Sep 14, 2016 10:24:53 AM
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "SEMESTER_DATA.findCurrentSemesterData",
				query = "SELECT sd FROM SEMESTER_DATA sd WHERE CURRENT_DATE BETWEEN sd.beginDate AND sd.endDate")
})
public class SEMESTER_DATA extends AbstractEntity {

	private static final long serialVersionUID = -8425361689778390188L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR year;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.DATE, order = 3)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.DATE)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 4)
	@Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;
	
	public SEMESTER_DATA() {
	}

	public ENTRANCE_YEAR getYear() {
		return year;
	}

	public void setYear(ENTRANCE_YEAR year) {
		this.year = year;
	}

	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
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

	@Override
	public String toString() {
		return getYear().toString()+" "+getSemesterPeriod().toString();
	}
}
