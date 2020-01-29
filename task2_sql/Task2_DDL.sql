DROP DATABASE task2;
CREATE DATABASE  task2;
use task2;
CREATE TABLE IF NOT EXISTS Customers 
(Customer_Id INTEGER NOT NULL  auto_increment  PRIMARY KEY,
 First_name VARCHAR(50) NOT NULL,
 Last_name VARCHAR(50) NOT NULL,
 City VARCHAR(50) NOT NULL,
 Pincode INTEGER NOT NULL);

INSERT INTO Customers VALUES (1,'Macus','Smith','Stockholm', 123);
INSERT INTO Customers VALUES (2,'Jim','King','Lund',345);
INSERT INTO Customers VALUES (3,'Kim','Ericsson','Uppsala',456);
INSERT INTO Customers VALUES (4,'John','Ken','Olso',678);
INSERT INTO Customers VALUES (5,'Mike','Fisherman','New York',890);

CREATE TABLE IF NOT EXISTS Categories(
  Category_id INTEGER NOT NULL  PRIMARY KEY,
  Category VARCHAR(50) NOT NULL 
);

INSERT INTO Categories VALUES (1,'Outdoors');
INSERT INTO Categories VALUES (2,'Sports');
INSERT INTO Categories VALUES (3,'Training');
INSERT INTO Categories VALUES (4,'Sandals');
INSERT INTO Categories VALUES (5,'Basketball');
INSERT INTO Categories VALUES (6,'Football');
INSERT INTO Categories VALUES (7,'Running');
INSERT INTO Categories VALUES (8,'Gym');
INSERT INTO Categories VALUES (9,'Sneaker');

CREATE TABLE IF NOT EXISTS Shoes 
(Shoe_id INTEGER NOT NULL auto_increment PRIMARY KEY,
    Color VARCHAR(10) NOT NULL,
    Size VARCHAR(10) NOT NULL,
    Brand VARCHAR(10) NOT NULL,
    Price INTEGER NOT NULL,
    Storage INTEGER NOT NULL);

INSERT INTO Shoes VALUES (1, 'Black', '38','Ecco',100,20);
INSERT INTO Shoes VALUES (2, 'White', '38','Ecco',100,15);
INSERT INTO Shoes VALUES (3, 'Brown', '42','Nike',200,10);
INSERT INTO Shoes VALUES (4, 'Yellow', '45','Puma',150,30);
INSERT INTO Shoes VALUES (5, 'Red', '39','Adidas',120,25);
INSERT INTO Shoes VALUES (6, 'Green', '42','Acics',230,26);
INSERT INTO Shoes VALUES (7, 'Black', '43','Ecco',100,34);
INSERT INTO Shoes VALUES (8, 'Blue', '40','Nike',50,50);

