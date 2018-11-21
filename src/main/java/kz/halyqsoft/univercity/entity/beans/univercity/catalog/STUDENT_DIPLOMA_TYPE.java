package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.ID;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created on 02.05.2018
 */
@Entity
public class STUDENT_DIPLOMA_TYPE extends AbstractTypeEntity {
    public static final ID FULL_TIME = ID.valueOf(1);
    public static final ID EVENING_TIME = ID.valueOf(3);
    public static final ID FULL_TIME_AFTER_COLLEGE = ID.valueOf(5);
    public static final ID EXTRAMURAL_AFTER_COLLEGE = ID.valueOf(6);
    public static final ID EXTRAMURAL_SECOND_EDUCATION = ID.valueOf(7);
}
