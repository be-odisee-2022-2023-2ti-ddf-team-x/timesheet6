package be.odisee.ti2.ddf.timesheet.domain;

// Lombok library automatically generates getters, setters, equals(), hashCode(), toString() at runtime
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "PROJECTS")
@Data
@RequiredArgsConstructor  // generates constructor with required arguments - final fields and @NonNull-fields
@NoArgsConstructor(access= AccessLevel.PROTECTED,force=true)
public class Project {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private final String name;

    @ManyToOne
    private final Category category;

    // Each user has her own projects
    @ManyToOne
    private final User user;
}
