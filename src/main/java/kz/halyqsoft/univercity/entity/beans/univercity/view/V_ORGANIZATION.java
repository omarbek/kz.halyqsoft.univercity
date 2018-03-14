package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Dec 30, 2015 9:13:57 AM
 */
@Entity
public class V_ORGANIZATION extends AbstractEntity {

	private static final long serialVersionUID = -6363200137577197295L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ORGANIZATION_TYPE_ID", referencedColumnName = "ID")})
    private ORGANIZATION_TYPE organizationType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 2, inEdit = false, inView = false, columnWidth = 120)
	@Column(name = "ORGANIZATION_TYPE_NAME", nullable = false)
	private String organizationTypeName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 3)
	@Column(name = "ORGANIZATION_NAME", nullable = false)
	private String organizationName;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 4, required = false, inGrid = false)
	@Column(name = "PHONE", nullable = false)
	private String phone;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 5, required = false, inGrid = false)
	@Column(name = "PHONE_MOBILE", nullable = false)
	private String phoneMobile;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 6, required = false, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "ID")})
    private COUNTRY country;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 7, inGrid = false, inEdit = false, inView = false)
	@Column(name = "COUNTRY_NAME", nullable = false)
	private String countryName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 8, required = false, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "REGION_ID", referencedColumnName = "ID")})
    private COUNTRY region;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 9, inGrid = false, inEdit = false, inView = false)
	@Column(name = "REGION_NAME", nullable = false)
	private String regionName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 512, order = 10, required = false)
	@Column(name = "ADDRESS", nullable = true)
	private String address;
	
	public V_ORGANIZATION() {
	}

	public ORGANIZATION_TYPE getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(ORGANIZATION_TYPE organizationType) {
		this.organizationType = organizationType;
	}

	public String getOrganizationTypeName() {
		return organizationTypeName;
	}

	public void setOrganizationTypeName(String organizationTypeName) {
		this.organizationTypeName = organizationTypeName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public COUNTRY getCountry() {
		return country;
	}

	public void setCountry(COUNTRY country) {
		this.country = country;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public COUNTRY getRegion() {
		return region;
	}

	public void setRegion(COUNTRY region) {
		this.region = region;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return organizationName;
	}
}
