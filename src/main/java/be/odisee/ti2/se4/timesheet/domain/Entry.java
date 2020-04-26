package be.odisee.ti2.se4.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "ENTRIES")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force=true)
public class Entry {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private final long id;

    // Each user has her own entries
    @JsonBackReference
    @ManyToOne
    private User user;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Objective objective;

    private LocalDateTime dateTimeFrom, dateTimeTo;
    private Duration duration;

    private String description;
}
