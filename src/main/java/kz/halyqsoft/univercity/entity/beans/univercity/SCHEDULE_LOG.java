package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * @@author Omarbek
 * @created Apr 11, 2017 10:50:13 AM
 */
@Entity
public class SCHEDULE_LOG extends AbstractEntity {

	private static final long serialVersionUID = 1002285370514682255L;
	
	@Column(name = "LOG_RECORD", nullable = false)
    @Lob
    private String logRecord;
	
	public SCHEDULE_LOG() {
	}

	public String getLogRecord() {
		return logRecord;
	}

	public void setLogRecord(String logRecord) {
		this.logRecord = logRecord;
	}
}
