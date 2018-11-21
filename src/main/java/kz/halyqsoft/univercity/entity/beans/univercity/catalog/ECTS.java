package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Mar 14, 2018 11:51:39 AM
 */
@Entity
public class ECTS extends AbstractEntity {

	private static final long serialVersionUID = -4799345968646328286L;
	
	@Column(name = "ECTS", nullable = false)
	private int ects;

	public ECTS() {
	}

	public int getEcts() {
		return ects;
	}

	public void setEcts(int ects) {
		this.ects = ects;
	}

	@Override
	public String toString() {
		return String.valueOf(ects);
	}
}
