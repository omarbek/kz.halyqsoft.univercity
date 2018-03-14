package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.UNT_CERTIFICATE;
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
 * @created Nov 23, 2015 10:57:21 AM
 */
@Entity
public class V_UNT_CERT_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -8938204232042162472L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "UNT_CERTIFICATE_ID", referencedColumnName = "ID")})
    private UNT_CERTIFICATE untCertificate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "UNT_SUBJECT_ID", referencedColumnName = "ID")})
    private UNT_SUBJECT untSubject;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = false, inView = false)
	@Column(name = "UNT_SUBJECT_NAME", nullable = false)
	private String untSubjectName;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 99, order = 4)
	@Column(name = "RATE", nullable = false)
	private Integer rate;

	public V_UNT_CERT_SUBJECT() {
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

	public String getUntSubjectName() {
		return untSubjectName;
	}

	public void setUntSubjectName(String untSubjectName) {
		this.untSubjectName = untSubjectName;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}
}
