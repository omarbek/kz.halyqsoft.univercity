package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.RELATIVE_TYPE;
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
 * @created Nov 23, 2015 5:32:36 PM
 */
@Entity
public class STUDENT_RELATIVE extends AbstractEntity {

	private static final long serialVersionUID = 1139428063564298618L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "RELATIVE_TYPE_ID", referencedColumnName = "ID")})
    private RELATIVE_TYPE relativeType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, max = 128)
	@Column(name = "FIO")
	private String fio;
	
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
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 10, required = false)
	@Column(name = "PHONE_MOBILE")
	private String phoneMobile;
	
	@FieldInfo(type = EFieldType.TEXT, order = 11, required = false, max = 32)
	@Column(name = "EMAIL")
	private String email;
	
	@FieldInfo(type = EFieldType.TEXT, order = 12, required = false, max = 128)
	@Column(name = "WORK_PLACE")
	private String workPlace;
	
	@FieldInfo(type = EFieldType.TEXT, order = 13, required = false, max = 128)
	@Column(name = "POST_NAME")
	private String postName;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 14, required = false)
	@Column(name = "PHONE_WORK")
	private String phoneWork;

	public STUDENT_RELATIVE() {
	}

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public RELATIVE_TYPE getRelativeType() {
		return relativeType;
	}

	public void setRelativeType(RELATIVE_TYPE relativeType) {
		this.relativeType = relativeType;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
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

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getPhoneWork() {
		return phoneWork;
	}

	public void setPhoneWork(String phoneWork) {
		this.phoneWork = phoneWork;
	}
}
