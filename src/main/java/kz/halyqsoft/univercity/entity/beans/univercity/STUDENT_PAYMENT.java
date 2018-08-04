package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Apr 3, 2017 3:14:15 PM
 */
@Entity
public class STUDENT_PAYMENT extends AbstractEntity {

	private static final long serialVersionUID = -7691848287100727089L;

	@FieldInfo(type = EFieldType.FK_COMBO)
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false) })
	private V_STUDENT student;

	@FieldInfo(type = EFieldType.DOUBLE, order = 2)
	@Column(name = "PAYMENT_SUM", nullable = false)
	private Double paymentSum;

	@Column(name = "CREATED", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	public STUDENT_PAYMENT() {
	}

	public V_STUDENT getStudent() {
		return student;
	}

	public void setStudent(V_STUDENT student) {
		this.student = student;
	}

	public Double getPaymentSum() {
		return paymentSum;
	}

	public void setPaymentSum(Double paymentSum) {
		this.paymentSum = paymentSum;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
