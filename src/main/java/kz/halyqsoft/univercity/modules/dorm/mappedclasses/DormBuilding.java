package kz.halyqsoft.univercity.modules.dorm.mappedclasses;

import org.r3a.common.entity.*;

/**
 * @author Dinassil Omarbek
 * @created 15.08.2017.
 */
public class DormBuilding implements Entity {

	public static final String SQL = 
			"SELECT " +
			"  dorm.ID, " +
			"  dorm.DORM_NAME, " +
			"  dorm.DORM_ADDRESS, " +
			"  COUNT(room.ID), " +
			"  COALESCE(SUM(( " +
			"        SELECT " +
			"          CASE " +
			"            WHEN COUNT(*) >= dormRoom.BED_COUNT THEN 1 ELSE 0 END " +
			"        FROM " +
			"          DORM_STUDENT dormStudent, " +
			"          DORM_ROOM dormRoom " +
			"        WHERE " +
			"          dormStudent.ROOM_ID = dormRoom.ID AND " +
			"          dormStudent.ROOM_ID = room.ID AND " +
			"          dormStudent.CHECK_OUT_DATE IS NULL AND " +
			"          dormStudent.DELETED = FALSE " +
			"        GROUP BY dormRoom.BED_COUNT " +
			"  )), 0), " +
			"  COALESCE(CAST(SUM(room.BED_COUNT) AS INT), 0), " +
			"  COALESCE(SUM(( " +
			"        SELECT " +
			"          COUNT(*) " +
			"        FROM " +
			"          DORM_STUDENT dormStudent " +
			"        WHERE " +
			"          dormStudent.ROOM_ID = room.ID AND " +
			"          dormStudent.CHECK_OUT_DATE IS NULL AND " +
			"          dormStudent.DELETED = FALSE " +
			"  )), 0) " +
			"FROM " +
			"  DORM dorm " +
			"   LEFT JOIN DORM_ROOM room ON dorm.ID = room.DORM_ID AND room.DELETED = FALSE " +
			"WHERE " +
			"  dorm.DELETED = FALSE " +
			"GROUP BY dorm.ID, dorm.DORM_NAME, dorm.DORM_ADDRESS " +
			"ORDER BY dorm.DORM_NAME";

	public static final String NAME_COLUMN = "name";

	public static final String ADDRESS_COLUMN = "address";

	public static final String ROOM_COUNT_COLUMN = "roomCount";

	public static final String BUSY_ROOM_COUNT_COLUMN = "busyRoomCount";

	public static final String BED_COUNT_COLUMN = "bedCount";

	public static final String BUSY_BED_COUNT_COLUMN = "busyBedCount";

	@FieldInfo(type = EFieldType.TEXT)
	private ID id;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private String name;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String address;

	@FieldInfo(type = EFieldType.INTEGER, order = 4)
	private int roomCount;

	@FieldInfo(type = EFieldType.INTEGER, order = 5)
	private int busyRoomCount;

	@FieldInfo(type = EFieldType.INTEGER, order = 6)
	private int bedCount;

	@FieldInfo(type = EFieldType.INTEGER, order = 7)
	private int busyBedCount;

	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

	@Override
	public String logString() throws Exception {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getRoomCount() {
		return roomCount;
	}

	public void setRoomCount(int roomCount) {
		this.roomCount = roomCount;
	}

	public int getBusyRoomCount() {
		return busyRoomCount;
	}

	public void setBusyRoomCount(int busyRoomCount) {
		this.busyRoomCount = busyRoomCount;
	}

	public int getBedCount() {
		return bedCount;
	}

	public void setBedCount(int bedCount) {
		this.bedCount = bedCount;
	}

	public int getBusyBedCount() {
		return busyBedCount;
	}

	public void setBusyBedCount(int busyBedCount) {
		this.busyBedCount = busyBedCount;
	}
}
