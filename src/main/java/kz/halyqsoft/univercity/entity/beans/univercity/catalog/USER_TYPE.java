package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.ID;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:28:30 PM
 */
@Entity
public class USER_TYPE extends AbstractTypeEntity {

	public static final ID EMPLOYEE_ID = ID.valueOf(1);

	public static final ID STUDENT_ID = ID.valueOf(2);
}
