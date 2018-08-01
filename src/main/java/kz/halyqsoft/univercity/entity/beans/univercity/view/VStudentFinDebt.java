package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

/**
 * @author Dinassil Omarbek
 * @created Apr 18, 2017 9:19:47 AM
 */
public class VStudentFinDebt extends AbstractEntity {

	private static final long serialVersionUID = -2348574322994827920L;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String code;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String fio;

	@FieldInfo(type = EFieldType.DATETIME, order = 4)
	private Date created;

	@FieldInfo(type = EFieldType.DOUBLE, order = 5)
	private Double debtSum;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private String retake;

	public VStudentFinDebt() {
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Double getDebtSum() {
		return debtSum;
	}

	public void setDebtSum(Double debtSum) {
		this.debtSum = debtSum;
	}

	public String getRetake() {
		return retake;
	}

	public void setRetake(String retake) {
		this.retake = retake;
	}
}
