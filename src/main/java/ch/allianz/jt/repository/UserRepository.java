package ch.allianz.jt.repository;

import ch.allianz.jt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


    public interface UserRepository extends JpaRepository<User, Long> {}

