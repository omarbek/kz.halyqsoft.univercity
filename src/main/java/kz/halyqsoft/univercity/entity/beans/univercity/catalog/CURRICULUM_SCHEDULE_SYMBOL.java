package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Mar 1, 2016 10:19:35 AM
 */
@Entity
public class CURRICULUM_SCHEDULE_SYMBOL extends AbstractEntity {

	private static final long serialVersionUID = 1486546444283046734L;

	@FieldInfo(type = EFieldType.TEXT)
	@Column(name = "SYMBOL", nullable = false)
	private String symbol;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "DESCR", nullable = false)
	private String descr;
	
	public CURRICULUM_SCHEDULE_SYMBOL() {
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	@Override
	public String toString() {
		return symbol + " - " + descr;
	}
}
