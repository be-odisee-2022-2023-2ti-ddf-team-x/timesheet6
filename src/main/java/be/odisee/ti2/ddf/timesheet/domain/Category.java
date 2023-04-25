package be.odisee.ti2.ddf.timesheet.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "CATEGORIES")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED,force=true)
public class Category {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private final String name;

    // Each user has her own categories
    @ManyToOne
    private final User user;
}
