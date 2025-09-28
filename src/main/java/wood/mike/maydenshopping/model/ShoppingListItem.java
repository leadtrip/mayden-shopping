package wood.mike.maydenshopping.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shopping_list_id", "item_idx"})
})
public class ShoppingListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Integer itemIdx;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean purchased = false;

    @Column(nullable = false)
    private Integer itemPrice;
}

