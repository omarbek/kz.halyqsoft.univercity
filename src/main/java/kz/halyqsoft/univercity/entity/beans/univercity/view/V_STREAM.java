package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STREAM_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
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

    @FieldInfo(type = EFieldType.TEXT)
    @Column(name = "STREAM_NAME", nullable = false)
    private String streamName;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID", nullable = false)})
    private SEMESTER_PERIOD semesterPeriod;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    @Column(name = "PERIOD_NAME", nullable = false)
    private String periodName;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID", nullable = false)})
    private LANGUAGE language;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    @Column(name = "LANG_NAME", nullable = false)
    private String langName;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STREAM_TYPE_ID", referencedColumnName = "ID", nullable = false)})
    private STREAM_TYPE streamType;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "STREAM_TYPE_NAME", nullable = false)
    private String streamTypeName;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID", nullable = false)})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    @Column(name = "SUBJECT_NAME_KZ", nullable = false)
    private String subjectNameKz;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    @Column(name = "SUBJECT_NAME_RU", nullable = false)
    private String subjectNameRu;

    @FieldInfo(type = EFieldType.INTEGER, order = 7, readOnlyFixed = true)
    @Column(name = "STUDENT_COUNT")
    private Integer studentCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 8, readOnlyFixed = true)
    @Column(name = "GROUP_COUNT")
    private Integer groupCount;

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public SEMESTER_PERIOD getSemesterPeriod() {
        return semesterPeriod;
    }

    public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
        this.semesterPeriod = semesterPeriod;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public LANGUAGE getLanguage() {
        return language;
    }

    public void setLanguage(LANGUAGE language) {
        this.language = language;
    }

    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }

    public STREAM_TYPE getStreamType() {
        return streamType;
    }

    public void setStreamType(STREAM_TYPE streamType) {
        this.streamType = streamType;
    }

    public String getStreamTypeName() {
        return streamTypeName;
    }

    public void setStreamTypeName(String streamTypeName) {
        this.streamTypeName = streamTypeName;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public String getSubjectNameKz() {
        return subjectNameKz;
    }

    public void setSubjectNameKz(String subjectNameKz) {
        this.subjectNameKz = subjectNameKz;
    }

    public String getSubjectNameRu() {
        return subjectNameRu;
    }

    public void setSubjectNameRu(String subjectNameRu) {
        this.subjectNameRu = subjectNameRu;
    }
}
