package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @@author Omarbek
 * @created Sep 11, 2016 9:12:08 PM
 */
@Entity
@DiscriminatorValue(value = "14")
public class GUARDIAN_COUNCIL_DECISION extends USER_DOCUMENT {

	private static final long serialVersionUID = 445272948574218742L;

	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "ISSUER_NAME")
	private String issuerName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 6)
	@Column(name = "DESCR")
	private String descr;
	
	public GUARDIAN_COUNCIL_DECISION() {
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
