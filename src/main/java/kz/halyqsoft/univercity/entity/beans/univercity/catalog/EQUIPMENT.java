package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 22, 2015 5:09:31 PM
 */
@Entity
public class EQUIPMENT extends AbstractEntity {

	private static final long serialVersionUID = -1391629020674877915L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 1)
	@Column(name = "EQUIPMENT_NAME", nullable = false)
	private String equipmentName;
	
	public EQUIPMENT() {
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	@Override
	public String toString() {
		return equipmentName;
	}
}
