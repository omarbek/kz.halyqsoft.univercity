package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Dinassil Omarbek
 * @created Apr 18, 2018 9:19:47 AM
 */
public class VStudentPayment extends AbstractEntity {

	private static final long serialVersionUID = -2348574322994827920L;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String code;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String fio;

	@FieldInfo(type = EFieldType.DOUBLE, order = 5)
	private Double paymentSum;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private String time;

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

	public Double getPaymentSum() {
		return paymentSum;
	}

	public void setPaymentSum(Double paymentSum) {
		this.paymentSum = paymentSum;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public VStudentPayment() {
	}
}
