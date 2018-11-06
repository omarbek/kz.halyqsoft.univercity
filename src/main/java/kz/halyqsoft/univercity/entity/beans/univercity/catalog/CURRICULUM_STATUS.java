package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.ID;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Feb 18, 2016 9:53:27 AM
 */
@Entity
public class CURRICULUM_STATUS extends AbstractStatusEntity {

    public static final ID IN_CREATING = ID.valueOf(1);
    public static final ID IN_CONFORMING = ID.valueOf(2);
    public static final ID APPROVED = ID.valueOf(3);

}
