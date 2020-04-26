package be.odisee.ti2.se4.timesheet.domain;

// Lombok library automatically generates getters, setters, equals(), hashCode(), toString() at runtime
import be.odisee.ti2.se4.timesheet.dao.CategoryRepository;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "PROJECTS")
@Data
@RequiredArgsConstructor  // generates constructor with required arguments - final fields and @NonNull-fields
@NoArgsConstructor(access= AccessLevel.PRIVATE,force=true)
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
