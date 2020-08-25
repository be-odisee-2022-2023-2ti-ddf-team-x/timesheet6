package be.odisee.ti2.se4.timesheet.formdata;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EntryData {

    // id is needed for updating
    private long id=0;

    @NotBlank(message="Date must be specified ")
    private String entryDatum;

    @NotBlank(message="Time must be specified ")
    private String startTime, endTime;

    private long[] projectIds;
    private long objectiveId;

    @NotBlank(message="Description must be filled in ")
    private String description;

    // HV 25-8-2020 added for security reasons
    private String username;
}
