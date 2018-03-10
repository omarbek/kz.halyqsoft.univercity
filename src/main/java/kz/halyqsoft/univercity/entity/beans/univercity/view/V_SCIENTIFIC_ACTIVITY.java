package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SCIENTIFIC_ACTIVITY_TYPE;
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
 * @author Rakymzhan A. Kenzhegul
 * @created Dec 30, 2015 1:20:43 PM
 */
@Entity
public class V_SCIENTIFIC_ACTIVITY extends AbstractEntity {

	private static final long serialVersionUID = 3551562177351081321L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCIENTIFIC_ACTIVITY_TYPE_ID", referencedColumnName = "ID")})
    private SCIENTIFIC_ACTIVITY_TYPE scientificActivityType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = false, inView = false, columnWidth = 200)
	@Column(name = "SCIENTIFIC_ACTIVITY_TYPE_NAME", nullable = false)
	private String scientificActivityTypeName;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 512, order = 4)
	@Column(name = "TOPIC", nullable = false)
	private String topic;
	
	@FieldInfo(type = EFieldType.DATE, order = 5, required = false, inGrid = false)
	@Column(name = "BEGIN_DATE")
    @Temporal(TemporalType.DATE)
    private Date beginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 6, required = false, inGrid = false)
	@Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;
	
	public V_SCIENTIFIC_ACTIVITY() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public SCIENTIFIC_ACTIVITY_TYPE getScientificActivityType() {
		return scientificActivityType;
	}

	public void setScientificActivityType(
			SCIENTIFIC_ACTIVITY_TYPE scientificActivityType) {
		this.scientificActivityType = scientificActivityType;
	}

	public String getScientificActivityTypeName() {
		return scientificActivityTypeName;
	}

	public void setScientificActivityTypeName(String scientificActivityTypeName) {
		this.scientificActivityTypeName = scientificActivityTypeName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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
