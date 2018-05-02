package kz.halyqsoft.univercity.modules.catalog;

import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.tree.CommonTree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created Oct 29, 2015 4:30:39 PM
 */
public class CatalogEntity implements CommonTree<CatalogEntity> {

    private static final long serialVersionUID = 6214904027515063924L;

    private ID id;
    private Class<? extends Entity> entityClass;

    public CatalogEntity() {
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void setDeleted(boolean deleted) {
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public String logString() throws Exception {
        return null;
    }

    @Override
    public CatalogEntity getParent() {
        return null;
    }

    @Override
    public void setParent(CatalogEntity parent) {
    }

    @Override
    public List<CatalogEntity> getChildren() {
        return new ArrayList<CatalogEntity>();
    }

    @Override
    public String toString() {
        return CommonUtils.getUILocaleUtil().getEntityLabel(entityClass);
    }
}
