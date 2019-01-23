package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Assylkhan
 * on 2019-01-17
 * @project kz.halyqsoft.univercity
 */
@Entity
public class CONSTANTS extends AbstractTypeEntity {

    public static final ID WITH_TEACHER = ID.valueOf(1);
    public static final ID RATING = ID.valueOf(2);
    public static final ID TICKET = ID.valueOf(3);
    public static final ID EXAM = ID.valueOf(4);
    public static final ID CONTROL = ID.valueOf(5);
    public static final ID COURSE = ID.valueOf(6);
    public static final ID DIPLOMA = ID.valueOf(7);
    public static final ID MEK = ID.valueOf(8);
    public static final ID PROTECT_DIPLOMA = ID.valueOf(9);

    @FieldInfo(type = EFieldType.DOUBLE, max = 12,order = 2)
    @Column(name = "value", nullable = false)
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
