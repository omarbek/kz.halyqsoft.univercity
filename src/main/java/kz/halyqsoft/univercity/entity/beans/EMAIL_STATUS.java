package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created 15.04.2014 16:47:14
 */
@Entity
public class EMAIL_STATUS extends AbstractEntity {

    @FieldInfo(type = EFieldType.LOCALIZED_TEXT, max = 64)
    @Column(name = "NAME", nullable = false, length = 64)
    private String name;

    public EMAIL_STATUS() {
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
