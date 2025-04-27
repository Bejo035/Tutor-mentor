package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDb, Long> {
    Optional<UserDb> findByUserName(String userName);
}
