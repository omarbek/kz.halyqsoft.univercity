package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LOCK_REASON;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MARITAL_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.NATIONALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEX;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @@author Omarbek
 * @created Nov 9, 2015 10:48:13 AM
 */
@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "USER_TYPE_ID", discriminatorType = DiscriminatorType.INTEGER)
public abstract class USER extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 2)
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 3)
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 4, required = false)
	@Column(name = "MIDDLE_NAME", nullable = true)
	private String middleName;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 5, inGrid = false)
	@Column(name = "FIRST_NAME_EN", nullable = false)
	private String firstNameEN;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 6, inGrid = false)
	@Column(name = "LAST_NAME_EN", nullable = false)
	private String lastNameEN;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 7, required = false, inGrid = false)
	@Column(name = "MIDDLE_NAME_EN", nullable = true)
	private String middleNameEN;
	
	@FieldInfo(type = EFieldType.DATE, order = 8, inGrid = false)
	@Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 9, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEX_ID", referencedColumnName = "ID")})
    private SEX sex;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 10, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "MARITAL_STATUS_ID", referencedColumnName = "ID")})
    private MARITAL_STATUS maritalStatus;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 11, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "NATIONALITY_ID", referencedColumnName = "ID")})
    private NATIONALITY nationality;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 12, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CITIZENSHIP_ID", referencedColumnName = "ID")})
    private COUNTRY citizenship;
	
	@FieldInfo(type = EFieldType.TEXT_LABEL, max = 12, order = 13, inGrid = false, required = false, readOnlyFixed = true)
	@Column(name = "CODE")
	private String code;
	
	@FieldInfo(type = EFieldType.TEXT_LABEL, max = 32, order = 14, required = false, readOnlyFixed = true, inGrid = false)
	@Column(name = "LOGIN")
	private String login;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 15, required = false, inGrid = false)
	@Column(name = "EMAIL")
	private String email;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", max = 10, order = 16, required = false, inGrid = false)
	@Column(name = "PHONE_MOBILE")
	private String phoneMobile;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 17, required = false, inGrid = false)
	@Column(name = "PHONE_INTERNAL")
	private String phoneInternal;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 18, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "LOCKED", nullable = false)
    private boolean locked;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 19, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LOCK_REASON_ID", referencedColumnName = "ID")})
    private LOCK_REASON lockReason;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 20, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "SAVED_TO_AD", nullable = false)
    private boolean savedToAD;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 21, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "SAVED_TO_LIB", nullable = false)
    private boolean savedToLib;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 22, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "SAVED_TO_SKD", nullable = false)
    private boolean savedToSKD;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 23, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 24, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 25, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "INFO")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String info;

	@Column(name = "OLD_ID")
	private String oldId;

	@Column(name = "USER_TYPE_ID", insertable = false, updatable = false)
	private int typeIndex;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFirstNameEN() {
		return firstNameEN;
	}

	public void setFirstNameEN(String firstNameEN) {
		this.firstNameEN = firstNameEN;
	}

	public String getLastNameEN() {
		return lastNameEN;
	}

	public void setLastNameEN(String lastNameEN) {
		this.lastNameEN = lastNameEN;
	}

	public String getMiddleNameEN() {
		return middleNameEN;
	}

	public void setMiddleNameEN(String middleNameEN) {
		this.middleNameEN = middleNameEN;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public SEX getSex() {
		return sex;
	}

	public void setSex(SEX sex) {
		this.sex = sex;
	}

	public MARITAL_STATUS getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MARITAL_STATUS maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public NATIONALITY getNationality() {
		return nationality;
	}

	public void setNationality(NATIONALITY nationality) {
		this.nationality = nationality;
	}

	public COUNTRY getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(COUNTRY citizenship) {
		this.citizenship = citizenship;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}
	
	public String getPhoneInternal() {
		return phoneInternal;
	}

	public void setPhoneInternal(String phoneInternal) {
		this.phoneInternal = phoneInternal;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public LOCK_REASON getLockReason() {
		return lockReason;
	}

	public void setLockReason(LOCK_REASON lockReason) {
		this.lockReason = lockReason;
	}
	
	public boolean isSavedToAD() {
		return savedToAD;
	}

	public void setSavedToAD(boolean savedToAD) {
		this.savedToAD = savedToAD;
	}

	public boolean isSavedToLib() {
		return savedToLib;
	}

	public void setSavedToLib(boolean savedToLib) {
		this.savedToLib = savedToLib;
	}

	public boolean isSavedToSKD() {
		return savedToSKD;
	}

	public void setSavedToSKD(boolean savedToSKD) {
		this.savedToSKD = savedToSKD;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	public int getTypeIndex() {
		return typeIndex;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lastName);
		sb.append(" ");
		sb.append(firstName);
		if (middleName != null) {
			sb.append(" ");
			sb.append(middleName);
		}
		
		return sb.toString();
	}
	
	public String toString(Locale l) {
		if (l.getLanguage().equals("en")) {
			String s = lastNameEN + " " + firstNameEN;
			if (middleNameEN != null) {
				s = s + " " + middleNameEN;
			}
			
			return s;
		}
		
		return toString();
	}
}
