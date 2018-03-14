package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractEmail;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created 20.12.2011 10:35:13
 */
@Entity
public class EMAIL extends AbstractEmail {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")})
    private EMAIL_STATUS status;

    public EMAIL() {
    }

    public EMAIL_STATUS getStatus() {
        return status;
    }

    public void setStatus(EMAIL_STATUS status) {
        this.status = status;
    }
}
