package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SKILL extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "SKILL_NAME", nullable = false)
	private String skillName;

	public SKILL() {
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	@Override
	public String toString() {
		return skillName;
	}
}
