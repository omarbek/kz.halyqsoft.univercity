package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MILITARY_DOC_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MILITARY_STATUS;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Nov 13, 2015 2:41:15 PM
 */
@Entity
@DiscriminatorValue(value = "2")
public class MILITARY_DOC extends USER_DOCUMENT {

	private static final long serialVersionUID = 520645616502639767L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "MILITARY_DOC_TYPE_ID", referencedColumnName = "ID")})
    private MILITARY_DOC_TYPE militaryDocType;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "MILITARY_STATUS_ID", referencedColumnName = "ID")})
    private MILITARY_STATUS militaryStatus;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 7)
	@Column(name = "ISSUER_NAME", nullable = false)
	private String issuerName;

	public MILITARY_DOC() {
	}

	public MILITARY_DOC_TYPE getMilitaryDocType() {
		return militaryDocType;
	}

	public void setMilitaryDocType(MILITARY_DOC_TYPE militaryDocType) {
		this.militaryDocType = militaryDocType;
	}

	public MILITARY_STATUS getMilitaryStatus() {
		return militaryStatus;
	}

	public void setMilitaryStatus(MILITARY_STATUS militaryStatus) {
		this.militaryStatus = militaryStatus;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
}
