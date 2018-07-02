package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Apr 26, 2016 10:25:15 AM
 */
@Entity
public class SEMESTER_PERIOD extends AbstractEntity {

    public static final ID FALL_ID = ID.valueOf(1);
    public static final ID SPRING_ID = ID.valueOf(2);

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    @Column(name = "PERIOD_NAME", nullable = false)
    private String periodName;

    public SEMESTER_PERIOD() {
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    @Override
    public String toString() {
        return periodName;
    }
}
