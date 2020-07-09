package be.odisee.ti2.se4.timesheet;

import be.odisee.ti2.se4.timesheet.dao.*;
import be.odisee.ti2.se4.timesheet.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * Database initializer that populates the database with some
 * initial data.
 *
 * This component is started only when app.db-init property is set to true
 */
@Component
@ConditionalOnProperty(name = "app.db-init", havingValue = "true")
public class TimesheetApplicationInitDB implements CommandLineRunner {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ObjectiveRepository objectiveRepository;

    @Autowired
    EntryRepository entryRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        List<User> users = Arrays.asList(
                new User(1, "imke",
                        "{bcrypt}$2a$10$2o9Frax4HHJLEMMb5iKs9ONs8zEmGv51IRIURY8PkBk7GsCxy4ixO",
                        "ROLE_USER","Imke", "Courtois","imke@courtois.be"),
                new User(2,"tessa",
                        "{bcrypt}$2a$10$vwKk.OxTjqkzVudfuIuUauxmIrcx8Miq6vFgmLF6sgRcu7viIxgGO",
                        "ROLE_USER","Tessa", "Wullaert", "tessa@wullaert.be"),
                new User(3, "nicky",
                        "{bcrypt}$2a$10$dTj8pIUJCTDi3kr.VnNRye1E7vmG7Yitx3IFFYrqAoEWwHWTJ1Zbu",
                        "ROLE_USER","Nicky", "Evrard", "nicky@evrard.be"),
                new User(4, "hans",
                        "{bcrypt}$2a$10$6OnHGe1AvHhR0vTRED7Obeh02YnQlUBEXENHoXsZ7v5EFigcVWrTm",
                        "ROLE_ADMIN","Hans", "Vandenbogaerde","hans@gmail.com")
        );

        for (User user: users) {

            // user must be saved for it to have an id
            userRepository.save(user);
            // There must be 1 dummy entry for each user to save the next startTimeFrom
            Entry dummyEntry = new Entry();
            dummyEntry.setUser(user);
            LocalDateTime now = LocalDateTime.now();
            dummyEntry.setDateTimeFrom(now);
            dummyEntry.setDateTimeTo(now);
            dummyEntry.setDuration(Duration.ZERO);
            dummyEntry.setDescription("This is your dummy entry, for housekeeping purposes");
            entryRepository.save(dummyEntry);

            user.setDummyEntry(dummyEntry);
            userRepository.save(user);
        }


        // For convenience each user gets a copy of the same categories, projects and objectives
        // Obiously, each user can have her own sets if this is desired
        for (User user : users) {

            List<Category> categories = Arrays.asList(
                    new Category("OVHD", user),
                    new Category("NETW", user),
                    new Category("PROSP", user),
                    new Category("FULF", user),
                    new Category("REND", user),
                    new Category("TRAVEL", user)
            );

            for (Category category : categories) {
                categoryRepository.save(category);
            };

            List<Project> projects = Arrays.asList(
                    new Project("General", categoryRepository.findCategoryByUserAndName(user,"OVHD"), user),
                    new Project("Infrastructure", categoryRepository.findCategoryByUserAndName(user,"OVHD"), user ),
                    new Project("Jazzzolder", categoryRepository.findCategoryByUserAndName(user,"NETW"), user ),
                    new Project("Jazzathome", categoryRepository.findCategoryByUserAndName(user,"NETW"), user ),
                    new Project("Jazzcontest", categoryRepository.findCategoryByUserAndName(user,"NETW"), user ),
                    new Project("Odisee", categoryRepository.findCategoryByUserAndName(user,"FULF"), user ),
                    new Project("EMS", categoryRepository.findCategoryByUserAndName(user,"FULF"), user ),
                    new Project("Syntra-AB", categoryRepository.findCategoryByUserAndName(user,"FULF"), user ),
                    new Project("SBM", categoryRepository.findCategoryByUserAndName(user,"FULF"), user ),
                    new Project("Microservices", categoryRepository.findCategoryByUserAndName(user,"REND"), user ),
                    new Project("Odisee", categoryRepository.findCategoryByUserAndName(user,"TRAVEL"), user )
            );

            for (Project project: projects) {
                projectRepository.save(project);
            }

            List<Objective> objectives = Arrays.asList(
                    new Objective("1. Omzet zelfstandig", user),
                    new Objective("2. Omzet digitaal", user),
                    new Objective("3. Digitale aanwezigheid", user),
                    new Objective("4. Talentdoelgroepen", user),
                    new Objective("5. Automatisering", user),
                    new Objective("6.Lean agile devops", user),
                    new Objective("7.DX in training", user),
                    new Objective("8. Training topics", user)
            );

            for (Objective objective: objectives) {
                objectiveRepository.save(objective);
            }
        }

    }
}
