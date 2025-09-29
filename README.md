### Mayden shopping list app

A spring boot shopping list app backed by a MySql database.\
Bring it all up with:

`./startup.sh`

Then navigate to:\
`http://localhost:9051/shoppingLists`

And log in with either user jon or sue who both have the password m4yD3N

### Testing
Unit and integration tests are handled by spock with groovy.\
Run all tests with:\
`./gradlew build`

### Improvements

* No item quantity recorded on items
* Removed this constraint due to issues with swapping items: CONSTRAINT uq_item_per_list UNIQUE (shopping_list_id, item_idx)