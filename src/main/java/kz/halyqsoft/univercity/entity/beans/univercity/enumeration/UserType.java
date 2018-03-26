package kz.halyqsoft.univercity.entity.beans.univercity.enumeration;

/**
 * @author Omarbek
 * @created 11.05.2017.
 */
public enum UserType {

	EMPLOYEE,
	STUDENT;

	public static final String EMPLOYEE_INDEX = "1";

	public static final String STUDENT_INDEX = "2";

	public static UserType getUserTypeByIndex(String index) {
		switch (index) {
			case EMPLOYEE_INDEX:
				return EMPLOYEE;
			case STUDENT_INDEX:
				return STUDENT;
		}
		throw new IllegalArgumentException("Неверное значение индекса");
	}
}
