package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;

/**
 * @@author Omarbek
 * @created Apr 19, 2017 9:56:20 AM
 */
@Entity
public class REQUEST_FILE extends AbstractEntity {

	private static final long serialVersionUID = -428359558539266457L;

	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;

	@Column(name = "FILE_BODY")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	private byte[] fileBody;

	public REQUEST_FILE() {
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
