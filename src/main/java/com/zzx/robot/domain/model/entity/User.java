package com.zzx.robot.domain.model.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Entity
@Table(name = "ROBOT_USER")
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(
            generator = "sequence",
            strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
            name = "sequence",
            sequenceName = "SEQ_ROBOT_USER",
            allocationSize = 1
    )
    private Long id;

    private String qq;

    private String mapleName;

    private Date lastTowerTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMapleName() {
        return mapleName;
    }

    public void setMapleName(String mapleName) {
        this.mapleName = mapleName;
    }

    public Date getLastTowerTime() {
        return lastTowerTime;
    }

    public void setLastTowerTime(Date lastTowerTime) {
        this.lastTowerTime = lastTowerTime;
    }
}
