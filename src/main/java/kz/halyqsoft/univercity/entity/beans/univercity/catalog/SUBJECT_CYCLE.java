package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 23, 2015 2:24:56 PM
 */
@Entity
public class SUBJECT_CYCLE extends AbstractEntity {

	private static final long serialVersionUID = 2743369226969076792L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "CYCLE_NAME", nullable = false)
	private String cycleName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 2, order = 2)
	@Column(name = "CYCLE_SHORT_NAME", nullable = false)
	private String cycleShortName;
	
	public SUBJECT_CYCLE() {
	}

	public String getCycleName() {
		return cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}

	public String getCycleShortName() {
		return cycleShortName;
	}

	public void setCycleShortName(String cycleShortName) {
		this.cycleShortName = cycleShortName;
	}

	@Override
	public String toString() {
		return cycleShortName;
	}
}
