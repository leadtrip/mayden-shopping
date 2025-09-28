package wood.mike.maydenshopping.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private Integer spendingLimit;
}
