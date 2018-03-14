package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:16:17 PM
 */
@Entity
public class SEX extends AbstractEntity {

	private static final long serialVersionUID = 6847769034853690385L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "SEX_NAME", nullable = false)
	private String sexName;

	public SEX() {
	}

	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	@Override
	public String toString() {
		return sexName;
	}
}
