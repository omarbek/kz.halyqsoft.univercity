package kz.halyqsoft.univercity.modules.catalog;

import org.r3a.common.entity.Entity;

import java.util.Comparator;

/**
 * @author Omarbek
 * @created Nov 4, 2015 10:46:36 AM
 */
public final class CatalogEntityComparator implements Comparator<Entity> {

	@Override
	public int compare(Entity ce1, Entity ce2) {
		return ce1.toString().compareTo(ce2.toString());
	}
}
