package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created Nov 16, 2015 9:43:38 AM
 */
@Entity
@DiscriminatorValue(value = "11")
public class PREEMPTIVE_RIGHT extends USER_DOCUMENT {

	private static final long serialVersionUID = 7219043992694039786L;

	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "ISSUER_NAME", nullable = false)
	private String issuerName;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 1024, order = 6, required = false)
	@Column(name = "DESCR")
	private String descr;

	public PREEMPTIVE_RIGHT() {
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
