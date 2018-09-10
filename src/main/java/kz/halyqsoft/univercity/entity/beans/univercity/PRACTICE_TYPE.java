package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractTypeEntity;
import org.r3a.common.entity.ID;

import javax.persistence.Entity;

@Entity
public class PRACTICE_TYPE extends AbstractTypeEntity {

    public static final ID PRODUCTION_ID = ID.valueOf(1);

    public static final ID EDUCATIONAL_ID = ID.valueOf(2);

    public static final ID PEDAGOGICAL_ID = ID.valueOf(3);

    public PRACTICE_TYPE() {
    }
}
