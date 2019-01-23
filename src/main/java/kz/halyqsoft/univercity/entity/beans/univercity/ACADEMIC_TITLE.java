package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
public class ACADEMIC_TITLE extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT)
    @Column(name = "TITLE_NAME")
    private String titleName;

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public ACADEMIC_TITLE() {
    }

    @Override
    public String toString() {
        return  titleName ;
    }
}
