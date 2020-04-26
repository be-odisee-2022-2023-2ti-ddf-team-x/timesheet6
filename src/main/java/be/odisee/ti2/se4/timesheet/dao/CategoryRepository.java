package be.odisee.ti2.se4.timesheet.dao;

import be.odisee.ti2.se4.timesheet.domain.Category;
import be.odisee.ti2.se4.timesheet.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    /**
     * Look up a category based on its unique name
     */
    public Category findCategoryByUserAndName(User user, String name);

    /**
     * List all categories for a particular user, order alphabetically by name
     */
    public List<Category> findAllByUserOrderByName(User user);
}
