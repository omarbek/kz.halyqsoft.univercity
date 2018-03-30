package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AWARD;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Nov 24, 2015 2:05:33 PM
 */
@Entity
public class V_USER_AWARD extends AbstractEntity {

    private static final long serialVersionUID = -4690588002827723871L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "AWARD_ID", referencedColumnName = "ID")})
    private AWARD award;

    @FieldInfo(type = EFieldType.TEXT, max = 128, order = 3, inEdit = false, inView = false)
    @Column(name = "AWARD_NAME", nullable = false)
    private String awardName;

    public V_USER_AWARD() {
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

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }
}
