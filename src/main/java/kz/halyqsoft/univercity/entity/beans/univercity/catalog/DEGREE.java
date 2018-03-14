package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 10:38:03 AM
 */
@Entity
public class DEGREE extends AbstractEntity {

	private static final long serialVersionUID = 2533240250321509812L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "DEGREE_NAME", nullable = false)
	private String degreeName;

	public DEGREE() {
	}

	public String getDegreeName() {
		return degreeName;
	}

	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
	}

	@Override
	public String toString() {
		return degreeName;
	}
}
