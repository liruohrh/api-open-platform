-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: api_platform
-- ------------------------------------------------------
-- Server version	5.7.44-log
create database if not exists api_platform;
use api_platform;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `api_call`
--

DROP TABLE IF EXISTS `api_call`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_call` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api_id` bigint(20) NOT NULL,
  `caller_id` bigint(20) NOT NULL,
  `left_times` int(11) NOT NULL DEFAULT '-1' COMMENT '-1表示免费',
  `free_used` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_call`
--

/*!40000 ALTER TABLE `api_call` DISABLE KEYS */;
INSERT INTO `api_call` VALUES (1,41,1,10,0);
/*!40000 ALTER TABLE `api_call` ENABLE KEYS */;

--
-- Table structure for table `api_call_log`
--

DROP TABLE IF EXISTS `api_call_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_call_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api_id` bigint(20) NOT NULL,
  `caller_id` bigint(20) NOT NULL,
  `success` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0-失败，1-成功',
  `time_consuming_ms` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_call_log`
--

/*!40000 ALTER TABLE `api_call_log` DISABLE KEYS */;
INSERT INTO `api_call_log` VALUES (1,41,1,0,3242),(2,41,1,0,357),(3,41,1,0,290),(4,41,1,0,237),(5,41,1,0,227),(6,41,1,0,316),(7,41,1,0,176),(8,41,1,0,290),(9,41,3,0,2144),(10,41,3,0,295),(11,1,1,1,35126),(12,1,1,1,11048),(13,1,1,1,2418),(14,1,1,1,2418),(15,1,1,1,2418),(16,41,1,1,631),(17,41,1,1,250),(18,41,1,1,479),(19,41,1,1,469),(20,41,1,1,181),(21,41,1,1,181),(22,41,1,1,17),(23,41,1,1,492),(24,41,1,1,11),(25,41,1,0,2020),(26,41,1,0,2389),(27,41,1,0,2546),(28,41,1,0,3058),(29,41,1,0,3233);
/*!40000 ALTER TABLE `api_call_log` ENABLE KEYS */;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application` (
  `id` bigint(20) NOT NULL,
  `application_type` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '申请类型',
  `title` varchar(32) COLLATE utf8_bin NOT NULL,
  `content` varchar(512) COLLATE utf8_bin NOT NULL,
  `reply_content` varchar(512) COLLATE utf8_bin NOT NULL DEFAULT '',
  `audit_status` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '根据不同申请类型做出不同审核状态回调，0-待审核，1-审核通过，2-审核不通过',
  `ctime` bigint(20) NOT NULL,
  `utime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

/*!40000 ALTER TABLE `application` DISABLE KEYS */;
/*!40000 ALTER TABLE `application` ENABLE KEYS */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL,
  `api_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `score` tinyint(4) NOT NULL,
  `content` varchar(512) COLLATE utf8_bin NOT NULL,
  `ctime` bigint(20) NOT NULL,
  `deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;

--
-- Table structure for table `goods_order`
--

DROP TABLE IF EXISTS `goods_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `api_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `actual_payment` float NOT NULL,
  `amount` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL COMMENT '0-待支付，1-已支付，2-已取消',
  `price` float NOT NULL DEFAULT '0' COMMENT '单价',
  `ctime` bigint(20) NOT NULL,
  `utime` bigint(20) NOT NULL,
  `deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_order`
--

/*!40000 ALTER TABLE `goods_order` DISABLE KEYS */;
INSERT INTO `goods_order` VALUES (1,'12345678901234567890123456789012',41,1,0,0,2,0,0,0,0),(2,'12345678901234567890123456789023',21,1,1,1,1,1,1,1,0);
/*!40000 ALTER TABLE `goods_order` ENABLE KEYS */;

--
-- Table structure for table `http_api`
--

