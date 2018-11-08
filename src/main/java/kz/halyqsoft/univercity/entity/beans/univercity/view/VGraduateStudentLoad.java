package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Omarbek
 * @created Mar 27, 2018 9:31:17 AM
 */
public class VGraduateStudentLoad extends AbstractEntity {

	private static final long serialVersionUID = -6617074962906165812L;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String levelName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 3)
	private int studentCount;
	
	public VGraduateStudentLoad() {
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}
}
