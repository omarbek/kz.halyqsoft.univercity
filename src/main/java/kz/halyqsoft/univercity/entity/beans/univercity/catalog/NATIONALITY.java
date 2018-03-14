package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 11:49:50 AM
 */
@Entity
public class NATIONALITY extends AbstractEntity {

	private static final long serialVersionUID = -8511099194990779029L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "NATION_NAME", nullable = false)
	private String nationName;

	public NATIONALITY() {
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	@Override
	public String toString() {
		return nationName;
	}
}
