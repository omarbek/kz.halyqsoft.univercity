package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Nov 17, 2016 10:54:49 AM
 */
@Entity
public class V_TEACHER_SUBJECT_SELECT extends AbstractEntity {

	private static final long serialVersionUID = 5264722790789817373L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "TEACHER_FIO")
	private String teacherFIO;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5)
	@Column(name = "GROUP_LEC_COUNT", nullable = false)
    private int groupLecCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 7)
	@Column(name = "GROUP_LAB_COUNT", nullable = false)
	private int groupLabCount;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 9)
	@Column(name = "GROUP_PRAC_COUNT", nullable = false)
	private int groupPracCount;
	
	@Column(name = "LOAD_PER_HOURS", nullable = false)
    private boolean loadPerHours;
	
	public V_TEACHER_SUBJECT_SELECT() {
	}
	
	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public String getTeacherFIO() {
		return teacherFIO;
	}

	public void setTeacherFIO(String teacherFIO) {
		this.teacherFIO = teacherFIO;
	}

	public int getGroupLecCount() {
		return groupLecCount;
	}

	public void setGroupLecCount(int groupLecCount) {
		this.groupLecCount = groupLecCount;
	}

	public int getGroupLabCount() {
		return groupLabCount;
	}

	public void setGroupLabCount(int groupLabCount) {
		this.groupLabCount = groupLabCount;
	}

	public int getGroupPracCount() {
		return groupPracCount;
	}

	public void setGroupPracCount(int groupPracCount) {
		this.groupPracCount = groupPracCount;
	}

	public boolean isLoadPerHours() {
		return loadPerHours;
	}

	public void setLoadPerHours(boolean loadPerHours) {
		this.loadPerHours = loadPerHours;
	}

	@Override
	public String toString() {
		return teacherFIO;
	}
}
