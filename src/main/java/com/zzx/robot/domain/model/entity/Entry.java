package com.zzx.robot.domain.model.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Entity
@Table(name = "ROBOT_ENTRY")
public class Entry {

    @Id
    @Column(name = "ID")
    @GeneratedValue(
            generator = "sequence",
            strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
            name = "sequence",
            sequenceName = "SEQ_ROBOT_ENTRY",
            allocationSize = 1
    )
    private Long id;

    private String name;

    @Column(name = "CONTENT", columnDefinition = "CLOB")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
