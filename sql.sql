/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.1-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: library
-- ------------------------------------------------------
-- Server version	11.8.1-MariaDB-4

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `access`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `access` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `method_type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `authors`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `authors` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `book_categories`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_categories` (
  `category_id` bigint(20) NOT NULL,
  `book_id` bigint(20) NOT NULL,
  PRIMARY KEY (`category_id`,`book_id`),
  KEY `FK3k3ahp5vqlgmrr9swqqprmbxy` (`book_id`),
  CONSTRAINT `FK3k3ahp5vqlgmrr9swqqprmbxy` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `FKrg2xlmc92mm2y5b1wmhd2g0y0` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `books`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfjixh2vym2cvfj3ufxj91jem7` (`author_id`),
  CONSTRAINT `FKfjixh2vym2cvfj3ufxj91jem7` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categories`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exemplaires`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `exemplaires` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `book_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpl6eufbyq7h9otdh9i7fgxgn0` (`code`),
  KEY `FKo9vnis4dxxwydq2y3ia2nb8sh` (`book_id`),
  CONSTRAINT `FKo9vnis4dxxwydq2y3ia2nb8sh` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `extend_loans`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `extend_loans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `confirmed_at` datetime(6) DEFAULT NULL,
  `rejected_at` datetime(6) DEFAULT NULL,
  `requested_at` datetime(6) DEFAULT NULL,
  `loan_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjmkrg12qblw52d2kwevll0l07` (`loan_id`),
  CONSTRAINT `FKjmkrg12qblw52d2kwevll0l07` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loan_status_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_status_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status_date` datetime(6) NOT NULL,
  `loan_id` bigint(20) NOT NULL,
  `loan_status_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKki173gj8ti12lpyxaooupajf7` (`loan_id`),
  KEY `FKcv8dx5199g5fs37r9uu2mxbb3` (`loan_status_type_id`),
  CONSTRAINT `FKcv8dx5199g5fs37r9uu2mxbb3` FOREIGN KEY (`loan_status_type_id`) REFERENCES `loan_status_types` (`id`),
  CONSTRAINT `FKki173gj8ti12lpyxaooupajf7` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loan_status_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_status_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loan_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loans`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `loans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `extend_count` int(11) NOT NULL DEFAULT 0,
  `extended_at` datetime(6) DEFAULT NULL,
  `loan_date` datetime(6) NOT NULL,
  `return_date` datetime(6) DEFAULT NULL,
  `exemplaire_id` bigint(20) NOT NULL,
  `loan_type_id` bigint(20) NOT NULL,
  `membership_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8xpxwrakgjsoqifn46beq98je` (`exemplaire_id`),
  KEY `FKg5kc6jabj9q90obnbbhusa9ef` (`loan_type_id`),
  KEY `FKig5yg7vrrsdcx9p96ly2exwbk` (`membership_id`),
  CONSTRAINT `FK8xpxwrakgjsoqifn46beq98je` FOREIGN KEY (`exemplaire_id`) REFERENCES `exemplaires` (`id`),
  CONSTRAINT `FKg5kc6jabj9q90obnbbhusa9ef` FOREIGN KEY (`loan_type_id`) REFERENCES `loan_types` (`id`),
  CONSTRAINT `FKig5yg7vrrsdcx9p96ly2exwbk` FOREIGN KEY (`membership_id`) REFERENCES `memberships` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `membership_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `max_books_allowed_home` int(11) NOT NULL,
  `max_books_allowed_library` int(11) NOT NULL,
  `max_extensions_allowed` int(11) NOT NULL DEFAULT 2,
  `max_time_hours_home` int(11) NOT NULL DEFAULT 0,
  `max_time_hours_library` int(11) NOT NULL DEFAULT 0,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `memberships`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `memberships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `membership_type_id` bigint(20) NOT NULL,
  `people_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfw7800cb1uobhmcxc4nm7fdof` (`membership_type_id`),
  KEY `FK16rrrx1outto9v851uwo4ru6m` (`people_id`),
  CONSTRAINT `FK16rrrx1outto9v851uwo4ru6m` FOREIGN KEY (`people_id`) REFERENCES `peoples` (`id`),
  CONSTRAINT `FKfw7800cb1uobhmcxc4nm7fdof` FOREIGN KEY (`membership_type_id`) REFERENCES `membership_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peoples`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `peoples` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `punishment_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `punishment_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `punishments`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `punishments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `duration_hours` float NOT NULL,
  `punishment_date` datetime DEFAULT current_timestamp(),
  `membership_id` bigint(20) NOT NULL,
  `punishment_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6e129aof0s19vles5f0wiok0f` (`membership_id`),
  KEY `FKbv1vle70cpg0iyscym3qpft1e` (`punishment_type_id`),
  CONSTRAINT `FK6e129aof0s19vles5f0wiok0f` FOREIGN KEY (`membership_id`) REFERENCES `memberships` (`id`),
  CONSTRAINT `FKbv1vle70cpg0iyscym3qpft1e` FOREIGN KEY (`punishment_type_id`) REFERENCES `punishment_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_status_histories`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_status_histories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status_date` datetime(6) DEFAULT NULL,
  `reservation_id` bigint(20) NOT NULL,
  `reservation_status_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1bln4wrjg9gy5p5c9m5ihc55k` (`reservation_id`),
  KEY `FKf03dxxihu4usdo7j851lq6m0j` (`reservation_status_type_id`),
  CONSTRAINT `FK1bln4wrjg9gy5p5c9m5ihc55k` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`),
  CONSTRAINT `FKf03dxxihu4usdo7j851lq6m0j` FOREIGN KEY (`reservation_status_type_id`) REFERENCES `reservation_status_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_status_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_status_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `color_class` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservations`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reservation_date` datetime(6) DEFAULT NULL,
  `take_home` bit(1) NOT NULL,
  `exemplaire_id` bigint(20) NOT NULL,
  `membership_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2dp9axk3u0j1on0jur6yxhoj5` (`exemplaire_id`),
  KEY `FKqs9g849adpbv6y7ar9gukm5bx` (`membership_id`),
  CONSTRAINT `FK2dp9axk3u0j1on0jur6yxhoj5` FOREIGN KEY (`exemplaire_id`) REFERENCES `exemplaires` (`id`),
  CONSTRAINT `FKqs9g849adpbv6y7ar9gukm5bx` FOREIGN KEY (`membership_id`) REFERENCES `memberships` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `returned_loan_state_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `returned_loan_state_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `returned_loan_states`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `returned_loan_states` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `notes` varchar(255) DEFAULT NULL,
  `loan_id` bigint(20) NOT NULL,
  `returned_loan_state_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrugotqv1lpii0r09xod9afy2d` (`loan_id`),
  KEY `FK7jmancc1erqxx5ak02r3okkwn` (`returned_loan_state_type_id`),
  CONSTRAINT `FK7jmancc1erqxx5ak02r3okkwn` FOREIGN KEY (`returned_loan_state_type_id`) REFERENCES `returned_loan_state_types` (`id`),
  CONSTRAINT `FKbem3fmv3ytwceg8x8hd6l4iao` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_accesses`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_accesses` (
  `role_id` bigint(20) NOT NULL,
  `access_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`access_id`),
  KEY `FKaypq59r8ost5r3m9oow7sg98d` (`access_id`),
  CONSTRAINT `FK8cepiu17usukpcmwopq67n7u8` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKaypq59r8ost5r3m9oow7sg98d` FOREIGN KEY (`access_id`) REFERENCES `access` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `people_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn1lj54bahv6ahvi76lrlcfndb` (`people_id`),
  CONSTRAINT `FKn1lj54bahv6ahvi76lrlcfndb` FOREIGN KEY (`people_id`) REFERENCES `peoples` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users_roles`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKj6m8fwv7oqv74fcehir1a9ffy` (`role_id`),
  CONSTRAINT `FK2o0jvgh89lemvvo17cbqvdxaa` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKj6m8fwv7oqv74fcehir1a9ffy` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-07-16  8:58:37
