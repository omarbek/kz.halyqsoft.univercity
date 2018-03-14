package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Nov 13, 2015 5:25:04 PM
 */
@Entity
public class ORGANIZATION extends AbstractEntity {

	private static final long serialVersionUID = 5712649259862737448L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ORGANIZATION_TYPE_ID", referencedColumnName = "ID")})
    private ORGANIZATION_TYPE organizationType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 2)
	@Column(name = "ORGANIZATION_NAME", nullable = false)
	private String organizationName;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 3, required = false)
	@Column(name = "PHONE", nullable = false)
	private String phone;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 4, required = false)
	@Column(name = "PHONE_MOBILE", nullable = false)
	private String phoneMobile;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "ID")})
    private COUNTRY country;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "REGION_ID", referencedColumnName = "ID")})
    private COUNTRY region;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 7, required = false)
	@Column(name = "ADDRESS", nullable = true)
	private String address;

	public ORGANIZATION() {
	}

	public ORGANIZATION_TYPE getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(ORGANIZATION_TYPE organizationType) {
		this.organizationType = organizationType;
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

	public COUNTRY getRegion() {
		return region;
	}

	public void setRegion(COUNTRY region) {
		this.region = region;
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
