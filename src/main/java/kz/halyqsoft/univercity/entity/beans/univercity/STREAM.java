package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class STREAM extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT)
    @Column(name = "NAME")
    private String name;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STREAM_TYPE_ID", referencedColumnName = "ID")})
    private STREAM_TYPE streamType;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID")})
    private LANGUAGE language;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    //    @FieldInfo(type = EFieldType.BOOLEAN, inGrid = false, inEdit = false, inView = false, order = 7 )
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public SEMESTER_PERIOD getSemesterPeriod() {
        return semesterPeriod;
    }

    public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
        this.semesterPeriod = semesterPeriod;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return name;
    }

    public STREAM_TYPE getStreamType() {
        return streamType;
    }

    public void setStreamType(STREAM_TYPE streamType) {
        this.streamType = streamType;
    }

    public LANGUAGE getLanguage() {
        return language;
    }

    public void setLanguage(LANGUAGE language) {
        this.language = language;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
