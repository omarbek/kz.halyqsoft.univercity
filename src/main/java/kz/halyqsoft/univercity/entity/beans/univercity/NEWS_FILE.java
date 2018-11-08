package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Mar 24, 2018 12:44:36 PM
 */
@Entity
public class NEWS_FILE extends AbstractEntity {

	private static final long serialVersionUID = -3106047691470584494L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "NEWS_ID", referencedColumnName = "ID")})
    private NEWS news;
	
	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;
	
	@Column(name = "FILE_BODY")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileBody;
	
	public NEWS_FILE() {
	}

	public NEWS getNews() {
		return news;
	}

	public void setNews(NEWS news) {
		this.news = news;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileBody() {
		return fileBody;
	}

	public void setFileBody(byte[] fileBody) {
		this.fileBody = fileBody;
	}
}
