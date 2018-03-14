package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 23, 2015 4:49:32 PM
 */
@Entity
public class CREDITABILITY extends AbstractEntity {

	private static final long serialVersionUID = -1472737135302458773L;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 6)
	@Column(name = "CREDIT", nullable = false)
	private Integer credit;
	
	public CREDITABILITY() {
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	@Override
	public String toString() {
		return String.valueOf(credit);
	}
}
