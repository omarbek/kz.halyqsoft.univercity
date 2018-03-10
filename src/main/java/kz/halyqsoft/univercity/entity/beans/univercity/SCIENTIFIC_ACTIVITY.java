package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SCIENTIFIC_ACTIVITY_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Dec 21, 2015 4:35:02 PM
 */
@Entity
@DiscriminatorValue(value = "2")
public class SCIENTIFIC_ACTIVITY extends EMPLOYEE_SCIENTIFIC {

	private static final long serialVersionUID = -8246453104675902234L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCIENTIFIC_ACTIVITY_TYPE_ID", referencedColumnName = "ID")})
    private SCIENTIFIC_ACTIVITY_TYPE scientificActivityType;
	
	@FieldInfo(type = EFieldType.DATE, order = 4, required = false, inGrid = false)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.DATE)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 5, required = false, inGrid = false)
	@Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;
	
	public SCIENTIFIC_ACTIVITY() {
	}

	public SCIENTIFIC_ACTIVITY_TYPE getScientificActivityType() {
		return scientificActivityType;
	}

	public void setScientificActivityType(SCIENTIFIC_ACTIVITY_TYPE scientificActivityType) {
		this.scientificActivityType = scientificActivityType;
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
