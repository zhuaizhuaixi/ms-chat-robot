package com.zzx.robot.domain.repository;

import com.zzx.robot.domain.model.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    void deleteByName(String name);

    List<Entry> findByName(String name);

}
