package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Sep 11, 2016 8:19:35 PM
 */
@Entity
@DiscriminatorValue(value = "13")
public class DEATH_CERTIFICATE extends USER_DOCUMENT {
	
	private static final long serialVersionUID = -2696653709492130179L;

	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "ISSUER_NAME")
	private String issuerName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 6)
	@Column(name = "DESCR")
	private String descr;

	public DEATH_CERTIFICATE() {
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
