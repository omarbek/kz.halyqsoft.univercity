package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Feb 17, 2016 3:06:57 PM
 */
@Entity
public class STUDY_YEAR extends AbstractEntity {

	public static final ID FIRST_STUDY_YEAR = ID.valueOf(1);
	public static final ID SECOND_STUDY_YEAR = ID.valueOf(2);
	public static final ID THIRD_STUDY_YEAR = ID.valueOf(3);
	public static final ID FOURTH_STUDY_YEAR = ID.valueOf(4);

	private static final long serialVersionUID = -1807787751619446758L;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 4, order = 1)
	@Column(name = "STUDY_YEAR", nullable = false)
	private Integer studyYear;
	
	public STUDY_YEAR() {
	}

	public Integer getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(Integer studyYear) {
		this.studyYear = studyYear;
	}

	@Override
	public String toString() {
		return studyYear.toString();
	}
}
