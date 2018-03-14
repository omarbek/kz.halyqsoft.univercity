package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 23, 2015 5:38:55 PM
 */
@Entity
public class STUDY_DIRECT extends AbstractEntity {

	private static final long serialVersionUID = -4967328751941268599L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "DIRECT_NAME", nullable = false)
	private String directName;
	
	@FieldInfo(type = EFieldType.TEXT, min = 3, max = 3, order = 2)
	@Column(name = "CODE", nullable = false)
	private String code;
	
	public STUDY_DIRECT() {
	}

	public String getDirectName() {
		return directName;
	}

	public void setDirectName(String directName) {
		this.directName = directName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return directName;
	}
}
