package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 22, 2015 4:52:55 PM
 */
@Entity
public class CORPUS extends AbstractEntity {

	private static final long serialVersionUID = -8835818911571519475L;

	@FieldInfo(type = EFieldType.TEXT, max = 16, order = 1)
	@Column(name = "CORPUS_NAME", nullable = false)
	private String corpusName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 2, required = false)
	@Column(name = "ADDRESS")
	private String address;
	
	public CORPUS() {
	}

	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return corpusName;
	}
}
