USE Task2;
/* Test show storage by category */

call showStorageByCategory('Outdoors');
Update shoes set storage = 12 where shoe_id = 1;
call showStorageByCategory('Outdoors'); 

/* Test trigger */
Update shoes set storage = 3 where shoe_id = 1;
SELECT * FROM orders;
SELECT * FROM orderDetails;
/* order id is null */
call AddToCart(1,null,1);
SELECT * FROM orders;
SELECT * FROM orderDetails;
/* order id is not null and not exists */
call AddToCart(2,10,1);
SELECT * FROM orders;
SELECT * FROM orderDetails;
/* order id exists and shoe_id exists, and trigger works here to add a new post on table outofstorage */
call AddToCart(2,1,1);
SELECT * FROM orders;
SELECT * FROM orderDetails;
SELECT * FROM outofstorage;
/* order id exists and shoe_id not exists */
SELECT * FROM orders; 
call AddToCart(2,1,6);