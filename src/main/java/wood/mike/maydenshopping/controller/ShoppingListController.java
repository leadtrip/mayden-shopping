package wood.mike.maydenshopping.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import wood.mike.maydenshopping.dto.ShoppingListDTO;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.model.ShoppingList;
import wood.mike.maydenshopping.repository.AppUserRepository;
import wood.mike.maydenshopping.service.ShoppingListService;

@Slf4j
@Controller
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final AppUserRepository appUserRepository;

    public ShoppingListController(ShoppingListService shoppingListService, 
                                  AppUserRepository appUserRepository) {
        this.shoppingListService = shoppingListService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/shoppingLists")
    public String shoppingLists(Model model, @AuthenticationPrincipal UserDetails principal) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("pageTitle", "Shopping Lists");
        model.addAttribute("templateName", "shoppingLists");
        model.addAttribute("shoppingLists", shoppingListService.getListsForUser(user));
        return "layout";
    }

    @GetMapping("/shoppingLists/{id}")
    public String shoppingLists(Model model, @AuthenticationPrincipal UserDetails principal, @PathVariable long id) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        ShoppingListDTO list = shoppingListService.getListForUserById(user, id)
                .orElseThrow(() -> new IllegalArgumentException("List not found or not yours"));

        model.addAttribute("pageTitle", "Shopping List");
        model.addAttribute("templateName", "shoppingListDetail");
        model.addAttribute("list", list);
        return "layout";
    }

    @PostMapping("/shoppingLists/create")
    public String createNewShoppingList(@AuthenticationPrincipal UserDetails principal) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        ShoppingList newList = shoppingListService.createList(user);

        return "redirect:/shoppingLists/" + newList.getId();
    }

}
