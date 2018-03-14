package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @@author Omarbek
 * @created Sep 11, 2016 9:34:27 PM
 */
@Entity
@DiscriminatorValue(value = "15")
public class OTHER_DOCUMENT extends USER_DOCUMENT {

	private static final long serialVersionUID = -5185315065906630402L;

	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "ISSUER_NAME")
	private String issuerName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 6)
	@Column(name = "DESCR")
	private String descr;
	
	public OTHER_DOCUMENT() {
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
