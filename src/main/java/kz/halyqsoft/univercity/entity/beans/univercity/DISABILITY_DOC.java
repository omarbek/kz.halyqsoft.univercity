package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created Nov 13, 2015 4:40:01 PM
 */
@Entity
@DiscriminatorValue(value = "5")
public class DISABILITY_DOC extends USER_DOCUMENT {

	private static final long serialVersionUID = 404396627645418858L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "ISSUER_NAME", nullable = false)
	private String issuerName;

	public DISABILITY_DOC() {
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
}
