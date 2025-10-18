package FinanceApp.FinanceApp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.UUID;

/**
 * @NoArgsConstructor, @AllArgsConstructor, and @Data annotations belong to the Lombok library.
 * The first two, as implied by their names, automatically generate a default constructor and another
 * constructor containing all the attributes, respectively. The @Data annotation, on the other hand,
 * generates getters, setters, toString(), and other essential methods.
 * When using @Entity, JPA assumes the existence of a corresponding table in the database to store instances of this entity.
 * The @Table annotation allows customization by specifying the table name, schema, and additional attributes.
 *
 * The @Id annotation is employed to declare the primary key of an entity class.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    private UUID id;
    private String oktaUserId; //irgendwie Unique machen
    private String email;
    private Timestamp createdAt;

}
