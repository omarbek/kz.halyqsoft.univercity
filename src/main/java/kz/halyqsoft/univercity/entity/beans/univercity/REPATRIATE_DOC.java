package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 13, 2015 4:47:52 PM
 */
@Entity
@DiscriminatorValue(value = "6")
public class REPATRIATE_DOC extends USER_DOCUMENT {

	private static final long serialVersionUID = 1927637195695607491L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "ISSUER_NAME", nullable = false)
	private String issuerName;

	public REPATRIATE_DOC() {
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
}
