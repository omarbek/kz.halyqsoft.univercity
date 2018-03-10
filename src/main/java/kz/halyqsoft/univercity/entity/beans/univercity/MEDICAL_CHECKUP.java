package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MEDICAL_CHECKUP_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 13, 2015 4:11:16 PM
 */
@Entity
@DiscriminatorValue(value = "4")
public class MEDICAL_CHECKUP extends USER_DOCUMENT {

	private static final long serialVersionUID = -2350609911673317418L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHECKUP_TYPE_ID", referencedColumnName = "ID")})
    private MEDICAL_CHECKUP_TYPE checkupType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 6)
	@Column(name = "ISSUER_NAME", nullable = false)
	private String issuerName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 7, required = false)
	@Column(name = "ALLOW_DORM", nullable = true)
    private boolean allowDorm;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 8, required = false)
	@Column(name = "ALLOW_STUDY", nullable = true)
    private boolean allowStudy;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 9, required = false)
	@Column(name = "ALLOW_WORK", nullable = true)
    private boolean allowWork;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 256, order = 10, required = false)
	@Column(name = "DESCR", nullable = false)
	private String descr;

	public MEDICAL_CHECKUP() {
	}

	public MEDICAL_CHECKUP_TYPE getCheckupType() {
		return checkupType;
	}

	public void setCheckupType(MEDICAL_CHECKUP_TYPE checkupType) {
		this.checkupType = checkupType;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public boolean isAllowDorm() {
		return allowDorm;
	}

	public void setAllowDorm(boolean allowDorm) {
		this.allowDorm = allowDorm;
	}

	public boolean isAllowStudy() {
		return allowStudy;
	}

	public void setAllowStudy(boolean allowStudy) {
		this.allowStudy = allowStudy;
	}

	public boolean isAllowWork() {
		return allowWork;
	}

	public void setAllowWork(boolean allowWork) {
		this.allowWork = allowWork;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
