package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class AUTHORIZED_SIGN_TRANSCRIPT extends AbstractEntity {

	private static final long serialVersionUID = 7409856958999178825L;

	@FieldInfo(type = EFieldType.TEXT, order = 1)
	@Column(name = "LAST_NAME_RU")
	private String lastNameRu;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "LAST_NAME_KZ")
	private String lastNameKz;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3)
	@Column(name = "LAST_NAME_EN")
	private String lastNameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4)
	@Column(name = "INITIALS_RU")
	private String initialsRu;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5)
	@Column(name = "INITIALS_KZ")
	private String initialsKz;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6)
	@Column(name = "INITIALS_EN")
	private String initialsEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7)
	@Column(name = "POSITION_RU")
	private String positionRu;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8)
	@Column(name = "POSITION_KZ")
	private String positionKz;
	
	@FieldInfo(type = EFieldType.TEXT, order = 9)
	@Column(name = "POSITION_EN")
	private String positionEn;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 10)
	@Column(name = "MAIN_FEATURE", nullable = false)
    private boolean mainFeature;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 11)
	@Column(name = "FIRST_SIGNATURE", nullable = false)
    private boolean firstSignature;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 12)
	@Column(name = "SECOND_SIGNATURE", nullable = false)
    private boolean secondSegnature;

	public String getLastNameRu() {
		return lastNameRu;
	}

	public void setLastNameRu(String lastNameRu) {
		this.lastNameRu = lastNameRu;
	}

	public String getLastNameKz() {
		return lastNameKz;
	}

	public void setLastNameKz(String lastNameKz) {
		this.lastNameKz = lastNameKz;
	}

	public String getLastNameEn() {
		return lastNameEn;
	}

	public void setLastNameEn(String lastNameEn) {
		this.lastNameEn = lastNameEn;
	}

	public String getInitialsRu() {
		return initialsRu;
	}

	public void setInitialsRu(String initialsRu) {
		this.initialsRu = initialsRu;
	}

	public String getInitialsKz() {
		return initialsKz;
	}

	public void setInitialsKz(String initialsKz) {
		this.initialsKz = initialsKz;
	}

	public String getInitialsEn() {
		return initialsEn;
	}

	public void setInitialsEn(String initialsEn) {
		this.initialsEn = initialsEn;
	}

	public String getPositionRu() {
		return positionRu;
	}

	public void setPositionRu(String positionRu) {
		this.positionRu = positionRu;
	}

	public String getPositionKz() {
		return positionKz;
	}

	public void setPositionKz(String positionKz) {
		this.positionKz = positionKz;
	}

	public String getPositionEn() {
		return positionEn;
	}

	public void setPositionEn(String positionEn) {
		this.positionEn = positionEn;
	}

	public boolean isMainFeature() {
		return mainFeature;
	}

	public void setMainFeature(boolean mainFeature) {
		this.mainFeature = mainFeature;
	}

	public boolean isFirstSignature() {
		return firstSignature;
	}

	public void setFirstSignature(boolean firstSignature) {
		this.firstSignature = firstSignature;
	}

	public boolean isSecondSegnature() {
		return secondSegnature;
	}

	public void setSecondSegnature(boolean secondSegnature) {
		this.secondSegnature = secondSegnature;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
