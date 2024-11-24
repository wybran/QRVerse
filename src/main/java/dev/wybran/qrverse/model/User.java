package dev.wybran.qrverse.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String givenName;

    @Nonnull
    private String familyName;

    private String picture;

    @Nonnull
    private String userName;

    @Nonnull
    private String email;

}