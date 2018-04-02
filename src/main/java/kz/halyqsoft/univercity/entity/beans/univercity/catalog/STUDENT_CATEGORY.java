package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.ID;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:22:04 PM
 */
@Entity
public class STUDENT_CATEGORY extends AbstractCategoryEntity {

	private static final long serialVersionUID = -7148997974563925858L;

	public static final ID ENROLLEE_ID = ID.valueOf(1);

	public static final ID LISTENER_ID = ID.valueOf(2);

	public static final ID STUDENT_ID = ID.valueOf(3);

	public STUDENT_CATEGORY() {
	}
}
