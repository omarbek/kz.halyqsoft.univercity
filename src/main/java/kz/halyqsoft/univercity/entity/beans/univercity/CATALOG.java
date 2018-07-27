package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class CATALOG  extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, inGrid = true, inEdit = true, inView = true, order = 1)
    private String name;

    @FieldInfo(type = EFieldType.TEXT, inGrid = false, inEdit = false, inView = false, order = 2)
    private String value;

    @FieldInfo(type = EFieldType.TEXT, inGrid = true, inEdit = true, inView = true, order = 3)
    private String description;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inEdit = false, order = 4)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public CATALOG() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}