package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import kz.halyqsoft.univercity.entity.beans.univercity.DORM_STUDENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Omarbek
 * Created Apr 11, 2017 4:17:33 PM
 */
@Entity
public class DORM_ROOM extends AbstractEntity {

    private static final long serialVersionUID = 539867413160973719L;

    @ManyToOne
    @JoinColumns({@JoinColumn(name = "DORM_ID", referencedColumnName = "ID", nullable = false)})
    private DORM dorm;

    @FieldInfo(type = EFieldType.TEXT, max = 64)
    @Column(name = "ROOM_NO", length = 64, nullable = false)
    private String roomNo;

    @FieldInfo(type = EFieldType.INTEGER, order = 2)
    @Column(name = "BED_COUNT", nullable = false)
    private int bedCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 3, inView = false, inEdit = false)
    @Transient
    private Integer busyBedCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 4)
    @Column(name = "COST", nullable = false)
    private double cost;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    private Set<DORM_STUDENT> residence;

    public DORM_ROOM() {
        residence = new HashSet<>();
    }

    public DORM getDorm() {
        return dorm;
    }

    public void setDorm(DORM dorm) {
        this.dorm = dorm;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public int getBusyBedCount() {
        if (busyBedCount == null) {
            busyBedCount = 0;
            for (DORM_STUDENT dormStudent : residence) {
                if (dormStudent.getCheckOutDate() == null) {
                    busyBedCount++;
                }
            }
        }
        return busyBedCount;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Set<DORM_STUDENT> getResidence() {
        return residence;
    }

    public void setResidence(Set<DORM_STUDENT> residence) {
        this.residence = residence;
    }

    @Override
    public String toString() {
        return roomNo;
    }
}
