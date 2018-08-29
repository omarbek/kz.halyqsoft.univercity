package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;


@Entity
public class NON_ADMISSION_CAUSE extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT)
	@Column(name = "name", nullable = false)
	private String name;

	public NON_ADMISSION_CAUSE() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
