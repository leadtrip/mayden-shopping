create table shopping_list (
   id INT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   created_at DATETIME NOT NULL
);

create table shopping_list_item (
    id INT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
    shopping_list_id int not null,
    item_idx SMALLINT not null,
    created_at DATETIME NOT NULL,
    purchased boolean not null default FALSE,
    constraint fk_shopping_list foreign key (shopping_list_id) references shopping_list(id)
)