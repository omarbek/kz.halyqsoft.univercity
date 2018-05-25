package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Omarbek
 * @created on 21.05.2018
 */
@Entity
public class CARD extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, max = 256)
    @Column(name = "CARD_NAME", nullable = false)
    private String cardName;

    @FieldInfo(type = EFieldType.DATETIME, order = 2, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return cardName;
    }
}
