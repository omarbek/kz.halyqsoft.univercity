package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import javax.persistence.Entity;

@Entity
public class EDUCATION_MODULE_TYPE extends AbstractTypeEntity {

    @Override
    public String toString() {
        return getTypeName();
    }
}
