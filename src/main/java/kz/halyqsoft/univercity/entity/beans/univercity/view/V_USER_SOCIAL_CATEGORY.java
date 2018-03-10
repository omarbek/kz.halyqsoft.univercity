package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.USER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SOCIAL_CATEGORY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 24, 2015 3:08:27 PM
 */
@Entity
public class V_USER_SOCIAL_CATEGORY extends AbstractEntity {
	
	private static final long serialVersionUID = 6267385122574436600L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USER user;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SOCIAL_CATEGORY_ID", referencedColumnName = "ID")})
    private SOCIAL_CATEGORY socialCategory;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 3, inEdit = false, inView = false)
	@Column(name = "SOCIAL_CATEGORY_NAME", nullable = false)
	private String socialCategoryName;

	public V_USER_SOCIAL_CATEGORY() {
	}

	public USER getUser() {
		return user;
	}

	public void setUser(USER user) {
		this.user = user;
	}

	public SOCIAL_CATEGORY getSocialCategory() {
		return socialCategory;
	}

	public void setSocialCategory(SOCIAL_CATEGORY socialCategory) {
		this.socialCategory = socialCategory;
	}

	public String getSocialCategoryName() {
		return socialCategoryName;
	}

	public void setSocialCategoryName(String socialCategoryName) {
		this.socialCategoryName = socialCategoryName;
	}
}
