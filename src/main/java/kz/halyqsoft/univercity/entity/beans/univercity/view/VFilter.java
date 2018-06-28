package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.List;

/**
 * @author Omarbek
 * @created 26.06.2018
 */
public final class VFilter extends AbstractEntity {

    private SUBJECT subject;
    private List<GROUPS> groups;
    private int lectures;
    private int practices;
    private V_EMPLOYEE teacher;
    private int numberOfStudents;

    public VFilter(SUBJECT subject, List<GROUPS> groups, int lectures, int practices, V_EMPLOYEE teacher,
                   int numberOfStudents) {
        this.subject = subject;
        this.groups = groups;
        this.lectures = lectures;
        this.practices = practices;
        this.teacher = teacher;
        this.numberOfStudents = numberOfStudents;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public List<GROUPS> getGroups() {
        return groups;
    }

    public void setGroups(List<GROUPS> groups) {
        this.groups = groups;
    }

    public int getLectures() {
        return lectures;
    }

    public void setLectures(int lectures) {
        this.lectures = lectures;
    }

    public int getPractices() {
        return practices;
    }

    public void setPractices(int practices) {
        this.practices = practices;
    }

    public V_EMPLOYEE getTeacher() {
        return teacher;
    }

    public void setTeacher(V_EMPLOYEE teacher) {
        this.teacher = teacher;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }
}
