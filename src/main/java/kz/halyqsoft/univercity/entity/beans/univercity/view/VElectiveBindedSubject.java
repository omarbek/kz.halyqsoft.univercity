package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VElectiveBindedSubject extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT , order=2)
    private String firstSubjectName;

    @FieldInfo(type = EFieldType.TEXT, order=3 )
    private String secondSubjectName;

    public String getFirstSubjectName() {
        return firstSubjectName;
    }

    public void setFirstSubjectName(String firstSubjectName) {
        this.firstSubjectName = firstSubjectName;
    }

    public String getSecondSubjectName() {
        return secondSubjectName;
    }

    public void setSecondSubjectName(String secondSubjectName) {
        this.secondSubjectName = secondSubjectName;
    }
}
