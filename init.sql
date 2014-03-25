drop database db;
create database db;
use db;

create table Ingredients (
    Ingredient      varchar(100)  not null,
    Quantity        float         not null default 0 check (Quantity>=0),
    Unit            varchar(10)   not null,
    primary key (Ingredient)
);

create table IngredientDelivery (
    DeliveryID      integer       not null auto_increment,
    Ingredient      varchar(100)  not null,
    Date            datetime      not null default now(),
    Quantity        float         not null check (Quantity > 0),
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
    Quantity        float         not null check (Quantity > 0),
    primary key (ProductName, Ingredient),
    foreign key (ProductName)     references Recipes(ProductName),
    foreign key (Ingredient)      references Ingredients(Ingredient) 
);

create table Pallets (
    PalletID        integer       not null,
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

insert into Customers (Customer, Address) 
values 
("Finkakor AB", "Helsingborg"), 
("Smabröd AB", "Malmö"), 
("Kaffebröd AB", "Landskrona"), 
("Bjudkakor AB", "Ystad"), 
("Kalaskakor AB", "Trelleborg"), 
("Partykakor AB", "Kristianstad"), 
("Gästkakor AB", "Hässleholm"), 
("Skånekakor AB", "Perstorp");

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
("Wheat flour","g"),
("Sodium bicarbonate","g"),
("Vanilla","g"),
("Chopped almonds","g"),
("Cinnamon","g"),
("Eggs","g"),
("Vanilla sugar","g"),
("Chocolate","g");

--update Ingredients
--set Quantity = 10000;

insert into recipes (productName)
values 
("Nut ring"), 
("Nut cookie"), 
("Amneris"), 
("Tango"), 
("Almond delight"), 
("Berliner");

insert into RecipeIngredients (ProductName, Ingredient, Quantity)
values 
("Nut ring", "Flour", 450),
("Nut ring", "Butter", 450),
("Nut ring", "Icing sugar", 190),
("Nut ring", "Roasted, chopped nuts", 225),
("Nut cookie", "Fine-ground nuts", 750),
("Nut cookie", "Ground, roasted nuts", 625),
("Nut cookie", "Bread crumbs", 125),
("Nut cookie", "Sugar", 375),
("Nut cookie", "Egg whites", 3.5),
("Nut cookie", "Chocolate", 50),
("Amneris", "Marzipan", 200),
("Amneris", "Butter", 250),
("Amneris", "Eggs", 250),
("Amneris", "Potato starch", 25),
("Amneris", "Wheat flour", 25),
("Tango", "Butter", 200),
("Tango", "Sugar", 250),
("Tango", "Flour", 300),
("Tango", "Sodium bicarbonate", 4),
("Tango", "Vanilla", 2),
("Almond delight", "Butter", 400),
("Almond delight", "Sugar", 270),
("Almond delight", "Chopped almonds", 279),
("Almond delight", "Flour", 400),
("Almond delight", "Cinnamon", 10),
("Berliner", "Flour", 350),
("Berliner", "Butter", 250),
("Berliner", "Icing sugar", 100),
("Berliner", "Eggs", 50),
("Berliner", "Vanilla sugar", 5),
("Berliner", "Chocolate", 50);

