package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SUBJECT_MODULE extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, max = 64)
    @Column(name = "MODULE_NAME", nullable = false)
    private String moduleName;

    @Column(name = "MODULE_SHORT_NAME", nullable = false)
    private String moduleShortName;

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

    public String getModuleShortName() {
        return moduleShortName;
    }

    public void setModuleShortName(String moduleShortName) {
        this.moduleShortName = moduleShortName;
    }
}