DROP TABLE IF EXISTS `http_api`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `http_api` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) NOT NULL,
  `description` varchar(1024) COLLATE utf8_bin NOT NULL DEFAULT '',
  `name` varchar(128) COLLATE utf8_bin NOT NULL,
  `protocol` varchar(16) COLLATE utf8_bin NOT NULL DEFAULT '',
  `domain` varchar(512) COLLATE utf8_bin NOT NULL DEFAULT '',
  `path` varchar(1024) COLLATE utf8_bin NOT NULL DEFAULT '',
  `method` varchar(8) COLLATE utf8_bin NOT NULL COMMENT '大写',
  `params` varchar(1024) COLLATE utf8_bin NOT NULL DEFAULT '',
  `req_headers` varchar(1024) COLLATE utf8_bin NOT NULL DEFAULT '',
  `req_body` text COLLATE utf8_bin,
  `resp_headers` varchar(1024) COLLATE utf8_bin NOT NULL DEFAULT '',
  `resp_body` text COLLATE utf8_bin,
  `resp_success` text COLLATE utf8_bin,
  `resp_fail` text COLLATE utf8_bin,
  `error_codes` text COLLATE utf8_bin,
  `price` float NOT NULL DEFAULT '0' COMMENT '最多3位小数',
  `free_times` int(11) NOT NULL DEFAULT '0',
  `logo_url` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `score` int(11) NOT NULL DEFAULT '0',
  `order_volume` int(11) NOT NULL DEFAULT '0' COMMENT '成交量',
  `invoke_count` bigint(20) NOT NULL DEFAULT '0',
  `comment_count` int(11) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-待审核，1-上线，2-下线，3-被禁用',
  `ctime` bigint(20) NOT NULL,
  `utime` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `http_api_pk` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `http_api`
--

