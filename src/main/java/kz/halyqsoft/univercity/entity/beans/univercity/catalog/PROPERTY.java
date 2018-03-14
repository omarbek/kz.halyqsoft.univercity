package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Jan 25, 2017 12:49:55 PM
 */
@Entity
public class PROPERTY extends AbstractEntity {

	private static final long serialVersionUID = 5911374553706884769L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PROPERTY_TYPE_ID", referencedColumnName = "ID")})
    private PROPERTY_TYPE propertyType;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DATA_TYPE_ID", referencedColumnName = "ID")})
    private DATA_TYPE dataType;
	
	@Column(name = "PROPERTY_NAME", nullable = false)
	private String propertyName;
	
	public PROPERTY() {
	}

	public PROPERTY_TYPE getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PROPERTY_TYPE propertyType) {
		this.propertyType = propertyType;
	}

	public DATA_TYPE getDataType() {
		return dataType;
	}

	public void setDataType(DATA_TYPE dataType) {
		this.dataType = dataType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String toString() {
		return propertyName;
	}
}
