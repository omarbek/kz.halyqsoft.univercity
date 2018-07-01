package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class STREAM_GROUP extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "GROUP_ID",  referencedColumnName = "ID", nullable = false)})
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

}
