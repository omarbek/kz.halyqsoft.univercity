package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Apr 27, 2016 2:05:13 PM
 */
@Entity
public class LESSON_TYPE extends AbstractTypeEntity {

	private static final long serialVersionUID = 7789577289166763610L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 4, order = 2)
	@Column(name = "TYPE_SHORT_NAME", nullable = false)
	private String typeShortName;
	
	public LESSON_TYPE() {
	}

	public String getTypeShortName() {
		return typeShortName;
	}

	public void setTypeShortName(String typeShortName) {
		this.typeShortName = typeShortName;
	}

	@Override
	public String toString() {
		return typeShortName;
	}
}
