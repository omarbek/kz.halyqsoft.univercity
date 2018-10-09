package kz.halyqsoft.univercity.entity.beans;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractUser;

import javax.persistence.*;
import java.util.Date;
import java.util.Locale;

/**
 * Author Omarbek
 * Created 25.10.2008 14:43:26
 */
@Entity
public class M_USERS extends AbstractUser {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TASK_ID", referencedColumnName = "ID")})
    private TASKS task;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_TYPE_ID", referencedColumnName = "ID")})
    private USER_TYPE userType;

    @FieldInfo(type = EFieldType.TEXT_LABEL, max = 32, required = false, readOnlyFixed = true)
    @Column(name = "LOGIN")
    private String login;

    @FieldInfo(type = EFieldType.TEXT, max = 32, order = 2)
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;//

    @FieldInfo(type = EFieldType.TEXT, max = 32, order = 3)
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @FieldInfo(type = EFieldType.TEXT, max = 32, order = 4, required = false)
    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 5, inGrid = false)
    @Column(name = "FIRST_NAME_EN", nullable = false)
    private String firstNameEN;

    @FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 6, inGrid = false)
    @Column(name = "LAST_NAME_EN", nullable = false)
    private String lastNameEN;

    @FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 7, required = false, inGrid = false)
    @Column(name = "MIDDLE_NAME_EN")
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

    @FieldInfo(type = EFieldType.TEXT_LABEL, max = 12, order = 13, inGrid = false, required = false, readOnlyFixed = true, inEdit = false)
    @Column(name = "CODE")
    private String code;

    @FieldInfo(type = EFieldType.TEXT, max = 32, order = 15, required = false, inGrid = false)
    @Column(name = "EMAIL")
    private String email;

    @FieldInfo(type = EFieldType.TEXT, fieldMask = "(###)-###-####", max = 10, order = 16, required = false, inGrid = false)
    @Column(name = "PHONE_MOBILE")
    private String phoneMobile;

    @FieldInfo(type = EFieldType.TEXT, max = 128, order = 17, required = false, inGrid = false)
    @Column(name = "PHONE_INTERNAL")
    private String phoneInternal;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 19, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "LOCKED", nullable = false)
    private boolean locked;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 20, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LOCK_REASON_ID", referencedColumnName = "ID")})
    private LOCK_REASON lockReason;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 21, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @FieldInfo(type = EFieldType.DATETIME, order = 22, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "CREATED_BY")
    private String createdBy;

    public M_USERS() {
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public TASKS getTask() {
        return task;
    }

    public void setTask(TASKS task) {
        this.task = task;
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

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public USER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(USER_TYPE userType) {
        this.userType = userType;
    }
}
