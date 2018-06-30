package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 10:56:09 AM
 */
@Entity
public class LANGUAGE extends AbstractEntity {

	public static final ID KAZAKH = ID.valueOf(1);
	public static final ID RUSSIAN = ID.valueOf(2);
	public static final ID ENGLISH = ID.valueOf(3);

	@FieldInfo(type = EFieldType.TEXT, max = 16, order = 1)
	@Column(name = "LANG_NAME", nullable = false)
	private String langName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 3, order = 2)
	@Column(name = "LANG_SHORT_NAME", nullable = false)
	private String langShortName;

	public LANGUAGE() {
	}

	public String getLangName() {
		return langName;
	}

	public void setLangName(String langName) {
		this.langName = langName;
	}
	
	public String getLangShortName() {
		return langShortName;
	}

	public void setLangShortName(String langShortName) {
		this.langShortName = langShortName;
	}

	@Override
	public String toString() {
		return langName;
	}
}
