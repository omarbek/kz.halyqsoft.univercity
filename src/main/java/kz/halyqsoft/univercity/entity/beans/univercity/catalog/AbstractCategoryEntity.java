package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:18:58 PM
 */
@MappedSuperclass
public abstract class AbstractCategoryEntity extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "CATEGORY_NAME", nullable = false)
	private String categoryName;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return categoryName;
	}
}
