package be.odisee.ti2.ddf.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name = "USERS")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED,force=true)
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private final long id;

    @Column(unique = true)
    private final String username;
    // Must be {bcrypt}-encoded
    private final String password;
    private final String role;

    private final String firstName;
    private final String lastName;

    private final String email;

    // will be used to recover dateTimeFrom for a new entry
    // so a dummy Entry must be available for each user
    @JsonManagedReference
    @OneToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Entry dummyEntry;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
