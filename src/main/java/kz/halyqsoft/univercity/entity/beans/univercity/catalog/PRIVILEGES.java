package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PRIVILEGES extends AbstractTypeEntity {

    public static final ID IUPS = ID.valueOf(1);

    @FieldInfo(type = EFieldType.BOOLEAN, order = 2, required = false)
    @Column(name = "CAN_ACCESS", nullable = false)
    private boolean canAccess;

    public boolean isCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }
}
