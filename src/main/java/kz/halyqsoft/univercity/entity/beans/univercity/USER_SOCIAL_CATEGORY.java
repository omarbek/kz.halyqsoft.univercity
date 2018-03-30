package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SOCIAL_CATEGORY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Nov 24, 2015 2:51:53 PM
 */
@Entity
public class USER_SOCIAL_CATEGORY extends AbstractEntity {

    private static final long serialVersionUID = 3662923439027833950L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SOCIAL_CATEGORY_ID", referencedColumnName = "ID")})
    private SOCIAL_CATEGORY socialCategory;

    public USER_SOCIAL_CATEGORY() {
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public SOCIAL_CATEGORY getSocialCategory() {
        return socialCategory;
    }

    public void setSocialCategory(SOCIAL_CATEGORY socialCategory) {
        this.socialCategory = socialCategory;
    }
}
