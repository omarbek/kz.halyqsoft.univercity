package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class CHAT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "FIRST_USER_ID", referencedColumnName = "ID", nullable = false)})
    private USERS first_user;

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SECOND_USER_ID", referencedColumnName = "ID", nullable = false)})
    private USERS second_user;

    @Column(name = "accepted", nullable = false)
    private Boolean accepted;

    public CHAT() {
    }

    public USERS getFirst_user() {
        return first_user;
    }

    public void setFirst_user(USERS first_user) {
        this.first_user = first_user;
    }

    public USERS getSecond_user() {
        return second_user;
    }

    public void setSecond_user(USERS second_user) {
        this.second_user = second_user;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
