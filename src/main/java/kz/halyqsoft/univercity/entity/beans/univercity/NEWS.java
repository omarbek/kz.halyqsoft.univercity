package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import org.r3a.common.entity.AbstractEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * @created Mar 24, 2017 12:32:02 PM
 */
@Entity
public class NEWS extends AbstractEntity {
	
	private static final long serialVersionUID = 3206193597609281983L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)})
    private EMPLOYEE employee;
	
	@Column(name = "TOPIC", nullable = false)
	private String topic;
	
	@Column(name = "NEWS_BODY", nullable = false)
    @Lob
    private String newsBody;
	
	@Column(name = "LINK")
	private String link;
	
	@Column(name = "GLOBAL_NEWS", nullable = false)
    private boolean globalNews;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@Column(name = "EXPIRE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;
	
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

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
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
