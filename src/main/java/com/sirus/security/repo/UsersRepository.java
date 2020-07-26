package com.sirus.security.repo;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sirus.security.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByMobile(String mobile);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Users SET password = ?1, updatedOn = ?2 WHERE contactNumber = ?3")
	void updatePassword(String password, Date updatedOn, String contactNumber);

}
