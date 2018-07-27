package kz.halyqsoft.univercity.modules.dorm.mappedclasses;

import org.r3a.common.entity.*;
import org.r3a.common.entity.Entity;

/**
 * @author Dinassil Omarbek
 * @created 16.08.2017.
 */
public class DormRoom implements Entity {

	public static final String SQL = "" +
			"SELECT " +
			"  room.ID, " +
			"  room.ROOM_NO, " +
			"  CAST(room.BED_COUNT AS INT), " +
			"  ( " +
			"    SELECT COUNT(*) " +
			"    FROM " +
			"      DORM_STUDENT dormStudent " +
			"    WHERE " +
			"      dormStudent.ROOM_ID = room.ID AND " +
			"      dormStudent.CHECK_OUT_DATE IS NULL AND " +
			"      dormStudent.DELETED = FALSE " +
			"  ), " +
			"  CAST(room.COST AS INT) " +
			"FROM " +
			"  DORM_ROOM room " +
			"WHERE " +
			"  DELETED = FALSE AND " +
			"  room.DORM_ID = ?1 " +
			"ORDER BY room.ROOM_NO";

	public static final String ROOM_NO_COLUMN = "roomNo";

	public static final String BED_COUNT_COLUMN = "bedCount";

	public static final String BUSY_BED_COUNT_COLUMN = "busyBedCount";

	public static final String COST_COLUMN = "cost";

	@FieldInfo(type = EFieldType.TEXT)
	private ID id;

	@FieldInfo(type = EFieldType.TEXT,order = 2)
	private String roomNo;

	@FieldInfo(type = EFieldType.INTEGER,order = 3)
	private int bedCount;

	@FieldInfo(type = EFieldType.INTEGER,order = 4)
	private int busyBedCount;

	@FieldInfo(type = EFieldType.INTEGER,order = 5)
	private int cost;

	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
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

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String logString() throws Exception {
		return null;
	}
}
