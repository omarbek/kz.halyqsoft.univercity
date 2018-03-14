package kz.halyqsoft.univercity.modules.catalog;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.NATIONALITY;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Omarbek
 * @created on 13.03.2018
 */
public class CatalogList {
    public static List<Entity> getCatalogList() {
        Set<Entity> list = new TreeSet<>(new CatalogEntityComparator());

        CatalogEntity ce = new CatalogEntity();
        ce.setId(ID.valueOf(1));
        ce.setEntityClass(NATIONALITY.class);
        list.add(ce);

        return new ArrayList<>(list);
    }
}
