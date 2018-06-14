package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SUBJECT_MODULE extends AbstractEntity {

    private static final long serialVersionUID = 9003319730846780339L;

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
    @Column(name = "MODULE_NAME", nullable = false)
    private String moduleName;

    public SUBJECT_MODULE() {
    }

    @Override
    public String toString() {
        return moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}