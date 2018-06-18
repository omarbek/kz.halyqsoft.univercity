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
 * Created Nov 4, 2015 1:49:50 PM
 */
@Entity
public class SPECIALITY extends AbstractEntity {

	private static final long serialVersionUID = 5022298761693759949L;

	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 1)
	@Column(name = "SPEC_NAME", nullable = false)
	private String specName;

	@FieldInfo(type = EFieldType.TEXT, max = 10, order = 2, columnWidth = 80)
	@Column(name = "CODE", nullable = false)
	private String code;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 5, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public SPECIALITY() {
	}

	public String getSpecName() {
		return specName;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DEPARTMENT getDepartment() {
		return department;
	}

	public void setDepartment(DEPARTMENT department) {
		this.department = department;
	}
	
	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return specName + " - " + code;
	}
}
