package be.odisee.ti2.ddf.timesheet.dao;

import be.odisee.ti2.ddf.timesheet.domain.Entry;
import be.odisee.ti2.ddf.timesheet.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EntryRepository extends CrudRepository<Entry, Long> {

    /**
     * The default findById would return Optional<Entry>
     * We want a Entry object as return
     * therefore we override this method
     * @param id
     * @return
     */
    public Entry findById(long id);

    /**
     * @return The entry with the largest id = the most recently added entry for a given user
     */
    public Entry findFirstByUserOrderByIdDesc(User user);

    /**
     *
     * @param startDateTime
     * @param endDateTime
     * @return The entries with DateTimeFrom between startDateTime and endDateTime for given user, except the dummy entry
     */
    public List<Entry> findByProjectNotNullAndUserAndDateTimeFromBetween(User user,
                                                                           LocalDateTime startDateTime,
                                                                           LocalDateTime endDateTime);
}
