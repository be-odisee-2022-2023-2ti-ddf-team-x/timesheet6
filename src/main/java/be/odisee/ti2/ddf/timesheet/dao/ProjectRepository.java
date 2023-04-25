package be.odisee.ti2.ddf.timesheet.dao;

import be.odisee.ti2.ddf.timesheet.domain.Project;
import be.odisee.ti2.ddf.timesheet.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    /**
     * The default findById would return Optional<Project>
     * We want a Project object as return
     * therefore we override this method
     * @param id
     * @return
     */
    public Project findById(long id);

    /**
     * List all projects for a particular user, order alphabetically by name
     */
    public List<Project> findAllByUserOrderByName(User user);
}
