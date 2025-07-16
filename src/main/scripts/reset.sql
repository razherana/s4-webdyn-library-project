-- Reset access table
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM `books`;
ALTER TABLE `books` AUTO_INCREMENT = 1;

DELETE FROM `exemplaires`;
ALTER TABLE `exemplaires` AUTO_INCREMENT = 1;

DELETE FROM `book_categories`;
ALTER TABLE `book_categories` AUTO_INCREMENT = 1;

DELETE FROM `peoples`;
ALTER TABLE `peoples` AUTO_INCREMENT = 1;

DELETE FROM `memberships`;
ALTER TABLE `memberships` AUTO_INCREMENT = 1;

DELETE FROM `membership_types`;
ALTER TABLE `membership_types` AUTO_INCREMENT = 1;

DELETE FROM `punishments`;
ALTER TABLE `punishments` AUTO_INCREMENT = 1;

DELETE FROM `access`;
ALTER TABLE `access` AUTO_INCREMENT = 1;
INSERT INTO `access` (id, name, method_type, uri) VALUES
(1,'Home Dashboard','GET','/home'),
(2,'Books get','GET','^/books.*$'),
(3,'People links get','GET','^/user.*$'),
(4,'Books post','POST','^/books.*$'),
(5,'People post','POST','^/user.*$'),
(6,'Loans','GET','/loans');

-- Reset authors table
DELETE FROM `authors`;
ALTER TABLE `authors` AUTO_INCREMENT = 1;

-- Reset categories table
DELETE FROM `categories`;
ALTER TABLE `categories` AUTO_INCREMENT = 1;

-- Reset loan_status_types table
DELETE FROM `loan_status_types`;
ALTER TABLE `loan_status_types` AUTO_INCREMENT = 1;
INSERT INTO `loan_status_types` (id, name) VALUES
(1,'In loan'),
(2,'Late'),
(3,'Returned');

-- Reset loan_types table
DELETE FROM `loan_types`;
ALTER TABLE `loan_types` AUTO_INCREMENT = 1;
INSERT INTO `loan_types` (id, name) VALUES
(1,'Home'),
(2,'In-Library');

-- Reset punishment_types table
DELETE FROM `punishment_types`;
ALTER TABLE `punishment_types` AUTO_INCREMENT = 1;
INSERT INTO `punishment_types` (id, name) VALUES
(1,'Warning'),
(2,'Fine'),
(3,'Suspension'),
(4,'Ban');

-- Reset reservation_status_types table
DELETE FROM `reservation_status_types`;
ALTER TABLE `reservation_status_types` AUTO_INCREMENT = 4;
INSERT INTO `reservation_status_types` (id, name, color_class) VALUES
(1,'Pending',NULL),
(2,'Approved',NULL),
(3,'Cancelled',NULL);

-- Reset returned_loan_state_types table
DELETE FROM `returned_loan_state_types`;
ALTER TABLE `returned_loan_state_types` AUTO_INCREMENT = 4;
INSERT INTO `returned_loan_state_types` (id, description, name) VALUES
(1,'Normal book, good work by the guy','Normal'),
(2,'Damaged book, not good','Damaged'),
(3,'A book that is lost','Lost');

-- Reset roles table
DELETE FROM `roles`;
ALTER TABLE `roles` AUTO_INCREMENT = 1;
INSERT INTO `roles` (id, name, description) VALUES
(1, 'admin', 'admin'),
(2, 'librarian', 'librarian'),
(3, 'user', 'user');

-- Reset users table
DELETE FROM `users`;
ALTER TABLE `users` AUTO_INCREMENT = 1;
INSERT INTO `users` (id, people_id, username, password) VALUES
(1, null, 'admin', 'admin123'),
(2, null, 'librarian', 'librarian123');

DELETE FROM `users_roles`;
INSERT INTO `users_roles` (user_id, role_id) VALUES
(1, 1),  -- Admin user
(2, 2); 

DELETE FROM reservation_status_histories;
ALTER TABLE reservation_status_histories AUTO_INCREMENT = 1;

DELETE FROM loan_status_history;
ALTER TABLE loan_status_history AUTO_INCREMENT = 1;

DELETE FROM reservations;
ALTER TABLE reservations AUTO_INCREMENT = 1;

DELETE FROM returned_loan_states;
ALTER TABLE returned_loan_states AUTO_INCREMENT = 1;

DELETE FROM loans;
ALTER TABLE loans AUTO_INCREMENT = 1;

DELETE FROM punishments;
ALTER TABLE punishments AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;
