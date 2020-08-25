package be.odisee.ti2.se4.timesheet.controllers;

import be.odisee.ti2.se4.timesheet.errors.EntryNotFoundException;
import be.odisee.ti2.se4.timesheet.formdata.EntryData;

import be.odisee.ti2.se4.timesheet.service.TimesheetService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.Errors;
import javax.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Controller
@RequestMapping("/timesheet")
public class TimesheetController {

    @Autowired
    private TimesheetService timesheetService;

    /**
     * Prepare form for create
     */
    @GetMapping
    public String entryCreateForm(Model model) {

        EntryData entryData = timesheetService.prepareNewEntryData();
        prepareForm(entryData, model);
        model.addAttribute("message","Please, create a timesheet entry");
        return "entry";
    }

    /**
     * Prepares the form with data for projects- and objectives comboboxes
     */
    private void prepareForm(EntryData entryData, Model model) {

        model.addAttribute("fullname", timesheetService.getAuthenticatedFullname());
        model.addAttribute("categoriesWithProjects", timesheetService.getCategoriesWithProjects());
        model.addAttribute("objectives",timesheetService.getObjectives() );
        model.addAttribute("entryData", entryData );
        LocalDate theDatum = LocalDate.parse(entryData.getEntryDatum());
        model.addAttribute("entries", timesheetService.getEntriesFromDate(theDatum) );
        model.addAttribute("totalDuration", timesheetService.getTotalDuration(timesheetService.getEntriesFromDate(theDatum)));
    }


    /**
     * @param entryData to be taken over, except for timeFrom and timeTo, to be set to now
     */
    @PostMapping(params = "startnow")
    public String setTimeFromNow(EntryData entryData, Model model) {

        LocalDateTime now = LocalDateTime.now();
        entryData.setStartTime(now.format(DateTimeFormatter.ofPattern("HH:mm")));
        entryData.setEndTime(now.format(DateTimeFormatter.ofPattern("HH:mm")));

        // Now is set for a user, it should not be lost when the user logs off
        timesheetService.setNextStartTime(now);
        timesheetService.setNextEndTime(now);

        prepareForm(entryData, model);
        return "entry";
    }

    /**
     * @param entryData to be taken over, except for timeTo, to be set to now
     */
    @PostMapping(params = "endnow")
    public String setTimeToNow(EntryData entryData, Model model) {

        LocalDateTime now = LocalDateTime.now();
        entryData.setEndTime(now.format(DateTimeFormatter.ofPattern("HH:mm")));

        timesheetService.setNextEndTime(now);

        prepareForm(entryData, model);
        return "entry";
    }

    /**
     * Process the form
     * @param entryData the data for the entry to be saved
     */
    @PostMapping(params = "submit")
    public String processEntry(@Valid EntryData entryData, Errors errors, Model model) {

        String message="";

        try {
            // Are there any input validation errors detected by JSR 380 bean validation?
            if (errors.hasErrors() ) {
                message = "Correct input errors, please";
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

            // Prepare form for new data-entry
            entryData = timesheetService.prepareNewEntryData();

        } catch (IllegalArgumentException e) {
            // Nothing special needs to be done
        }
        prepareForm(entryData, model);
        model.addAttribute("message", message);
        return "entry";
    }

    /**
     * Prepare form for update or delete
     * @param id - the id of the entry to be updated or deleted
     * @param model
     * @return
     */
    @GetMapping("/edit")
    public String entryEditForm(@RequestParam("id") long id, Model model) {

        EntryData entryData = null;
        try {
            entryData = timesheetService.prepareEntryDataToEdit(id);
            prepareForm(entryData, model);
            model.addAttribute("message", "Update or Delete this entry please - or Cancel");
        } catch (EntryNotFoundException e) {
            entryCreateForm(model);
            model.addAttribute("message",e.getMessage());
        }
        return "entry";
    }

    /**
     * Delete the entry and prepare for creation of a new one
     * @return
     */
    @PostMapping(params = "delete")
    public String deleteEntry(EntryData entrydata, Model model) {

        timesheetService.deleteEntry(entrydata.getId());
        EntryData entryData = timesheetService.prepareNewEntryData();
        prepareForm(entryData, model);
        model.addAttribute("message", "Successfully deleted entry "+entrydata.getDescription());
        return "entry";
    }

    /**
     * If user does not want to update or delete, he will be redirect to create
     * @return
     */
    @PostMapping(params = "cancel")
    public String redirectToCreate() {

        return "redirect:/timesheet";
    }
}
