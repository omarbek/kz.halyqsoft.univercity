package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.USER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE_LEVEL;
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
 * @created Nov 20, 2015 12:16:54 PM
 */
@Entity
public class V_USER_LANGUAGE extends AbstractEntity {

	private static final long serialVersionUID = 5657547032832009424L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USER user;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID")})
    private LANGUAGE language;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 3, inEdit = false, inView = false)
	@Column(name = "LANG_NAME", nullable = false)
	private String languageName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_LEVEL_ID", referencedColumnName = "ID")})
    private LANGUAGE_LEVEL languageLevel;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 5, inEdit = false, inView = false)
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;

	public V_USER_LANGUAGE() {
	}

	public USER getUser() {
		return user;
	}

	public void setUser(USER user) {
		this.user = user;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public LANGUAGE_LEVEL getLanguageLevel() {
		return languageLevel;
	}

	public void setLanguageLevel(LANGUAGE_LEVEL languageLevel) {
		this.languageLevel = languageLevel;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
}
