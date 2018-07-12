package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created 30.06.2018
 */
@Entity
public class V_STREAM extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO , order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID", nullable = false)})
    private SEMESTER_DATA semesterData;

    @FieldInfo(type = EFieldType.FK_COMBO , order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID", nullable = false)})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "STREAM_NAME", nullable = false)
    private String streamName;

    @FieldInfo(type = EFieldType.INTEGER, order = 5, readOnlyFixed = true)
    @Column(name = "STUDENT_COUNT")
    private Integer studentCount;

    public SEMESTER_DATA getSemesterData() {
        return semesterData;
    }

    public void setSemesterData(SEMESTER_DATA semesterData) {
        this.semesterData = semesterData;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}
