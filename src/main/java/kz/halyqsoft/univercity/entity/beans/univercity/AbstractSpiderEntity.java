package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import java.util.Date;

import javax.persistence.MappedSuperclass;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Dec 28, 2016 11:00:56 AM
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class AbstractSpiderEntity extends AbstractEntity {

	public abstract String getLastname();

	public abstract void setLastname(String lastname);

	public abstract String getFirstname();

	public abstract void setFirstname(String firstname);

	public abstract String getMiddlename();

	public abstract void setMiddlename(String middlename);
	
	public abstract String getFio();

	public abstract void setFio(String fio);
	
	public abstract Date getBirthDate();
	
	public abstract void setBirthDate(Date birthDate);
	
	public abstract String getCode();
	
	public abstract void setCode(String code);
	
	public abstract boolean isSavedToAD();

	public abstract void setSavedToAD(boolean savedToAD);
	
	public abstract boolean isSavedToSKD();

	public abstract void setSavedToSKD(boolean savedToSKD);
	
	public abstract boolean isSavedToLib();

	public abstract void setSavedToLib(boolean savedToLib);
	
	public abstract String getInfo();

	public abstract void setInfo(String info);
	
	public abstract String getLogin();

	public abstract void setLogin(String adLogin);
	
	public abstract String getEmail();

	public abstract void setEmail(String email);
	
	public abstract String getFilename();

	public abstract void setFilename(String filename);
	
	public abstract byte[] getPhoto();
	
	public abstract void setPhoto(byte[] photo);
}
