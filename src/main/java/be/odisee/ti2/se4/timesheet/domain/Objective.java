package be.odisee.ti2.se4.timesheet.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "OBJECTIVES")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access= AccessLevel.PRIVATE,force=true)
public class Objective {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private final String name;

    // Each user has her own objectives
    @ManyToOne
    private final User user;
}
