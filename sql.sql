-- Active: 1738011862925@@127.0.0.1@3306@library

DROP TABLE IF EXISTS `access`;
CREATE TABLE `access` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `method_type` varchar(255) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `access` (id, name, method_type, uri) VALUES
(1,'Home Dashboard','GET','/home'),
(2,'Books get','GET','^/books.*$'),
(3,'People links get','GET','^/user.*$'),
(4,'Books post','POST','^/books.*$'),
(5,'People post','POST','^/user.*$'),
(6,'Loans','GET','/loans');

DROP TABLE IF EXISTS `authors`;
CREATE TABLE `authors` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
INSERT INTO `authors` (id, name) VALUES
(1,'Razafindratandra Jean'),
(2,'Dupont Marie'),
(3,'Rakoto David'),
(4,'Smith John'),
(5,'Andrianaivo Pierre'),
(6,'Martin Sophie'),
(7,'Rabe Paul'),
(8,'Johnson Emily'),
(9,'Rakotonanahary Julie'),
(10,'Tremblay Luc'),
(11,'Williams Robert'),
(12,'Brown Linda'),
(13,'Jones Michael'),
(14,'Davis Jennifer'),
(15,'Miller Christopher'),
(16,'Wilson Jessica'),
(17,'Moore Matthew'),
(18,'Taylor Ashley'),
(19,'Anderson Joshua'),
(20,'Thomas Brittany');

CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

INSERT INTO `categories` (id, name) VALUES
(1,'Fiction'),
(2,'Non-Fiction'),
(3,'Science Fiction'),
(4,'Fantasy'),
(5,'Mystery'),
(6,'Romance'),
(7,'Horror'),
(8,'Biography'),
(9,'History'),
(10,'Self-Help');

CREATE TABLE `loan_status_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `loan_status_types` (id, name) VALUES
(1,'In loan'),
(2,'Late'),
(3,'Returned');

DROP TABLE IF EXISTS `loan_types`;
CREATE TABLE `loan_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `loan_types` (id, name) VALUES
(1,'Home'),
(2,'In-Library');


DROP TABLE IF EXISTS `membership_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `max_books_allowed_home` int(11) NOT NULL,
  `max_books_allowed_library` int(11) NOT NULL,
  `max_extensions_allowed` int(11) NOT NULL DEFAULT 2,
  `max_time_hours_home` int(11) NOT NULL DEFAULT 0,
  `max_time_hours_library` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

INSERT INTO `membership_types` (id, name, max_books_allowed_home, max_books_allowed_library, max_extensions_allowed, max_time_hours_home, max_time_hours_library) VALUES
(1,'Student',2,5,2,1,8),
(2,'Teacher',5,1000000,2,0,0),
(3,'Staff',1000000,1000000,2,0,0),
(4,'Visitor',0,5,2,0,0);

DROP TABLE IF EXISTS `punishment_types`;
CREATE TABLE `punishment_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `punishment_types` (id, name) VALUES
(1,'Warning'),
(2,'Fine'),
(3,'Suspension'),
(4,'Ban');

DROP TABLE IF EXISTS `reservation_status_types`;
CREATE TABLE `reservation_status_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `color_class` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
LOCK TABLES `reservation_status_types` WRITE;
INSERT INTO `reservation_status_types` (id, name, color_class) VALUES
(1,'Pending',NULL),
(2,'Approved',NULL),
(3,'Cancelled',NULL);

DROP TABLE IF EXISTS `returned_loan_state_types`;
CREATE TABLE `returned_loan_state_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
LOCK TABLES `returned_loan_state_types` WRITE;
INSERT INTO `returned_loan_state_types` (id, description, name) VALUES
(1,'Normal book, good work by the guy','Normal'),
(2,'Damaged book, not good','Damaged'),
(3,'A book that is lost','Lost');
INSERT INTO `roles` (id, name, description) VALUES
(1, 'admin', 'admin'),
(2, 'librarian', 'librarian'),
(3, 'user', 'user');
INSERT INTO `users` (id, people_id, username, password) VALUES
(1, null, 'admin', 'admin123'),
(2, null, 'librarian', 'librarian123');