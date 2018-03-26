package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.GRANT_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Nov 13, 2015 5:10:09 PM
 */
@Entity
@DiscriminatorValue(value = "8")
public class GRANT_DOC extends USER_DOCUMENT {

	private static final long serialVersionUID = -385714717879570700L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GRANT_TYPE_ID", referencedColumnName = "ID")})
    private GRANT_TYPE grantType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 9, order = 6)
	@Column(name = "ICT", nullable = false)
	private String ict;

	public GRANT_DOC() {
	}

	public GRANT_TYPE getGrantType() {
		return grantType;
	}

	public void setGrantType(GRANT_TYPE grantType) {
		this.grantType = grantType;
	}

	public String getIct() {
		return ict;
	}

	public void setIct(String ict) {
		this.ict = ict;
	}
}
