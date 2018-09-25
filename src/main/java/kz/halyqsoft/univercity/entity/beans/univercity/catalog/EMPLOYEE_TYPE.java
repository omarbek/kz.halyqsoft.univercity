package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.ID;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 10:51:36 AM
 */
@Entity
public class EMPLOYEE_TYPE extends AbstractTypeEntity {

	private static final long serialVersionUID = -8460841093644869331L;

	public static ID ADMINISTRATIVE_EMPLOYEE_ID = ID.valueOf(1);
	public static ID TEACHER_ID = ID.valueOf(2);

	public EMPLOYEE_TYPE() {
	}
}
