package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
public class ABSENCE_CAUSE extends AbstractEntity {
	public static ID WORK_IN_FACT = ID.valueOf(4);
	public static ID DONT_COME = ID.valueOf(11);


	@FieldInfo(type = EFieldType.TEXT )
	@Column(name = "name", nullable = false)
	private String name;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "letter", nullable = false)
	private String letter;

	public ABSENCE_CAUSE() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	@Override
	public String toString() {
		return name;
	}
}
