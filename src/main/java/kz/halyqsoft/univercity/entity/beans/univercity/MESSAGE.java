package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MESSAGE extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CHAT_ID", referencedColumnName = "ID", nullable = false)})
    private CHAT chat;

    @Column(name = "content")
    private String content;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "from_first", nullable = false)
    private Boolean fromFirst;

    public MESSAGE() {
    }

    public CHAT getChat() {
        return chat;
    }

    public void setChat(CHAT chat) {
        this.chat = chat;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Boolean getFromFirst() {
        return fromFirst;
    }

    public void setFromFirst(Boolean fromFirst) {
        this.fromFirst = fromFirst;
    }
}
