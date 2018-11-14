package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:24:44 PM
 */
@Entity
public class UNIVERSITY extends AbstractEntity {

	private static final long serialVersionUID = 7695916141034416041L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "UNIVERSITY_NAME", nullable = false)
	private String universityName;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 2, required = false)
	@Column(name = "USE_DEFAULT", nullable = false)
    private boolean useDefault;

	public UNIVERSITY() {
	}

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	@Override
	public String toString() {
		return universityName;
	}

	public boolean isUseDefault() {
		return useDefault;
	}

	public void setUseDefault(boolean useDefault) {
		this.useDefault = useDefault;
	}
}
