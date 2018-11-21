package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Mar 24, 2018 12:50:02 PM
 */
@Entity
public class NEWS_COMMENT extends AbstractEntity {

    private static final long serialVersionUID = -754266641645349366L;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "NEWS_ID", referencedColumnName = "ID")})
    private NEWS news;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)})
    private USERS user;

    @Column(name = "COMMENT_BODY", nullable = false)
    @Lob
    private String commentBody;

    @Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public NEWS_COMMENT() {
    }

    public NEWS getNews() {
        return news;
    }

    public void setNews(NEWS news) {
        this.news = news;
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
