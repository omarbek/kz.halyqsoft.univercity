package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jun 29, 2016 12:13:43 PM
 */
@Entity
public class V_STUDENT_SUBJECT_SUM extends AbstractEntity {

	private static final long serialVersionUID = -8180010576810102039L;

	@Column(name = "SUBJECT_ID", nullable = false)
	private BigInteger subjectId;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 9)
	@Column(name = "STUDENT_COUNT", nullable = false)
	private Integer studentCount;
	
	public V_STUDENT_SUBJECT_SUM() {
	}
	
	public BigInteger getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(BigInteger subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Integer studentCount) {
		this.studentCount = studentCount;
	}
}
