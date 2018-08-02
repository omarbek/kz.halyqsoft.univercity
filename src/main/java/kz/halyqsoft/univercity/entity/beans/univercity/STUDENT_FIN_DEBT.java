package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Apr 3, 2017 3:03:25 PM
 */
@Entity
public class STUDENT_FIN_DEBT extends AbstractEntity {

	private static final long serialVersionUID = -2348574322994827920L;

	@FieldInfo(type = EFieldType.FK_COMBO)
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false) })
	private V_STUDENT student;

	@FieldInfo(type = EFieldType.DOUBLE, order = 2)
	@Column(name = "DEBT_SUM", nullable = false)
	private Double debtSum;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 3,required = false)
	@Column(name = "RETAKE", nullable = false)
	private boolean retake;

	@Column(name = "CREATED", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	public STUDENT_FIN_DEBT() {
	}

	public V_STUDENT getStudent() {
		return student;
	}

	public void setStudent(V_STUDENT student) {
		this.student = student;
	}

	public Double getDebtSum() {
		return debtSum;
	}

	public void setDebtSum(Double debtSum) {
		this.debtSum = debtSum;
	}

	public boolean isRetake() {
		return retake;
	}

	public void setRetake(boolean retake) {
		this.retake = retake;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
