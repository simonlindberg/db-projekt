CREATE TABLE Ingredients
(
Ingredient VARCHAR(100) PRIMARY KEY,
Quantity INT NOT NULL CHECK (Quantity>=0),
Unit VARCHAR(10) NOT NULL
);

CREATE TABLE IngredientDelivery 
(
DeliveryID INT auto_increment PRIMARY KEY,
Ingredient VARCHAR(100) REFERENCES Ingredients(Ingredient),
Date datetime NOT NULL DEFAULT NOW(),
Quantity INT NOT NULL CHECK (Quantity>0)

);

CREATE TABLE Recipes
(
ProductName VARCHAR(100) PRIMARY KEY;
);

CREATE TABLE RecipeIngredients 
(
ProductName VARCHAR(100) REFERENCES Recipes(ProductName),
Ingredient VARCHAR(100) REFERENCES Ingridients(Ingredient),
Quantity INT NOT NULL CHECK (Quantity>0),
PRIMARY KEY (ProductName, Ingredient)
);

CREATE TABLE Pallets 
(
PalletID INT auto_increment PRIMARY KEY,
ProductName VARCHAR(100) REFERENCES Recipes(ProductName),
Blocked BOOL NOT NULL DEFAULT 0,
ProductionDate datetime NOT NULL DEFAULT NOW()
);

CREATE TABLE Storage
(
PalletId INT REFERENCES(Pallets(PalletID)) PRIMARY KEY,
);


CREATE TABLE Customers
(
Customer VARCHAR(100) NOT NULL PRIMARY KEY,
Address VARCHAR(101) NOT NULL
);

CREATE TABLE Orders
(
OrderID INT auto_increment PRIMARY KEY
Customer VARCHAR(100) REFERENCES Customers(Customer)
);

CREATE TABLE PartialOrders 
(
OrderID INT REFERENCES(Orders(OrderID))
ProductName VARCHAR(100) REFERENCES Recipes(ProductName) 
Quantity INT NOT NULL CHECK (Quantity>0),
PRIMARY KEY (OrderID, ProductName) 
);

CREATE TABLE PalletDelivery
(
PalletID INT REFERENCES Pallets(PalletID) PRIMARY KEY,
OrderID INT REFERENCES Orders(OrderID),
DeliveryDate datetime NOT NULL DEFAULT NOW()
);
