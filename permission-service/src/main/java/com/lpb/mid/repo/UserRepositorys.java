package com.lpb.mid.repo;


import com.lpb.mid.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepositorys extends JpaRepository<UsersEntity, String> {
    UsersEntity findByUserNameAndStatus(String userName, String status);
}
