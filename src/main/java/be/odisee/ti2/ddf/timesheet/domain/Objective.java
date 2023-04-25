package be.odisee.ti2.ddf.timesheet.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "OBJECTIVES")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED,force=true)
public class Objective {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private final String name;

    // Each user has her own objectives
    @ManyToOne
    private final User user;
}
