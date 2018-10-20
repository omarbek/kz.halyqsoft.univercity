package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PASSPORT_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Nov 13, 2015 2:11:23 PM
 */
@Entity
@DiscriminatorValue(value = "1")
public class USER_PASSPORT extends USER_DOCUMENT {

	private static final long serialVersionUID = -1105372207725282569L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PASSPORT_TYPE_ID", referencedColumnName = "ID")})
    private PASSPORT_TYPE passportType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 6)
	@Column(name = "ISSUER_NAME")
	private String issuerName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 7, required = false)
	@Column(name = "IIN")
	private String iin;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 8)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "BIRTH_COUNTRY_ID", referencedColumnName = "ID")})
    private COUNTRY birthCountry;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 9)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "BIRTH_REGION_ID", referencedColumnName = "ID")})
    private COUNTRY birthRegion;


	@FieldInfo(type = EFieldType.TEXT, max = 24, order = 10)
	@Column(name = "SERIAL_NUMBER")
	private String serialNumber;

	public USER_PASSPORT() {
	}

	public PASSPORT_TYPE getPassportType() {
		return passportType;
	}

	public void setPassportType(PASSPORT_TYPE passportType) {
		this.passportType = passportType;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getIin() {
		return iin;
	}

	public void setIin(String iin) {
		this.iin = iin;
	}

	public COUNTRY getBirthCountry() {
		return birthCountry;
	}

	public void setBirthCountry(COUNTRY birthCountry) {
		this.birthCountry = birthCountry;
	}

	public COUNTRY getBirthRegion() {
		return birthRegion;
	}

	public void setBirthRegion(COUNTRY birthRegion) {
		this.birthRegion = birthRegion;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}