/*!40000 ALTER TABLE `http_api` DISABLE KEYS */;
INSERT INTO `http_api` VALUES (21,1,'Get user information','GetUserInfo','http','127.0.0.1:9001','/v1/user/info','GET','','','','','','','','',0,-1,'',0,0,0,0,1,1749104407297,1749104407297,0),(22,1,'Create new user','CreateUser','http','127.0.0.1:9001','/v1/user/create','POST','{\"name\":\"string\", \"email\":\"string\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(23,1,'Update user information','UpdateUser','http','127.0.0.1:9001','/v1/user/update','PUT','{\"id\":\"int\", \"email\":\"string\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(24,1,'Delete user','DeleteUser','http','127.0.0.1:9001','/v1/user/delete','DELETE','{\"id\":\"int\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(25,1,'Get list of users','ListUsers','http','127.0.0.1:9001','/v1/users','GET','','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(26,1,'Upload user profile picture','UploadProfilePic','http','127.0.0.1:9001','/v1/user/upload/pic','POST','','','{\"Content-Type\":\"multipart/form-data\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(27,1,'Reset user password','ResetPassword','http','127.0.0.1:9001','/v1/user/reset-password','POST','{\"email\":\"string\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(28,1,'Activate user account','ActivateAccount','http','127.0.0.1:9001','/v1/user/activate','POST','{\"token\":\"string\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(29,1,'Get user activity log','GetUserActivity','http','127.0.0.1:9001','/v1/user/activity','GET','{\"id\":\"int\"}','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(30,1,'Get user settings','GetUserSettings','http','127.0.0.1:9001','/v1/user/settings','GET','{\"id\":\"int\"}','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(31,1,'Update user settings','UpdateUserSettings','http','127.0.0.1:9001','/v1/user/settings/update','PUT','{\"id\":\"int\", \"settings\":\"string\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(32,1,'Get list of products','ListProducts','http','127.0.0.1:9001','/v1/products','GET','','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(33,1,'Get product details','GetProductDetails','http','127.0.0.1:9001','/v1/product/details','GET','{\"id\":\"int\"}','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(34,1,'Create new product','CreateProduct','http','127.0.0.1:9001','/v1/product/create','POST','{\"name\":\"string\", \"price\":\"float\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(35,1,'Update product details','UpdateProduct','http','127.0.0.1:9001','/v1/product/update','PUT','{\"id\":\"int\", \"price\":\"float\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(36,1,'Delete product','DeleteProduct','http','127.0.0.1:9001','/v1/product/delete','DELETE','{\"id\":\"int\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(37,1,'Get product reviews','GetProductReviews','http','127.0.0.1:9001','/v1/product/reviews','GET','{\"id\":\"int\"}','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(38,1,'Add product review','AddProductReview','http','127.0.0.1:9001','/v1/product/review/add','POST','{\"id\":\"int\", \"review\":\"string\"}','','{\"Content-Type\":\"application/json\"}','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(39,1,'Get user orders','GetUserOrders','http','127.0.0.1:9001','/v1/user/orders','GET','{\"userId\":\"int\"}','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(40,1,'Get order details','GetOrderDetails','http','127.0.0.1:9001','/v1/order/details','GET','{\"orderId\":\"int\"}','','','','','','','',0,-1,'',0,0,0,0,0,1749104407297,1749104407297,0),(41,1,'天气预报','天气预报','http','127.0.0.1:9001','/api/web/weather','GET','{\n  \"city\": {\n    \"type\": \"string\",\n    \"required\": false,\n    \"desc\": \"城市，城市、ip二选一\"\n  },\n  \"ip\": {\n    \"type\": \"string\",\n    \"required\": false,\n    \"desc\": \"ip\"\n  },\n  \"type\": {\n    \"type\": \"string\",\n    \"required\": false,\n    \"desc\": \"默认一天，还支持week（周）\"\n  }\n}','',NULL,'','{\n  \"code\": {\n    \"option\": false,\n    \"type\": \"integer\",\n    \"desc\": \"响应码\"\n  },\n  \"data\": {\n    \"option\": true,\n    \"type\": \"object\",\n    \"nestedType\": {\n      \"city\": {\n        \"option\": false,\n        \"type\": \"string\",\n        \"desc\": \"城市\"\n      },\n      \"weatherList\": [\n        {\n          \"date\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"日期\"\n          },\n          \"week\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"星期几\"\n          },\n          \"type\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"天气类型\"\n          },\n          \"lowDegreesCelsius\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"最低温度\"\n          },\n          \"highDegreesCelsius\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"最高温度\"\n          },\n          \"windDirection\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"风向\"\n          },\n          \"windForce\": {\n            \"option\": false,\n            \"type\": \"string\",\n            \"desc\": \"风级\"\n          },\n          \"night\": {\n            \"desc\": \"也是weatherList的元素类型\"\n          }\n        }\n      ],\n      \"air\": {\n        \"aqi\": {\n          \"option\": false,\n          \"type\": \"integer\",\n          \"desc\": \"空气质量指数\"\n        },\n        \"aqiLevel\": {\n          \"option\": true,\n          \"type\": \"integer\",\n          \"desc\": \" 空气质量等级\"\n        },\n        \"aqiName\": {\n          \"option\": false,\n          \"type\": \"string\",\n          \"desc\": \" 空气质量等级名称\"\n        },\n        \"co\": {\n          \"option\": false,\n          \"type\": \"string\",\n          \"desc\": \" 一氧化碳浓度\"\n        },\n        \"no2\": {\n          \"option\": false,\n          \"type\": \"string\",\n          \"desc\": \"  二氧化氮浓度\"\n        },\n        \"o3\": {\n          \"option\": false,\n          \"type\": \"string\",\n          \"desc\": \" 臭氧浓度\"\n        },\n        \"pm10\": {\n          \"option\": false,\n          \"type\": \"string\",\n          \"desc\": \" 可吸入颗粒物(PM10)浓度\"\n        },\n        \"pm25\": {\n          \"option\": false,\n          \"type\": \"string\",\n          \"desc\": \"  细颗粒物(PM2.5)浓度\"\n        },\n        \"so2\": {\n          \"option\": true,\n          \"type\": \"string\",\n          \"desc\": \" 二氧化硫浓度\"\n        }\n      }\n    }\n  },\n  \"message\": {\n    \"option\": true,\n    \"type\": \"string\",\n    \"desc\": \"错误信息\"\n  }\n}','{\n  \"code\": 0,\n  \"msg\": null,\n  \"data\": {\n    \"city\": \"广州市\",\n    \"weatherList\": [\n      {\n        \"city\": null,\n        \"date\": \"2024-08-16\",\n        \"week\": \"星期五\",\n        \"type\": \"小雨\",\n        \"lowDegreesCelsius\": \"25°C\",\n        \"highDegreesCelsius\": \"33°C\",\n        \"windDirection\": \"南风\",\n        \"windForce\": \"1-3级\",\n        \"night\": {\n          \"city\": null,\n          \"date\": null,\n          \"week\": null,\n          \"type\": \"中到大雨\",\n          \"lowDegreesCelsius\": null,\n          \"highDegreesCelsius\": null,\n          \"windDirection\": \"微风\",\n          \"windForce\": \"1-3级\",\n          \"night\": null,\n          \"airQuality\": null\n        },\n        \"airQuality\": null\n      }\n    ],\n    \"air\": {\n      \"aqi\": 18,\n      \"aqiLevel\": 0,\n      \"aqiName\": null,\n      \"co\": \"1\",\n      \"no2\": \"24\",\n      \"o3\": \"18\",\n      \"pm10\": \"18\",\n      \"pm25\": null,\n      \"so2\": \"5\"\n    }\n  }\n}','{\n  \"code\": 4000,\n  \"msg\": \"client param error, 城市和ip只能二选一\",\n  \"data\": null\n}','{\n    \"4000\": {\n\"httpStatus\": 400,\n “desc”: \"客户端参数错误\"    \n},\n    \"50001\": {\n\"httpStatus\": 500,\n\"desc\": \"系统繁忙，需要等待一段时间后才能请求，或者可能服务器出现问题，暂时无法使用\"\n}\n}',0,-1,'http://127.0.0.1:8080/api-platform/oss/static/imgs/7a2a1678dd9106bef66d52f203c76a45.png',0,0,14,0,1,1749104428097,1749104428097,0);
/*!40000 ALTER TABLE `http_api` ENABLE KEYS */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(32) COLLATE utf8_bin NOT NULL,
  `username` varchar(32) COLLATE utf8_bin NOT NULL,
  `passwd` varchar(32) COLLATE utf8_bin NOT NULL,
  `email` varchar(64) COLLATE utf8_bin NOT NULL,
  `personal_description` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `avatar_url` varchar(1024) COLLATE utf8_bin NOT NULL DEFAULT '',
  `app_key` varchar(512) COLLATE utf8_bin NOT NULL,
  `app_secret` varchar(512) COLLATE utf8_bin NOT NULL,
  `api_prefix` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所有API的前缀，默认username',
  `role` varchar(16) COLLATE utf8_bin NOT NULL DEFAULT 'USER',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-正常，1-冻结用户，2-冻结服务商，3-冻结用户+服务商',
  `ctime` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `app_key` (`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'test1','system','7a278ee6b95e4cd5c74036097f548beb','2372221537@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2b7bbe6309f4ea98648eb50694e083a4.jpeg','55d8086d574abf74e37bd73710675bac','41f2e9909ad1b48080bdafaa4186e152','','ADMIN',0,1723302784686,0),(3,'test2','test2','7a278ee6b95e4cd5c74036097f548beb','123@qq.com','各个ttt','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','c4ca4238a0b923820dcc509a6f75849b','5147ac4ce42e5cb2fd63a5fb8a5b03af','','USER',0,1723302784686,0),(4,'test3','test3','7a278ee6b95e4cd5c74036097f548beb','234@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/7294316754670b320c64db1564cd350f.jpeg','c81e728d9d4c2f636f067f89cc14862c','5147ac4ce42e5cb2fd63a5fb8a5b03af','','USER',0,1723302784686,0),(5,'test4','test4','7a278ee6b95e4cd5c74036097f548beb','345@qq.com','各个tttttttt','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','a87ff679a2f3e71d9181a67b7542122c','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723302784687,0),(6,'test5','test5','7a278ee6b95e4cd5c74036097f548beb','456@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/d2edc6ebd9436e419b60ed001b974efd.jpeg','e4da3b7fbbce2345d7772b0674a318d5','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723303784687,0),(7,'test6','test6','7a278ee6b95e4cd5c74036097f548beb','567@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','1679091c5a880faf6fb5e6087eb1b2dc','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723303784687,0),(8,'test1','test7','7a278ee6b95e4cd5c74036097f548beb','678@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','8f14e45fceea167a5a36dedd4bea2543','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723303784687,0),(9,'test8','test8','7a278ee6b95e4cd5c74036097f548beb','789@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','c9f0f895fb98ab9159f51fd0297e236d','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723303784687,0),(10,'test1','test9','7a278ee6b95e4cd5c74036097f548beb','1234@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','d3d9446802a44259755d38e6d163e820','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723304784687,0),(11,'test1','test10','7a278ee6b95e4cd5c74036097f548beb','2345@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','6512bd43d9caa6e02c990b0a82652dca','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723304784687,0),(12,'test1','test11','7a278ee6b95e4cd5c74036097f548beb','3456@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','7f6ffaa6bb0b408017b62254211691b5','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723304784687,0),(13,'test1','test12','7a278ee6b95e4cd5c74036097f548beb','4567@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','c51ce410c124a10e0db5e4b97fc2af39','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723304784687,0),(14,'test1','test13','7a278ee6b95e4cd5c74036097f548beb','5678@qq.com','各个','http://127.0.0.1:8080/api-platform/oss/static/imgs/2a829f9d48599bdae2250cf887c16120.jpeg','aab3238922bcc25a6f606eb525ffdc56','5147ac4ce42e5cb2fd63a5fb8a5b03af','','ADMIN',0,1723304784687,0),(15,'test123123','test01','7a278ee6b95e4cd5c74036097f548beb','01234@qq.com','DDDDD','http://127.0.0.1:8080/api-platform/oss/static/imgs/2b7bbe6309f4ea98648eb50694e083a4.jpeg','8c4cee563f790b89c6ec9514094b7ce5','f114c37e3535f63a3da6c46d708e9d23','','USER',0,1723607630890,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

--
-- Dumping routines for database 'api_platform'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-06 21:56:25
