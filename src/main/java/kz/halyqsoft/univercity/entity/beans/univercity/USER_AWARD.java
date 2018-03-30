package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AWARD;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Nov 24, 2015 1:56:37 PM
 */
@Entity
public class USER_AWARD extends AbstractEntity {

    private static final long serialVersionUID = -8276808938333415674L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "AWARD_ID", referencedColumnName = "ID")})
    private AWARD award;

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 3)
    @Column(name = "SUBJECT_NAME")
    private String subjectName;

    public USER_AWARD() {
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public AWARD getAward() {
        return award;
    }

    public void setAward(AWARD award) {
        this.award = award;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
