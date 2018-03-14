package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Feb 24, 2016 2:52:35 PM
 */
@Entity
public class ELECTIVE_SUBJECT_LABEL extends AbstractEntity {

	private static final long serialVersionUID = -8992470488952873999L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1, fieldWidth = 500)
	@Column(name = "NAME_KZ", nullable = false)
	private String nameKZ;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 2, fieldWidth = 500)
	@Column(name = "NAME_EN", nullable = false)
	private String nameEN;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, fieldWidth = 500)
	@Column(name = "NAME_RU", nullable = false)
	private String nameRU;
	
	public ELECTIVE_SUBJECT_LABEL() {
	}

	public String getNameKZ() {
		return nameKZ;
	}

	public void setNameKZ(String nameKZ) {
		this.nameKZ = nameKZ;
	}

	public String getNameEN() {
		return nameEN;
	}

	public void setNameEN(String nameEN) {
		this.nameEN = nameEN;
	}

	public String getNameRU() {
		return nameRU;
	}

	public void setNameRU(String nameRU) {
		this.nameRU = nameRU;
	}

	@Override
	public String toString() {
		return nameRU;
	}
}
