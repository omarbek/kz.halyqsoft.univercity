package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PUBLICATION_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Dec 21, 2015 4:11:34 PM
 */
@Entity
@DiscriminatorValue(value = "1")
public class PUBLICATION extends EMPLOYEE_SCIENTIFIC {

	private static final long serialVersionUID = -2362400963750663676L;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PUBLICATION_TYPE_ID", referencedColumnName = "ID")})
    private PUBLICATION_TYPE publicationType;
	
	public PUBLICATION() {
	}

	public PUBLICATION_TYPE getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(PUBLICATION_TYPE publicationType) {
		this.publicationType = publicationType;
	}
}
