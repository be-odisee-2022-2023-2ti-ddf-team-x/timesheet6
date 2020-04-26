package be.odisee.ti2.se4.timesheet.dao;

import be.odisee.ti2.se4.timesheet.domain.Objective;
import be.odisee.ti2.se4.timesheet.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObjectiveRepository extends CrudRepository<Objective, Long> {

    /**
     * The default findById would return Optional<Objective>
     * We want a Objective object as return
     * therefore we override this method
     * @param id
     * @return
     */
    public Objective findById(long id);

    /**
     * List all objectives for a particular user, order alphabetically by name
     */
    public List<Objective> findAllByUserOrderByName(User user);

}
