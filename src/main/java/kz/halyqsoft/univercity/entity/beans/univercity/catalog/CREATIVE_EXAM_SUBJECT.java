package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created on 23.04.2018
 */
@Entity
public class CREATIVE_EXAM_SUBJECT extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
    @Column(name = "SUBJECT_NAME", nullable = false)
    private String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return subjectName;
    }
}
