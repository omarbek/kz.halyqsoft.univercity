package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class DOCUMENT_ACTIVITY extends AbstractEntity{

    @Column(name = "ACTIVITY_NAME", nullable = false)
    private String activityName;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
