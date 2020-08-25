package be.odisee.ti2.se4.timesheet.service;

import be.odisee.ti2.se4.timesheet.dao.*;
import be.odisee.ti2.se4.timesheet.domain.*;
import be.odisee.ti2.se4.timesheet.errors.EntryNotFoundException;
import be.odisee.ti2.se4.timesheet.formdata.EntryData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TimesheetServiceImpl implements TimesheetService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    ObjectiveRepository objectiveRepository;

    @Autowired
    EntryRepository entryRepository;

    @Autowired
    UserRepository userRepository;

    private List<Category> getCategories() {
        return (List<Category>) categoryRepository.findAllByUserOrderByName(findAuthenticatedUser());
    }

    private List<Project> getProjects() {
        return (List<Project>) projectRepository.findAllByUserOrderByName(findAuthenticatedUser());
    }

    /**
     * Creates a map with categories as keys
     * and lists of projects belonging to that category
     * @return the map
     */
    @Override
    public Map<String, List<Project> > getCategoriesWithProjects() {

        Map<String, List<Project> > categoriesWithProjectsMap = new LinkedHashMap<String, List<Project> >();

        List<Category> categories = getCategories();
        List<Project> projects = getProjects();

        for (Category category : categories) {

            categoriesWithProjectsMap.put(category.getName().toUpperCase(),
                    filterByCategory(projects, category.getName()) );
        }

        return categoriesWithProjectsMap;
    }

    /**
     * A Java-8-filter to filter projects per category
     */
    private List<Project> filterByCategory(
            List<Project> projects, String category) {
        return projects
                .stream()
                .filter(x -> x.getCategory().getName().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Objective> getObjectives() {

        return (List<Objective>) objectiveRepository.findAllByUserOrderByName(findAuthenticatedUser());
    }

    /**
     * Instantiates and initializes new EntryData-object
     * to be used in the form
     * @return
     */
    public EntryData prepareNewEntryData() {

        User theUser = findAuthenticatedUser();
        Entry lastEntry = entryRepository.findFirstByUserOrderByIdDesc(theUser);
        return prepareEntryData(lastEntry, true);
    }

    /**
     * Prepares an EntryData-object based on an Entry-object
     * @param theEntry the Entry-object
     * @param timeShift indicates if the startTime needs to be shifted from previous timeTo
     * @return
     */
    private EntryData prepareEntryData(Entry theEntry,  boolean timeShift) {

        EntryData entryData = new EntryData();

        if (theEntry.getProject() != null) {

            // Pick up the last entry project choice and adapt it to the form
            Project lastProject = theEntry.getProject();
            List<Category> categories = getCategories();
            List<Long> projectIdsInList = new ArrayList<Long>();
            for (Category category: categories ) {
                if (category.equals(lastProject.getCategory()))
                    projectIdsInList.add(lastProject.getId());
                else
                    projectIdsInList.add( 0L );
            }
            // An array of long primitives is needed
            long[] projectIds = projectIdsInList.stream().mapToLong(l -> l).toArray();
            entryData.setProjectIds( projectIds );

            // Pick up the last entry objective provided there was one
            Objective lastEntryObjective = theEntry.getObjective();
            if (lastEntryObjective != null) entryData.setObjectiveId( lastEntryObjective.getId() );
            else entryData.setObjectiveId(0);

            // Pick up the other last entry data and propose them in the form
            entryData.setEntryDatum( theEntry.getDateTimeTo().toLocalDate().toString() );
            if (timeShift) {
                // The right to be used dateTimeFrom is in the dummy entry for this user
                // Ik denk dat we dit historisch niet zo goed deden hier
                // entryData.setStartTime( findAuthenticatedUser().getDummyEntry().getDateTimeFrom()
                //                            .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) );
                entryData.setStartTime( theEntry.getDateTimeTo().toLocalTime().toString() );
                entryData.setEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                entryData.setStartTime( theEntry.getDateTimeFrom().toLocalTime().toString() );
                entryData.setEndTime( theEntry.getDateTimeTo().toLocalTime().toString() );
            }

            entryData.setDescription( theEntry.getDescription() );
        } else {  // there is only a dummy entry for this user
            entryData.setObjectiveId(0);
            entryData.setEntryDatum(LocalDate.now().toString());
            entryData.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            entryData.setEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        return entryData;
    }

    @Override
    public String processEntry(EntryData entryData) {

        Entry entry;

        if (entryData.getId() == 0) entry = new Entry();
        else entry = entryRepository.findById( entryData.getId() );

        if (entry.getUser() == null) entry.setUser( findAuthenticatedUser() );

        // The projectId is that of the one and only selected
        long[] projectIds = entryData.getProjectIds();
        long projectId = Arrays.stream(projectIds).max().getAsLong();
        entry.setProject( projectRepository.findById(projectId) );

        long objectiveId = entryData.getObjectiveId();
        if (objectiveId !=0) entry.setObjective( objectiveRepository.findById(objectiveId) );
        else entry.setObjective( null );

        LocalTime timeFrom = LocalTime.parse(entryData.getStartTime());
        LocalTime timeTo = LocalTime.parse(entryData.getEndTime());
        LocalDate theDatum = LocalDate.parse(entryData.getEntryDatum());
        entry.setDateTimeFrom( LocalDateTime.of(theDatum, timeFrom ));
        LocalDateTime dateTimeTo = LocalDateTime.of(theDatum, timeTo );
        entry.setDateTimeTo( dateTimeTo );
        entry.setDuration(Duration.between(entry.getDateTimeFrom(),entry.getDateTimeTo()));

        String description = entryData.getDescription();
        entry.setDescription( description );

        // Save the newly created entry
        entryRepository.save(entry);

        // We must remember the DateTimeTo in the DateTimeFrom of the dummy Entry for this user
        setNextStartTime(dateTimeTo);

        return entry.getDateTimeFrom() + " to " + entry.getDateTimeTo() +
                " '" + entry.getDescription() + "' has been processed, with id="+entry.getId();

    }

    @Override
    public List<Entry> getEntriesFromDate(LocalDate theDate) {

        LocalDateTime startDateTime = LocalDateTime.of(theDate, LocalTime.parse("00:00:00.0"));
        LocalDateTime endDateTime = LocalDateTime.of(theDate, LocalTime.parse("23:59:00.0"));
        return entryRepository.findByProjectNotNullAndUserAndDateTimeFromBetween(findAuthenticatedUser(), startDateTime, endDateTime);
    }

    @Override
    public Duration getTotalDuration(List<Entry> entries) {

        Duration duration = Duration.ZERO;

        for (Entry entry: entries) {
            duration = duration.plus(entry.getDuration());
        }

        return duration;
    }

    @Override
    public EntryData prepareEntryDataToEdit(long id) throws EntryNotFoundException {

        Entry theEntry = entryRepository.findById(id);
        if (theEntry == null) throw new EntryNotFoundException(id);
        EntryData theEntryData = prepareEntryData(theEntry,false);
        theEntryData.setId(id);
        return theEntryData;
    }

    @Override
    public void deleteEntry(long id) {

        Entry entry = entryRepository.findById(id);
        entryRepository.delete(entry);
    }

    private String getAuthenticatedUsername() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return currentPrincipalName;
    }

    private User findAuthenticatedUser() {

        String username = getAuthenticatedUsername();
        /* - we hebben deze hack niet meer nodig nu
        if (username == "anonymousUser")
            username = "hans";   // just a hack to proceed with "hans" when not authenticated
         */
        return userRepository.findByUsername(username);
    }

    @Override
    public String getAuthenticatedFullname() {

        User theUser = findAuthenticatedUser();
        return theUser.getFirstName() +' '+ theUser.getLastName();
    }

    @Override
    public void setNextStartTime(LocalDateTime now) {

        Entry dummyEntry = findAuthenticatedUser().getDummyEntry();
        dummyEntry.setDateTimeFrom(now);
        entryRepository.save(dummyEntry);
    }

    @Override
    public void setNextEndTime(LocalDateTime now) {

        Entry dummyEntry = findAuthenticatedUser().getDummyEntry();
        dummyEntry.setDateTimeTo(now);
        entryRepository.save(dummyEntry);
    }
}
