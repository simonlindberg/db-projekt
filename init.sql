drop database db;
create database db;
use db;

create table Ingredients (
    Ingredient      varchar(100)  not null,
    Quantity        integer       not null default 0 check (Quantity>=0),
    Unit            varchar(10)   not null,
    primary key (Ingredient)
);

create table IngredientDelivery (
    DeliveryID      integer       not null auto_increment,
    Ingredient      varchar(100)  not null,
    Date            datetime      not null default now(),
    Quantity        integer       not null check (Quantity > 0),
    primary key (DeliveryID),
    foreign key (Ingredient)      references Ingredients(Ingredient)
);

create table Recipes (
    ProductName     varchar(100)  not null,
    primary key (ProductName)
);

create table RecipeIngredients (
    ProductName     varchar(100)  not null,
    Ingredient      varchar(100)  not null,
    Quantity        integer       not null check (Quantity > 0),
    primary key (ProductName, Ingredient),
    foreign key (ProductName)     references Recipes(ProductName),
    foreign key (Ingredient)      references Ingredients(Ingredient) 
);

create table Pallets (
    PalletID        integer       not null auto_increment,
    ProductName     varchar(100)  not null,
    Blocked         bool          not null default 0,
    ProductionDate  datetime      not null default now(),
    primary key (PalletID),
    foreign key (ProductName)     references Recipes(ProductName)
);

create table Storage (
    PalletId        integer       not null,
    primary key (PalletID),
    foreign key (PalletID)        references Pallets(PalletID)
);


create table Customers (
    Customer        varchar(100)  not null,
    Address         varchar(101)  not null,
    primary key (Customer)
);

create table Orders (
    OrderID         integer       not null auto_increment,
    Customer        varchar(100)  not null,
    primary key (OrderID),
    foreign key (Customer)        references Customers(Customer)
);

create table PartialOrders (
    OrderID         integer       not null,
    ProductName     varchar(100)  not null,
    Quantity        integer       not null check (Quantity > 0),
    primary key (OrderID, ProductName),
    foreign key (OrderID)         references Orders(OrderID),
    foreign key (ProductName)     references Recipes(ProductName)
);

create table PalletDelivery (
    PalletID        integer       not null,
    OrderID         integer       not null,
    DeliveryDate    datetime      not null default now(),
    primary key (PalletID),
    foreign key (PalletID)        references Pallets(PalletID),
    foreign key (OrderID)         references Orders(OrderID)
);


insert into Ingredients (Ingredient, Unit)
values
("Flour","g"),
("Butter","g"),
("Icing sugar","g"),
("Roasted, chopped nuts","g"),
("Fine-ground nuts","g"),
("Ground, roasted nuts","g"),
("Bread crumbs","g"),
("Sugar","g"),
("Egg whites","dl"),
("Marzipan","g"),
("Potato starch","g"),
("Wheat ﬂour","g"),
("Sodium bicarbonate","g"),
("Vanilla","g"),
("Chopped almonds","g"),
("Cinnamon","g"),
("Eggs","g"),
("Vanilla sugar","g"),
("Chocolate","g");