CREATE TABLE IF NOT EXISTS ShoeCategoryDetails(
  Shoe_id INTEGER NOT NULL,
  Category_id INTEGER NOT NULL,
  PRIMARY KEY (Shoe_id,Category_id),
  FOREIGN KEY (Shoe_id) REFERENCES Shoes(Shoe_id)
  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (Category_id) REFERENCES Categories(Category_id)
  ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO ShoeCategoryDetails VALUES (1,1);
INSERT INTO ShoeCategoryDetails VALUES (1,4);
INSERT INTO ShoeCategoryDetails VALUES (2,1);
INSERT INTO ShoeCategoryDetails VALUES (2,4);
INSERT INTO ShoeCategoryDetails VALUES (3,2);
INSERT INTO ShoeCategoryDetails VALUES (3,5);
INSERT INTO ShoeCategoryDetails VALUES (4,2);
INSERT INTO ShoeCategoryDetails VALUES (4,6);
INSERT INTO ShoeCategoryDetails VALUES (5,2);
INSERT INTO ShoeCategoryDetails VALUES (5,5);
INSERT INTO ShoeCategoryDetails VALUES (6,7);
INSERT INTO ShoeCategoryDetails VALUES (6,3);
INSERT INTO ShoeCategoryDetails VALUES (7,3);
INSERT INTO ShoeCategoryDetails VALUES (7,8);
INSERT INTO ShoeCategoryDetails VALUES (8,9);

CREATE TABLE IF NOT EXISTS Orders 
(Order_id INTEGER NOT NULL auto_increment PRIMARY KEY,
Customer INTEGER NOT NULL,
Order_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
FOREIGN KEY (Customer) REFERENCES Customers(Customer_Id)
);

INSERT INTO Orders VALUES (1,2,'20191201101236');
INSERT INTO Orders VALUES (2,1,'20191112051236');
INSERT INTO Orders VALUES (3,3,'20181001101236');
INSERT INTO Orders VALUES (4,5,'20180901101236');
INSERT INTO Orders VALUES (5,4,'20200101101236');
INSERT INTO Orders VALUES (6,2,'20190101101236');
INSERT INTO Orders VALUES (7,2,'20200102101236');

CREATE TABLE IF NOT EXISTS OrderDetails(
    Order_id INT NOT NUll,
    Shoe_id INT NOT NULL,
    Shoe_quantity Int NOT Null,
    PRIMARY KEY(Order_id, Shoe_id),
    FOREIGN KEY (Order_id) REFERENCES Orders(Order_id),
    FOREIGN KEY (Shoe_id) REFERENCES Shoes(Shoe_Id)
);

INSERT INTO OrderDetails VALUES (1, 2, 3);
INSERT INTO OrderDetails VALUES (1, 3, 2);
INSERT INTO OrderDetails VALUES (1, 5, 3);
INSERT INTO OrderDetails VALUES (2, 8, 2);
INSERT INTO OrderDetails VALUES (2, 4, 5);
INSERT INTO OrderDetails VALUES (3, 6, 10);
INSERT INTO OrderDetails VALUES (3, 3, 4);
INSERT INTO OrderDetails VALUES (4, 1, 3);
INSERT INTO OrderDetails VALUES (4, 6, 4);
INSERT INTO OrderDetails VALUES (5, 1, 3);
INSERT INTO OrderDetails VALUES (6, 3, 2);
INSERT INTO OrderDetails VALUES (6, 7, 5);
INSERT INTO OrderDetails VALUES (7, 2, 1);

CREATE TABLE IF NOT EXISTS Grades(
    Grade_Id INTEGER NOT NULL auto_increment PRIMARY KEY,
    Grade VARCHAR(50) NOT NULL
);

INSERT INTO Grades VALUES (1,'Mycket nöjd');
INSERT INTO Grades VALUES (2,'Nöjd');
INSERT INTO Grades VALUES (3,'Ganska Nöjd');
INSERT INTO Grades VALUES (4,'Missnöjd');

CREATE TABLE IF NOT EXISTS Comments(
    Comment_Id INT NOT NULL auto_increment PRIMARY KEY,
    Customer INT NOT NULL,
    Shoe INT NOT NULL,
    Grade INT NOT NULL,
    Comment VARCHAR(200) NOT NULL,
    FOREIGN KEY (Customer) REFERENCES Customers(Customer_Id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Shoe) REFERENCES Shoes(Shoe_Id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Grade) REFERENCES Grades(Grade_Id)
    ON UPDATE CASCADE ON DELETE CASCADE);

/*Test Grades*/
INSERT INTO Comments
VALUES
(1,1,1,1,'Perfect shoes');

INSERT INTO  Grades 
VALUES
(5,'Dålig');

INSERT INTO Comments
VALUES
(2,1,2,5,'So bad shoes');

/*Test Index*/
CREATE INDEX IX_comments_brand ON Shoes(Brand);
CREATE INDEX IX_comments_brand_compand ON Shoes(Brand,size,color);

/* show storage by category */
delimiter $
CREATE PROCEDURE showStorageByCategory(IN categoryType  varchar(50))
BEGIN
    SELECT SUM(Shoes.Storage), categoryType FROM Shoes  
    JOIN shoecategorydetails USING (shoe_id)
    JOIN Categories USING (Category_id)
    WHERE Categories.category = categoryType;
END 
$
delimiter ;

/* Create table to slutilager as stock out of storage*/
CREATE TABLE IF NOT EXISTS OutOfStorage 
(
  id int NOT NULL auto_increment PRIMARY KEY,
  endTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  shoe_id int NOT NULL,
  FOREIGN KEY (shoe_id) REFERENCES shoes(shoe_id)
);

/* Create trigger to check zero storage */
delimiter $
CREATE TRIGGER after_update_shoes AFTER UPDATE ON shoes
    FOR EACH ROW 
    BEGIN
      IF (NEW.storage = 0) THEN
          INSERT INTO OutOfStorage (endTime, shoe_id) 
          VALUES
          (now(), NEW.shoe_id);
      END IF;    
    END $ 
delimiter ;

/* AddToCart */
delimiter $
CREATE PROCEDURE AddTOCart (IN customerId int, IN orderId int, IN shoeId int)
BEGIN
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT ('An error has occurred, operation rollbacked and the stored procedure was terminated') as error;
    END; 

    IF(orderId IS NULL OR (NOT EXISTS(SELECT * FROM Orders WHERE Order_id = orderId))) THEN
        START TRANSACTION;
          INSERT INTO Orders (Customer,Order_date) VALUES (customerId, now());
          INSERT INTO OrderDetails VALUES (LAST_INSERT_ID(), shoeId, 1);
          update shoes set storage = storage -1 where shoe_id = shoeId;
        COMMIT;
    ELSEIF (EXISTS(SELECT * FROM Orders WHERE Order_id = orderId)) THEN
        IF (EXISTS(SELECT * FROM OrderDetails WHERE Shoe_id = shoeId AND Order_id = orderId)) THEN
            START TRANSACTION;
              UPDATE Shoes set Storage = Storage -1 where Shoe_id = shoeId;
              UPDATE Orders set Shoe_quantity = Shoe_quantity + 1 WHERE Shoe_id = shoeId AND Order_id = orderId; 
            COMMIT;
        ELSEIF (NOT EXISTS(SELECT * FROM OrderDetails WHERE Shoe_id = shoeId AND Order_id = orderId)) THEN
            START TRANSACTION;
              UPDATE Shoes set Storage = Storage -1 where Shoe_id = shoeId;
              INSERT INTO OrderDetails VALUES (OrderId, Shoeid,1);
            COMMIT;
        END IF;    
    END IF;   
END
$
delimiter ;