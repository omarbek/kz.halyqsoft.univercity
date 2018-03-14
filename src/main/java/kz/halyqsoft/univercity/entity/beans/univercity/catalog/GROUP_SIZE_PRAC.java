package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 24, 2015 1:29:27 PM
 */
@Entity
public class GROUP_SIZE_PRAC extends AbstractEntity {

	private static final long serialVersionUID = -1302782888638098017L;

	@FieldInfo(type = EFieldType.INTEGER, min = 5, max = 25, order = 1)
	@Column(name = "FROM_SIZE", nullable = false)
	private Integer fromSize;
	
	@FieldInfo(type = EFieldType.INTEGER, min = 10, max = 30, order = 2)
	@Column(name = "TO_SIZE", nullable = false)
	private Integer toSize;
	
	@FieldInfo(type = EFieldType.TEXT, max = 5, order = 3)
	@Column(name = "SIZE", nullable = false)
	private String size;
	
	public GROUP_SIZE_PRAC() {
	}

	public Integer getFromSize() {
		return fromSize;
	}

	public void setFromSize(Integer fromSize) {
		this.fromSize = fromSize;
	}

	public Integer getToSize() {
		return toSize;
	}

	public void setToSize(Integer toSize) {
		this.toSize = toSize;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return size;
	}
}
