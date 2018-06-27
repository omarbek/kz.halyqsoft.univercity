package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class DOCUMENT_IMPORTANCE extends AbstractEntity{

    @Column(name = "importance_name", nullable = false)
    private String importanceName;

    public String getImportanceName() {
        return importanceName;
    }

    public void setImportanceName(String importanceName) {
        this.importanceName = importanceName;
    }
}
