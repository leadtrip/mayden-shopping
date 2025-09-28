package wood.mike.maydenshopping.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wood.mike.maydenshopping.dto.ShoppingListDTO;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.repository.AppUserRepository;
import wood.mike.maydenshopping.service.ShoppingListService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shoppingLists/{listId}/items")
public class ShoppingListItemController {

    private final ShoppingListService shoppingListService;
    private final AppUserRepository appUserRepository;

    public ShoppingListItemController(ShoppingListService shoppingListService,
                                      AppUserRepository appUserRepository) {
        this.shoppingListService = shoppingListService;
        this.appUserRepository = appUserRepository;
    }

    private AppUser getCurrentUser(UserDetails principal) {
        return appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @PostMapping
    public ModelAndView addItemFragment(@AuthenticationPrincipal UserDetails principal,
                                  @PathVariable Integer listId,
                                  @RequestBody Map<String, Object> payload,
                                  Model model) {
        AppUser user = getCurrentUser(principal);
        shoppingListService.addItemToList(user, listId,
                payload.get("itemName").toString(),
                Integer.parseInt(payload.get("price").toString()));

        return renderItemsFragment(listId);
    }

    @PostMapping("/{itemId}/toggle")
    public ModelAndView toggleItemFragment(@AuthenticationPrincipal UserDetails principal,
                                           @PathVariable Integer listId,
                                           @PathVariable Integer itemId) {
        AppUser user = getCurrentUser(principal);
        shoppingListService.togglePurchased(user, itemId);

        return renderItemsFragment(listId);
    }


    @PostMapping("/{itemId}/remove")
    public ModelAndView removeItemFragment(@AuthenticationPrincipal UserDetails principal,
                                     @PathVariable Integer listId,
                                     @PathVariable Integer itemId) {
        AppUser user = getCurrentUser(principal);
        shoppingListService.removeItem(user, itemId);
        return renderItemsFragment(listId);
    }

    @PostMapping("/{itemId}/move")
    public ModelAndView moveItemFragment(@AuthenticationPrincipal UserDetails principal,
                                         @PathVariable Integer listId,
                                         @PathVariable Integer itemId,
                                         @RequestParam String direction) {
        AppUser user = getCurrentUser(principal);
        ShoppingListDTO updatedList = shoppingListService.moveItem(user, listId, itemId, direction);

        return new ModelAndView(
                "shoppingListItems :: itemsTableFragment",
                Map.of("list", updatedList)
        );
    }

    private ModelAndView renderItemsFragment(Integer listId) {
        return new ModelAndView(
                "shoppingListItems :: itemsTableFragment",
                Map.of("list", shoppingListService.getListDto(listId)));
    }
}
