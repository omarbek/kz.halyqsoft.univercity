package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CANDIDATE extends AbstractEntity {
    private static final long serialVersionUID = 2533240250321509812L;

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
    @Column(name = "CANDIDATE_NAME", nullable = false)
    private String candidateName;

    public CANDIDATE() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    @Override
    public String toString() {
        return candidateName;
    }
}
