package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class STREAM_GROUP extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO,order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID", nullable = false)})
    private GROUPS group;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STREAM_ID", referencedColumnName = "ID", nullable = false)})
    private STREAM stream;

    public GROUPS getGroup() {
        return group;
    }

    public void setGroup(GROUPS group) {
        this.group = group;
    }

    public STREAM getStream() {
        return stream;
    }

    public void setStream(STREAM stream) {
        this.stream = stream;
    }

    @Override
    public String toString() {
        return stream.getName() + ": " + group.getName();
    }
}
