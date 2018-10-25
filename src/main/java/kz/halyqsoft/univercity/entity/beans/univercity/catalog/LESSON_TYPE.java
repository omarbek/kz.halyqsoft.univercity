package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Apr 27, 2016 2:05:13 PM
 */
@Entity
public class LESSON_TYPE extends AbstractTypeEntity {

    public static final ID LECTURE_ID = ID.valueOf(1);
    public static final ID THEORY_ID = ID.valueOf(2);
    public static final ID PRACTICE_ID = ID.valueOf(3);

    @FieldInfo(type = EFieldType.TEXT, max = 4, order = 2)
    @Column(name = "TYPE_SHORT_NAME", nullable = false)
    private String typeShortName;

    public String getTypeShortName() {
        return typeShortName;
    }

    public void setTypeShortName(String typeShortName) {
        this.typeShortName = typeShortName;
    }

    @Override
    public String toString() {
        return typeShortName;
    }
}
