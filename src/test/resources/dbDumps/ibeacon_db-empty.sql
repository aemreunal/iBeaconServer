-- MySQL dump 10.13  Distrib 5.6.20, for osx10.9 (x86_64)
--
-- Host: localhost    Database: ibeacon_db
-- ------------------------------------------------------
-- Server version	5.6.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Position to start replication or point-in-time recovery from
--

CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000001', MASTER_LOG_POS=120;

--
-- Current Database: `ibeacon_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ibeacon_db` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `ibeacon_db`;

--
-- Table structure for table `beacon_groups`
--

DROP TABLE IF EXISTS `beacon_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `beacon_groups` (
  `beacon_group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `description` varchar(200) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`beacon_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beacon_groups`
--

LOCK TABLES `beacon_groups` WRITE;
/*!40000 ALTER TABLE `beacon_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `beacon_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `beacon_groups_to_beacons`
--

DROP TABLE IF EXISTS `beacon_groups_to_beacons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `beacon_groups_to_beacons` (
  `beacon_group_id` bigint(20) DEFAULT NULL,
  `beacon_id` bigint(20) NOT NULL,
  PRIMARY KEY (`beacon_id`),
  KEY `FK_72qra71pq5uj5fiatq0qrcttc` (`beacon_group_id`),
  CONSTRAINT `FK_72qra71pq5uj5fiatq0qrcttc` FOREIGN KEY (`beacon_group_id`) REFERENCES `beacon_groups` (`beacon_group_id`),
  CONSTRAINT `FK_dwui3oqknpdv1b0oy3ycp0mky` FOREIGN KEY (`beacon_id`) REFERENCES `beacons` (`beacon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beacon_groups_to_beacons`
--

LOCK TABLES `beacon_groups_to_beacons` WRITE;
/*!40000 ALTER TABLE `beacon_groups_to_beacons` DISABLE KEYS */;
/*!40000 ALTER TABLE `beacon_groups_to_beacons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `beacons`
--

DROP TABLE IF EXISTS `beacons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `beacons` (
  `beacon_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `description` varchar(200) NOT NULL,
  `major` varchar(4) NOT NULL,
  `minor` varchar(4) NOT NULL,
  `uuid` varchar(36) NOT NULL,
  PRIMARY KEY (`beacon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beacons`
--

LOCK TABLES `beacons` WRITE;
/*!40000 ALTER TABLE `beacons` DISABLE KEYS */;
/*!40000 ALTER TABLE `beacons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projects` (
  `project_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `description` varchar(200) NOT NULL,
  `name` varchar(50) NOT NULL,
  `project_secret` varchar(36) NOT NULL,
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects`
--

LOCK TABLES `projects` WRITE;
/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects_to_beacon_groups`
--

DROP TABLE IF EXISTS `projects_to_beacon_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projects_to_beacon_groups` (
  `project_id` bigint(20) NOT NULL,
  `beacon_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`beacon_group_id`),
  KEY `FK_g27bfy0qbn4nya0p8pneij8mp` (`project_id`),
  CONSTRAINT `FK_g27bfy0qbn4nya0p8pneij8mp` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`),
  CONSTRAINT `FK_k6yt0f13pj1ej5nfg9gauv8or` FOREIGN KEY (`beacon_group_id`) REFERENCES `beacon_groups` (`beacon_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects_to_beacon_groups`
--

LOCK TABLES `projects_to_beacon_groups` WRITE;
/*!40000 ALTER TABLE `projects_to_beacon_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `projects_to_beacon_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects_to_beacons`
--

DROP TABLE IF EXISTS `projects_to_beacons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projects_to_beacons` (
  `project_id` bigint(20) NOT NULL,
  `beacon_id` bigint(20) NOT NULL,
  PRIMARY KEY (`beacon_id`),
  KEY `FK_mobdvlm8yfy2fbrr76kkv1mgq` (`project_id`),
  CONSTRAINT `FK_mobdvlm8yfy2fbrr76kkv1mgq` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`),
  CONSTRAINT `FK_o5m08m6shno43pebuoeiiwr5y` FOREIGN KEY (`beacon_id`) REFERENCES `beacons` (`beacon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects_to_beacons`
--

LOCK TABLES `projects_to_beacons` WRITE;
/*!40000 ALTER TABLE `projects_to_beacons` DISABLE KEYS */;
/*!40000 ALTER TABLE `projects_to_beacons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scenarios`
--

DROP TABLE IF EXISTS `scenarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scenarios` (
  `scenario_id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`scenario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenarios`
--

LOCK TABLES `scenarios` WRITE;
/*!40000 ALTER TABLE `scenarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `scenarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-12 14:44:10
