package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractLog;
import org.r3a.common.utility.DateUtils;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import java.time.format.DateTimeFormatter;

/**
 * @author Omarbek
 * Created 03.08.2011 15:07:40
 */
@Entity
public class LOG extends AbstractLog {

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 1, columnWidth = 150)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LOG_TYPE_ID", referencedColumnName = "ID")})
    private LOG_TYPES logType;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3, columnWidth = 120)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TASK_ID", referencedColumnName = "ID")})
    private TASKS task;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 5, required = false, columnWidth = 150)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TABLE_ID", referencedColumnName = "ID")})
    private TABLES table;

    public LOG() {
    }

    public LOG_TYPES getLogType() {
        return logType;
    }

    public void setLogType(LOG_TYPES logType) {
        this.logType = logType;
    }

    public TABLES getTable() {
        return table;
    }

    public void setTable(TABLES table) {
        this.table = table;
    }

    public TASKS getTask() {
        return task;
    }

    public void setTask(TASKS task) {
        this.task = task;
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(logType.toString());
        sb.append('|');
        try {
            sb.append(getLogDate().format(DateTimeFormatter.ofPattern(DateUtils.LONGEST_FORMAT)));
            sb.append('|');
        } catch (IllegalArgumentException ex) {
        }
        sb.append(user.toString());

        if (task != null) {
            sb.append('|');
            sb.append(task.toString());
        }

        if (table != null) {
            sb.append('|');
            sb.append(table.toString());
        }

        if (getLog() != null) {
            sb.append('|');
            sb.append(getLog());
        }
        sb.append(']');

        return sb.toString();
    }
}
