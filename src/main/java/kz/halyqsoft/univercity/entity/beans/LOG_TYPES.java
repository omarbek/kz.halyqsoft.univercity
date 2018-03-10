package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created 03.08.2011 15:07:24
 */
@Entity
public class LOG_TYPES extends AbstractEntity {

    @FieldInfo(type = EFieldType.LOCALIZED_TEXT, max = 64)
    @Column(name = "NAME", nullable = false, length = 64)
    private String name;

    public LOG_TYPES() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
