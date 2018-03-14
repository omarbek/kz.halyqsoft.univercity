package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ADDRESS_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
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
 * @created Nov 23, 2015 1:50:40 PM
 */
@Entity
public class USER_ADDRESS extends AbstractEntity {

	private static final long serialVersionUID = 8929462338657286667L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USER user;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ADDRESS_TYPE_ID", referencedColumnName = "ID")})
    private ADDRESS_TYPE addressType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, required = false, max = 6)
	@Column(name = "POSTAL_CODE")
	private String postalCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "ID")})
    private COUNTRY country;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "REGION_ID", referencedColumnName = "ID")})
    private COUNTRY region;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CITY_ID", referencedColumnName = "ID")})
    private COUNTRY city;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 7, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "VILLAGE_ID", referencedColumnName = "ID")})
    private COUNTRY village;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, required = false, max = 128)
	@Column(name = "STREET")
	private String street;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 9, required = false)
	@Column(name = "PHONE_HOME")
	private String phoneHome;

	public USER_ADDRESS() {
	}

	public USER getUser() {
		return user;
	}

	public void setUser(USER user) {
		this.user = user;
	}

	public ADDRESS_TYPE getAddressType() {
		return addressType;
	}

	public void setAddressType(ADDRESS_TYPE addressType) {
		this.addressType = addressType;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public COUNTRY getCountry() {
		return country;
	}

	public void setCountry(COUNTRY country) {
		this.country = country;
	}

	public COUNTRY getRegion() {
		return region;
	}

	public void setRegion(COUNTRY region) {
		this.region = region;
	}

	public COUNTRY getCity() {
		return city;
	}

	public void setCity(COUNTRY city) {
		this.city = city;
	}

	public COUNTRY getVillage() {
		return village;
	}

	public void setVillage(COUNTRY village) {
		this.village = village;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPhoneHome() {
		return phoneHome;
	}

	public void setPhoneHome(String phoneHome) {
		this.phoneHome = phoneHome;
	}
}
