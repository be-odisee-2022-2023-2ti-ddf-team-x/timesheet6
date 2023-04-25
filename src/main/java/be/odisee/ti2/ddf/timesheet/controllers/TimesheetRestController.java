package be.odisee.ti2.ddf.timesheet.controllers;

import be.odisee.ti2.ddf.timesheet.dao.EntryRepository;
import be.odisee.ti2.ddf.timesheet.domain.Entry;
import be.odisee.ti2.ddf.timesheet.domain.Project;
import be.odisee.ti2.ddf.timesheet.errors.EntryNotAuthorizedException;
import be.odisee.ti2.ddf.timesheet.formdata.EntryData;
import be.odisee.ti2.ddf.timesheet.service.TimesheetService;

import be.odisee.ti2.ddf.timesheet.errors.EntryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/timesheetrest", produces = "application/json")
// needed for CORS cookie passing from vue front and API tester respectively
@CrossOrigin(origins={"http://localhost:8888", "chrome-extension://aejoelaoggembcahagimdiliamlcdmfm"},
        maxAge = 3600, allowCredentials = "true")
public class TimesheetRestController {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private TimesheetService timesheetService;

    @GetMapping("/{id}")
    public Entry getEntrybyId(@PathVariable("id") Long id) {

        if (entryRepository.findById(id).isPresent()) {
            return entryRepository.findById(id).get();
        } else {
            return  null;
        }
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

        StringBuilder message=new StringBuilder();

        try {
            // Are there any input validation errors detected by JSR 380 bean validation?
            if (errors.hasErrors() ) {
                message = new StringBuilder("Correct input errors, please: <br>");
                for (ObjectError objectError: errors.getAllErrors()) {
                    message.append(objectError.getDefaultMessage()).append("<br>");
                }
                throw new IllegalArgumentException();
            }
            // Check how many projects have been selected for this entry
            long numberNonzero = Arrays.stream( entryData.getProjectIds()).filter(x -> x>0).count();
            // There should have been one and only one project selected, if not throw an exception
            if (numberNonzero != 1) {
                message = new StringBuilder("Unacceptable, there must be 1 and only 1 project");
                throw new IllegalArgumentException();
            }

            // Now that the input seems to be OK, let's create a new entry or update/delete an existing entry
            message = new StringBuilder(timesheetService.processEntry(entryData));

            // Prepare form for new data-entry - moeten we niet meer doen in dit geval
            // entryData = timesheetService.prepareNewEntryData();

        } catch (IllegalArgumentException e) {
            // Nothing special needs to be done
        }
        return message.toString();
    }

    /**
     * Prepare entryData to be edited
     * @param id of entry to be edited
     * @return the original data to be edited by the caller
     */
    @GetMapping("/editentrydata")
    public ResponseEntity<Object> entryEditForm(@RequestParam("id") long id) {

        EntryData entryData;
        try {
            entryData = timesheetService.prepareEntryDataToEdit(id);
        } catch (EntryNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(entryData, HttpStatus.OK);
    }

    /**
     * Delete the entry and prepare for creation of a new one
     * @return how the operation went
     */
    @PostMapping(path = "deleteEntry", consumes = "application/json")
    public String deleteEntry(@RequestBody EntryData entryData) {

        try {
            timesheetService.deleteEntry(entryData.getId());
        } catch (EntryNotFoundException | EntryNotAuthorizedException e) {
            return (e.getMessage());
        }
        return "Successfully deleted entry "+entryData.getDescription();
    }


}
