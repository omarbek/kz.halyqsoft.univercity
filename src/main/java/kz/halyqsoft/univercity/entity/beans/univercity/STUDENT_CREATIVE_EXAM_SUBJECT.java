package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREATIVE_EXAM_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.UNT_SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 23.04.2018
 */
@Entity
public class STUDENT_CREATIVE_EXAM_SUBJECT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_CREATIVE_EXAM_ID", referencedColumnName = "ID")})
    private STUDENT_CREATIVE_EXAM studentCreativeExam;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREATIVE_EXAM_SUBJECT_ID", referencedColumnName = "ID")})
    private CREATIVE_EXAM_SUBJECT creativeExamSubject;

    @FieldInfo(type = EFieldType.INTEGER, max = 99, order = 3)
    @Column(name = "RATE", nullable = false)
    private Integer rate;

    public STUDENT_CREATIVE_EXAM getStudentCreativeExam() {
        return studentCreativeExam;
    }

    public void setStudentCreativeExam(STUDENT_CREATIVE_EXAM studentCreativeExam) {
        this.studentCreativeExam = studentCreativeExam;
    }

    public CREATIVE_EXAM_SUBJECT getCreativeExamSubject() {
        return creativeExamSubject;
    }

    public void setCreativeExamSubject(CREATIVE_EXAM_SUBJECT creativeExamSubject) {
        this.creativeExamSubject = creativeExamSubject;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}
