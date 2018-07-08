package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class DOCUMENT_IMPORTANCE extends AbstractEntity{

    public static int IMPORTANCE_1 = 1;
    public static int IMPORTANCE_2 = 2;
    public static int IMPORTANCE_3 = 3;
    public static int IMPORTANCE_4 = 4;
    public static int IMPORTANCE_5 = 5;

    @FieldInfo(type= EFieldType.INTEGER , order = 1)
    @Column(name = "importance_value", nullable = true )
    private Integer importanceValue;

    public Integer getImportanceValue() {
        return importanceValue;
    }

    public void setImportanceValue(Integer importanceValue) {
        this.importanceValue = importanceValue;
    }

    @Override
    public String toString() {
        return importanceValue + "";
    }
}
