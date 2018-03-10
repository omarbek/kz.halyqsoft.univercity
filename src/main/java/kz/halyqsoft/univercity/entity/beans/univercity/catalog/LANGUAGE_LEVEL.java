package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Oct 27, 2015 10:58:14 AM
 */
@Entity
public class LANGUAGE_LEVEL extends AbstractEntity {

	private static final long serialVersionUID = 6537008641490280549L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;

	public LANGUAGE_LEVEL() {
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	@Override
	public String toString() {
		return levelName;
	}
}
