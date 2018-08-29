package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.ID;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:23:17 PM
 */
@Entity
public class STUDENT_STATUS extends AbstractStatusEntity {

	private static final long serialVersionUID = 6238843988881450199L;
	public static final ID STUDYING_ID = ID.valueOf(1);
	public static final ID GRADUATED_ID = ID.valueOf(2);
	public static final ID DEDUCTED_ID = ID.valueOf(3);
	public static final ID CHANGED_FACULTY_OR_SPECIALITY_ID = ID.valueOf(4);
	public static final ID ACADEMIC_VACATION_ID = ID.valueOf(5);
	public static final ID CHANGED_TO_NEXT_YEAR_ID = ID.valueOf(6);
	public static final ID CHANGED_TO_ANOTHER_TYPE_OF_STUDY_ID = ID.valueOf(7);
	public static final ID CHANGED_TO_ANOTHER_LANGUAGE_ID = ID.valueOf(8);
	public static final ID RETAKE_ID = ID.valueOf(9);

	public STUDENT_STATUS() {
	}
}
