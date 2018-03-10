package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PROPERTY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jan 25, 2017 2:26:50 PM
 */
@Entity
public class SCHEDULE_PROPERTY extends AbstractEntity {

	private static final long serialVersionUID = 8280000384225829364L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_DETAIL_ID", referencedColumnName = "ID")})
    private SCHEDULE_DETAIL scheduleDetail;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PROPERTY_ID", referencedColumnName = "ID")})
    private PROPERTY property;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3)
	@Column(name = "PROPERTY_VALUE")
	private String propertyValue;
	
	public SCHEDULE_PROPERTY() {
	}

	public SCHEDULE_DETAIL getScheduleDetail() {
		return scheduleDetail;
	}

	public void setScheduleDetail(SCHEDULE_DETAIL scheduleDetail) {
		this.scheduleDetail = scheduleDetail;
	}

	public PROPERTY getProperty() {
		return property;
	}

	public void setProperty(PROPERTY property) {
		this.property = property;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
}
