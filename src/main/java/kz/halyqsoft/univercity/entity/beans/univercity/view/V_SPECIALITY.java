package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
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
 * @created Nov 5, 2015 10:42:27 AM
 */
@Entity
public class V_SPECIALITY extends AbstractEntity {

	private static final long serialVersionUID = -5242825826672044938L;

	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 1)
	@Column(name = "SPEC_NAME", nullable = false)
	private String specName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 10, order = 2, columnWidth = 80)
	@Column(name = "CODE", nullable = false)
	private String code;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 3, inEdit = false, inGrid = false, inView = false)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 4)
	@Column(name = "CHAIR_NAME", nullable = false)
	private String chairName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 5, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CHAIR_SHORT_NAME", nullable = false)
	private String chairShortName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 7, inGrid = false, inEdit = false, inView = false)
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 8, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;

	public V_SPECIALITY() {
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

	public String getChairName() {
		return chairName;
	}

	public void setChairName(String chairName) {
		this.chairName = chairName;
	}

	public String getChairShortName() {
		return chairShortName;
	}

	public void setChairShortName(String chairShortName) {
		this.chairShortName = chairShortName;
	}
	
	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public String toString() {
		return specName;
	}
}
