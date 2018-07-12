package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 11:58:57 AM
 */
@Entity
public class POST extends AbstractEntity {

	private static final long serialVersionUID = -5128853767511513443L;

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "POST_NAME", nullable = false)
	private String postName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 2, required = false)
	@Column(name = "STUDY_LOAD", nullable = false)
	private Integer studyLoad;

	@FieldInfo(type = EFieldType.INTEGER, order = 3)
	@Column(name = "PRIORITY", nullable = false)
	private Integer priority;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 4, required = false, columnWidth = 100)
	@Column(name = "TP", nullable = false)
    private boolean tp;//teacher post

	public POST() {
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}
	
	public Integer getStudyLoad() {
		return studyLoad;
	}

	public void setStudyLoad(Integer studyLoad) {
		this.studyLoad = studyLoad;
	}
	
	public boolean isTp() {
		return tp;
	}

	public void setTp(boolean tp) {
		this.tp = tp;
	}
	
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return postName;
	}
}
