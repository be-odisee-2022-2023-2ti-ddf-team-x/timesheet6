package be.odisee.ti2.ddf.timesheet.dao;

import be.odisee.ti2.ddf.timesheet.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    // needed for Spring Security
    public User findByUsername(String username);
}
