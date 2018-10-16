package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TURNSTILE_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
@Entity
public class USER_ARRIVAL extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @FieldInfo(type = EFieldType.DATETIME, order = 3, required = false, readOnlyFixed = true, inEdit = false)
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 4)
    @Column(name = "COME_IN", nullable = false)
    private boolean comeIn;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TURNSTILE_TYPE_ID", referencedColumnName = "ID")})
    private TURNSTILE_TYPE turnstileType;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6, inView = false, inEdit = false ,inGrid = false, required = false)
    @Column(name = "MANUALLY_SIGNED")
    private boolean manuallySigned = false;


    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isComeIn() {
        return comeIn;
    }

    public void setComeIn(boolean comeIn) {
        this.comeIn = comeIn;
    }

    public TURNSTILE_TYPE getTurnstileType() {
        return turnstileType;
    }

    public void setTurnstileType(TURNSTILE_TYPE turnstileType) {
        this.turnstileType = turnstileType;
    }

    public boolean isManuallySigned() {
        return manuallySigned;
    }

    public void setManuallySigned(boolean manuallySigned) {
        this.manuallySigned = manuallySigned;
    }

}
