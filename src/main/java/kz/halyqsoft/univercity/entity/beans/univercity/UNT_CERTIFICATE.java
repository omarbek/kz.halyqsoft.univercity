package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @@author Omarbek
 * @created Nov 13, 2015 10:16:38 AM
 */
@Entity
@DiscriminatorValue(value = "7")
public class UNT_CERTIFICATE extends USER_DOCUMENT {

	private static final long serialVersionUID = -1655237271567659251L;

	@FieldInfo(type = EFieldType.TEXT, max = 9, order = 5)
	@Column(name = "ICT")
	private String ict;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 100, order = 6, readOnlyFixed = true)
	@Column(name = "RATE")
	private Integer rate;

	public UNT_CERTIFICATE() {
	}

	public String getIct() {
		return ict;
	}

	public void setIct(String ict) {
		this.ict = ict;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}
}
