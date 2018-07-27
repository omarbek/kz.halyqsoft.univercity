package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Dinassil Omarbek
 * @created 26.05.2017.
 */
@SuppressWarnings("serial")
@Entity
public class COMPLAINT extends AbstractEntity {

    @ManyToOne
    @JoinColumns({@JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @Column(name = "SHORT_DESCRIPTION", length = 50, nullable = false)
    private String shortDescription;

    @Column(name = "DESCRIPTION", length = 2048, nullable = false)
    private String description;

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createDate;

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
