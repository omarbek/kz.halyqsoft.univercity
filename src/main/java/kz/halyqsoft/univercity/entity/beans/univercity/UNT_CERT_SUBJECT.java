package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.UNT_SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Nov 23, 2015 9:28:29 AM
 */
@Entity
public class UNT_CERT_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -7307992547767242754L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "UNT_CERTIFICATE_ID", referencedColumnName = "ID")})
    private UNT_CERTIFICATE untCertificate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "UNT_SUBJECT_ID", referencedColumnName = "ID")})
    private UNT_SUBJECT untSubject;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 99, order = 3)
	@Column(name = "RATE", nullable = false)
	private Integer rate;

	public UNT_CERT_SUBJECT() {
	}

	public UNT_CERTIFICATE getUntCertificate() {
		return untCertificate;
	}

	public void setUntCertificate(UNT_CERTIFICATE untCertificate) {
		this.untCertificate = untCertificate;
	}

	public UNT_SUBJECT getUntSubject() {
		return untSubject;
	}

	public void setUntSubject(UNT_SUBJECT untSubject) {
		this.untSubject = untSubject;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}
}
