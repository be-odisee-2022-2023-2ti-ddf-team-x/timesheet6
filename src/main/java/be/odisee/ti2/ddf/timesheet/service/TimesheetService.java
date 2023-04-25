package be.odisee.ti2.ddf.timesheet.service;

import be.odisee.ti2.ddf.timesheet.errors.EntryNotAuthorizedException;
import be.odisee.ti2.ddf.timesheet.domain.Entry;
import be.odisee.ti2.ddf.timesheet.domain.Project;
import be.odisee.ti2.ddf.timesheet.errors.EntryNotFoundException;
import be.odisee.ti2.ddf.timesheet.formdata.EntryData;

import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TimesheetService {

    public Map<String, List<Project> > getCategoriesWithProjects();

    public Object getObjectives();

    public EntryData prepareNewEntryData();

    public String processEntry(@Valid EntryData entryData);

    public List<Entry> getEntriesFromDate(LocalDate theDate);

    public Duration getTotalDuration(List<Entry> entries);

    public EntryData prepareEntryDataToEdit(long id) throws EntryNotFoundException;

    public void deleteEntry(long id) throws EntryNotFoundException, EntryNotAuthorizedException;

    public String getAuthenticatedFullname();

    void setNextStartTime(LocalDateTime now);

    void setNextEndTime(LocalDateTime now);
}
