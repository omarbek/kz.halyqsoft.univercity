package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CONTRACT_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PAYMENT_TYPE;
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
 * @created Nov 13, 2015 5:41:08 PM
 */
@Entity
@DiscriminatorValue(value = "9")
public class STUDENT_CONTRACT extends USER_DOCUMENT {

	private static final long serialVersionUID = 4477146989679423353L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CONTRACT_TYPE_ID", referencedColumnName = "ID")})
    private CONTRACT_TYPE contractType;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PAYMENT_TYPE_ID", referencedColumnName = "ID")})
    private PAYMENT_TYPE paymentType;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 7, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "ID")})
    private ORGANIZATION organization;
	
	@Column(name = "OLD_ID")
	private String oldId;

	public STUDENT_CONTRACT() {
	}

	public CONTRACT_TYPE getContractType() {
		return contractType;
	}

	public void setContractType(CONTRACT_TYPE contractType) {
		this.contractType = contractType;
	}

	public PAYMENT_TYPE getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PAYMENT_TYPE paymentType) {
		this.paymentType = paymentType;
	}

	public ORGANIZATION getOrganization() {
		return organization;
	}

	public void setOrganization(ORGANIZATION organization) {
		this.organization = organization;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}
