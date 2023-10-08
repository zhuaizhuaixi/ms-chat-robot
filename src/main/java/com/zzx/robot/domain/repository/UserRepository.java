package com.zzx.robot.domain.repository;

import com.zzx.robot.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByQq(String qq);

}
