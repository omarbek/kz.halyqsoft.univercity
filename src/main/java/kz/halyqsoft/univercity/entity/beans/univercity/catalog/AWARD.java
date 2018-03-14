package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 10:30:44 AM
 */
@Entity
public class AWARD extends AbstractEntity {

	private static final long serialVersionUID = 3284500069342008188L;

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "AWARD_NAME", nullable = false)
	private String awardName;

	public AWARD() {
	}

	public String getAwardName() {
		return awardName;
	}

	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

	@Override
	public String toString() {
		return awardName;
	}
}
