package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Mar 24, 2017 12:32:02 PM
 */
@Entity
public class NEWS extends AbstractEntity {

    private static final long serialVersionUID = 3206193597609281983L;

    @FieldInfo(type = EFieldType.FK_COMBO, inEdit = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)})
    private USERS user;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    @Column(name = "TOPIC", nullable = false)
    private String topic;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "NEWS_BODY", nullable = false)
    @Lob
    private String newsBody;

    @FieldInfo(type = EFieldType.TEXT, order = 5, required = false)
    @Column(name = "LINK")
    private String link;

    @FieldInfo(type = EFieldType.DATETIME, order = 7, inEdit = false)
    @Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @FieldInfo(type = EFieldType.DATETIME, order = 8)
    @Column(name = "EXPIRE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 9, required = false)
    @Column(name = "GLOBAL_NEWS", nullable = false)
    private boolean globalNews;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public NEWS() {
    }

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public void setNewsBody(String newsBody) {
        this.newsBody = newsBody;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isGlobalNews() {
        return globalNews;
    }

    public void setGlobalNews(boolean globalNews) {
        this.globalNews = globalNews;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
