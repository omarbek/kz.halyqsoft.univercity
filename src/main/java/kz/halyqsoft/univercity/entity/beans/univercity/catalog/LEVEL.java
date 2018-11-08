package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 11:00:02 AM
 */
@Entity
public class LEVEL extends AbstractEntity {

    public static final ID BACHELOR = ID.valueOf(1);
    public static final ID MASTER = ID.valueOf(2);
    public static final ID DOCTOR = ID.valueOf(3);

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
    @Column(name = "LEVEL_NAME", nullable = false)
    private String levelName;

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Override
    public String toString() {
        return levelName;
    }
}
