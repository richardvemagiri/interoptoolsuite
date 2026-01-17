/*
SQLyog Community
MySQL - 5.7.31 : Database - deid_app
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `deid_tool`;

/*Table structure for table `deidconfig` */

DROP TABLE IF EXISTS `deidconfig`;

CREATE TABLE `deidconfig` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `category` VARCHAR(255) NOT NULL,
    `delflag` INT(11) NOT NULL DEFAULT '0',
    `value` VARCHAR(255) NOT NULL,
    `xpath` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_5tdm7lhfynhet7lfr85nkh17f` (`xpath`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
