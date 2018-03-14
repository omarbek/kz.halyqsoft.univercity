package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Oct 22, 2016 1:42:07 PM
 */
@Entity
public class ACADEMIC_CALENDAR_ITEM extends AbstractEntity {

	private static final long serialVersionUID = 4004720332675119342L;

	@FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FACULTY_ID", referencedColumnName = "ID")})
    private ACADEMIC_CALENDAR_FACULTY faculty;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 3)
	@Column(name = "ITEM_NAME", nullable = false)
	private String itemName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 4)
	@Column(name = "ITEM_TYPE", nullable = false)
	private String itemType;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public ACADEMIC_CALENDAR_ITEM() {
	}

	public ACADEMIC_CALENDAR_FACULTY getFaculty() {
		return faculty;
	}

	public void setFaculty(ACADEMIC_CALENDAR_FACULTY faculty) {
		this.faculty = faculty;
	}

	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return itemName;
	}
}
