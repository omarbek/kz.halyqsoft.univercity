package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 11:00:02 AM
 */
@Entity
public class LEVEL extends AbstractEntity {

	private static final long serialVersionUID = 9003319730846780339L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;

	public LEVEL() {
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
