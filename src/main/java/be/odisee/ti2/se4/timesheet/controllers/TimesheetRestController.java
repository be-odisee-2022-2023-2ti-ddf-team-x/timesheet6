package be.odisee.ti2.se4.timesheet.controllers;

import be.odisee.ti2.se4.timesheet.dao.EntryRepository;

import be.odisee.ti2.se4.timesheet.domain.*;

import be.odisee.ti2.se4.timesheet.formdata.EntryData;
import be.odisee.ti2.se4.timesheet.service.TimesheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/timesheetrest", produces = "application/json")
@CrossOrigin(origins="http://localhost:8888", maxAge = 3600, allowCredentials = "true")  // needed for CORS cookie passing
public class TimesheetRestController {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private TimesheetService timesheetService;

    @GetMapping("/{id}")
    public Entry getEntrybyId(@PathVariable("id") Long id) {
        return entryRepository.findById(id).get();
    }

    @GetMapping("/categoriesWithProjects")
    public Map<String, List<Project>> getCategoriesWithProjects(){
        return timesheetService.getCategoriesWithProjects();
    }

    @GetMapping("/objectives")
    public Object getObjectives(){
        return timesheetService.getObjectives();
    }

    @GetMapping("/newentrydata")
    public Object getEntryData(){
        return timesheetService.prepareNewEntryData();
    }

    @GetMapping("/entriesFromDate")
    public List<Entry> getEntriesFromDate(String entryDatum) {
        LocalDate theDatum = LocalDate.parse(entryDatum);
        return timesheetService.getEntriesFromDate(theDatum);
    }

    /**
     * Process the form
     * @param entryData the data for the entry to be saved
     */
    @PostMapping(path = "/processEntry", consumes = "application/json")
    public String processEntry(@Valid @RequestBody EntryData entryData, Errors errors) {

        String message="";

        try {
            // Are there any input validation errors detected by JSR 380 bean validation?
            if (errors.hasErrors() ) {
                message = "Correct input errors, please: "+errors.getAllErrors();
                throw new IllegalArgumentException();
            }
            // Check how many projects have been selected for this entry
            long numberNonzero = Arrays.stream( entryData.getProjectIds()).filter(x -> x>0).count();
            // There should have been one and only one project selected, if not throw an exception
            if (numberNonzero != 1) {
                message = "Unacceptable, there must be 1 and only 1 project";
                throw new IllegalArgumentException();
            }

            // Now that the input seems to be OK, let's create a new entry or update/delete an existing entry
            message = timesheetService.processEntry(entryData);

            // Prepare form for new data-entry - moeten we niet meer doen in dit geval
            // entryData = timesheetService.prepareNewEntryData();

        } catch (IllegalArgumentException e) {
            // Nothing special needs to be done
        }
        return message;
    }

    /**
     * Prepare entryData to be edited
     * @param id of entry to be edited
     * @return the original data to be edited by the caller
     */
    @GetMapping("/editentrydata")
    public Object entryEditForm(@RequestParam("id") long id) {

        EntryData entryData = timesheetService.prepareEntryDataToEdit(id);
        return entryData;
    }

    /**
     * Delete the entry and prepare for creation of a new one
     * @return
     */
    @PostMapping(path = "deleteEntry", consumes = "application/json")
    public String deleteEntry(@RequestBody EntryData entryData) {

        timesheetService.deleteEntry(entryData.getId());
        return "Successfully deleted entry "+entryData.getDescription();
    }


}
