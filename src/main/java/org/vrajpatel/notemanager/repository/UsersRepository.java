package org.vrajpatel.notemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vrajpatel.notemanager.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {

    @Query(value = "select email from Users  where email=:email ")
    public String isEmailExist(String email);

    public Users findByEmail(String email);

    public void deleteByEmail(String email);
}
