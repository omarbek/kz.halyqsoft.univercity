package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

public class VCanceledLessons extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String subjectName;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String teacherFIO;

    @FieldInfo(type = EFieldType.DATE, order = 3)
    private Date lessonDate;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String beginDate;


    @FieldInfo(type = EFieldType.TEXT, order = 5)
    private String finishDate;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    private String cancelReason;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherFIO() {
        return teacherFIO;
    }

    public void setTeacherFIO(String teacherFIO) {
        this.teacherFIO = teacherFIO;
    }

    public Date getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(Date lessonDate) {
        this.lessonDate = lessonDate;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
