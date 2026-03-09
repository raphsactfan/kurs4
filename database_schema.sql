-- --------------------------------------------------------
-- Хост:                         127.0.0.1
-- Версия сервера:               8.3.0 - MySQL Community Server - GPL
-- Операционная система:         Win64
-- HeidiSQL Версия:              12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Дамп структуры базы данных storekurs
CREATE DATABASE IF NOT EXISTS `storekurs` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `storekurs`;

-- Дамп структуры для таблица storekurs.address
CREATE TABLE IF NOT EXISTS `address` (
  `AddressID` int NOT NULL AUTO_INCREMENT,
  `Country` varchar(50) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `Street` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`AddressID`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.address: ~20 rows (приблизительно)
INSERT INTO `address` (`AddressID`, `Country`, `City`, `Street`) VALUES
	(1, 'Беларусь', 'Минск', 'Победителей 9'),
	(2, 'Беларусь', 'Минск', 'Весёлая 15'),
	(3, 'Беларусь', 'Гродно', 'Серая 3'),
	(7, 'Беларусь', 'Витебск', 'Зелёная 66'),
	(8, 'Беларусь', 'Гродно', 'Кольцевая 17'),
	(9, 'Беларусь', 'Брест', 'Советская 8'),
	(10, 'Литва', 'Вильнюс', 'Гедимино 10'),
	(11, 'Латвия', 'Лиепая', 'Курземе 12'),
	(12, 'Польша', 'Краков', 'Главный рынок 4'),
	(50, 'Беларусь', 'Минск', 'Красная 23'),
	(51, 'Беларусь', 'Минск', 'Красная 23'),
	(52, 'Беларусь', 'Минск', 'Красная 23'),
	(53, 'Беларусь', 'Минск', 'Красная 23'),
	(54, 'Беларусь', 'Минск', 'Красная 23'),
	(55, 'Беларусь', 'Витебск', 'Ленина 85'),
	(56, 'Беларусь', 'Пинск', 'Гагарина 14'),
	(57, 'Беларусь', 'Витебск', 'Ленина 85'),
	(58, 'Беларусь', 'Витебск', 'Гоголя 3'),
	(60, 'Беларусь', 'Орша', 'Пушкина 2'),
	(62, 'Россия', 'Екатеринбург', 'Гоголя 2'),
	(63, 'Беларусь', 'Мозырь', 'Гоголя 2'),
	(64, 'Минск', 'Дзержинского', '24'),
	(65, 'Беларусь', 'Минск', 'Дзержинского 24'),
	(66, 'Беларусь', 'Пинск', 'Гоголя 22');

-- Дамп структуры для таблица storekurs.category
CREATE TABLE IF NOT EXISTS `category` (
  `CategoryID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`CategoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.category: ~11 rows (приблизительно)
INSERT INTO `category` (`CategoryID`, `Name`) VALUES
	(1, 'Смартфоны'),
	(2, 'Телевизоры'),
	(3, 'Ноутбуки'),
	(4, 'Для кухни'),
	(5, 'Гарнитура'),
	(6, 'Комплектующие'),
	(7, 'Игровые консоли'),
	(8, 'Для офиса'),
	(9, 'Для дома'),
	(10, 'Прочие гаджеты');

-- Дамп структуры для таблица storekurs.orderdetails
CREATE TABLE IF NOT EXISTS `orderdetails` (
  `OrderID` int NOT NULL,
  `DateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `ordertable` (`OrderID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.orderdetails: ~10 rows (приблизительно)
INSERT INTO `orderdetails` (`OrderID`, `DateTime`) VALUES
	(22, '2025-04-16 22:15:00'),
	(23, '2025-04-16 22:15:15'),
	(24, '2025-04-16 22:15:27'),
	(25, '2025-04-16 22:15:40'),
	(27, '2025-04-16 22:40:26'),
	(28, '2025-04-22 22:47:10'),
	(29, '2025-04-16 23:47:46'),
	(30, '2025-04-18 01:08:01'),
	(33, '2025-04-22 20:02:09'),
	(35, '2025-04-25 22:25:29'),
	(36, '2025-05-02 22:21:02');

-- Дамп структуры для таблица storekurs.orderproduct
CREATE TABLE IF NOT EXISTS `orderproduct` (
  `OrderID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Quantity` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`OrderID`,`ProductID`),
  KEY `ProductID` (`ProductID`),
  CONSTRAINT `orderproduct_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `ordertable` (`OrderID`) ON DELETE CASCADE,
  CONSTRAINT `orderproduct_ibfk_2` FOREIGN KEY (`ProductID`) REFERENCES `product` (`ProductID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.orderproduct: ~18 rows (приблизительно)
INSERT INTO `orderproduct` (`OrderID`, `ProductID`, `Quantity`) VALUES
	(22, 4, 1),
	(23, 4, 2),
	(24, 4, 1),
	(25, 4, 1),
	(27, 4, 1),
	(28, 54, 2),
	(29, 33, 1),
	(29, 63, 1),
	(30, 42, 1),
	(33, 26, 1),
	(33, 40, 1),
	(35, 18, 1),
	(35, 38, 1),
	(35, 40, 2),
	(35, 54, 1),
	(36, 19, 1),
	(36, 38, 1),
	(36, 40, 2),
	(36, 48, 1);

-- Дамп структуры для таблица storekurs.ordertable
CREATE TABLE IF NOT EXISTS `ordertable` (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `Quantity` int DEFAULT NULL,
  `TotalAmount` decimal(10,2) DEFAULT NULL,
  `AddressID` int DEFAULT NULL,
  `UserID` int DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  KEY `AddressID` (`AddressID`),
  KEY `UserID` (`UserID`),
  CONSTRAINT `ordertable_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE CASCADE,
  CONSTRAINT `ordertable_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.ordertable: ~10 rows (приблизительно)
INSERT INTO `ordertable` (`OrderID`, `Quantity`, `TotalAmount`, `AddressID`, `UserID`) VALUES
	(22, 1, 1799.00, 50, 5),
	(23, 2, 3598.00, 51, 28),
	(24, 1, 1799.00, 52, 3),
	(25, 1, 1799.00, 53, 9),
	(27, 1, 1799.00, 55, 28),
	(28, 2, 120.00, 56, 28),
	(29, 2, 94.00, 57, 28),
	(30, 1, 1199.00, 58, 3),
	(33, 2, 388.00, 63, 35),
	(35, 5, 4206.00, 65, 38),
	(36, 5, 3095.00, 66, 39);

-- Дамп структуры для процедура storekurs.patients_by_gender
DELIMITER //
CREATE PROCEDURE `patients_by_gender`(IN g ENUM('М','Ж'))
BEGIN
    SELECT familia, imya, vozrast FROM patients WHERE pol = g;
END//
DELIMITER ;

-- Дамп структуры для таблица storekurs.product
CREATE TABLE IF NOT EXISTS `product` (
  `ProductID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Quantity` int DEFAULT NULL,
  `Price` decimal(10,2) DEFAULT NULL,
  `SupplierID` int DEFAULT NULL,
  `CategoryID` int DEFAULT NULL,
  PRIMARY KEY (`ProductID`),
  KEY `SupplierID` (`SupplierID`),
  KEY `product_ibfk_2` (`CategoryID`),
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`SupplierID`) REFERENCES `supplier` (`SupplierID`) ON DELETE CASCADE,
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`CategoryID`) REFERENCES `category` (`CategoryID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.product: ~63 rows (приблизительно)
INSERT INTO `product` (`ProductID`, `Name`, `Quantity`, `Price`, `SupplierID`, `CategoryID`) VALUES
	(1, 'Смартфон Xiaomi Redmi 13 Pro', 18, 799.00, 3, 1),
	(2, 'Смартфон Apple iPhone 15', 7, 3599.00, 11, 1),
	(3, 'Смартфон Samsung Galaxy S24+', 10, 3299.00, 11, 1),
	(4, 'Смартфон Huawei Pura 70', 993, 1799.00, 1, 1),
	(5, 'Смартфон Apple iPhone 13', 6, 2249.00, 3, 1),
	(6, 'Смартфон POCO M6 Pro', 6, 849.00, 9, 1),
	(7, 'Смартфон HONOR X8b', 3, 699.00, 12, 1),
	(8, 'Телевизор LG 50UR78006LK', 8, 1699.00, 8, 2),
	(9, 'Телевизор TCL 32FHD7900', 6, 649.00, 1, 2),
	(10, 'Телевизор TCL 50P79B', 7, 1199.00, 10, 2),
	(11, 'Телевизор Haier 32 Smart TV', 12, 669.00, 3, 2),
	(12, 'Телевизор Xiaomi TV A2', 3, 649.00, 2, 2),
	(13, 'Ноутбук Honor MagicBook X16', 5, 2399.00, 1, 3),
	(14, 'Ноутбук Digma EVE 0801', 5, 800.00, 3, 3),
	(15, 'Ноутбук Apple MacBook Air 13"', 6, 2999.00, 3, 3),
	(16, 'Моноблок ASUS Ai0 A5', 2, 3999.00, 2, 3),
	(17, 'Игровой ноутбук ASUS TUF Gaming F17', 5, 3999.00, 2, 3),
	(18, 'Игровой ноутбук MSI Thin 15', 4, 3499.00, 10, 3),
	(19, 'Ноутбук Huawei MateBook D 16', 3, 2399.00, 12, 3),
	(20, 'Ноутбук Chuwi HeroBook Pro', 1, 841.00, 7, 3),
	(21, 'Холодильник Indesit ITS 4200', 12, 1269.00, 10, 4),
	(22, 'Микроволновая печь Gorenje M017E1S', 3, 199.00, 3, 4),
	(23, 'Кофемашина POLARIS PACM', 7, 1399.00, 10, 4),
	(24, 'Мультиварка REDMOND', 2, 419.00, 1, 4),
	(25, 'Морозильник Hyundai CU1007', 3, 639.00, 3, 4),
	(26, 'Блендер HOLT', 2, 89.00, 11, 4),
	(27, 'Электрочайник Hott', 6, 75.00, 1, 4),
	(28, 'Наушники Marshall Major IV', 6, 399.00, 2, 5),
	(29, 'Наушники Samsung Galaxy Buds 3', 13, 419.00, 8, 5),
	(30, 'Наушники JBL Tune 310C', 15, 90.00, 9, 5),
	(31, 'Микрофон Oklick', 2, 11.00, 7, 5),
	(32, 'Наушники Apple Airpods 2', 7, 500.00, 12, 5),
	(33, 'Беспроводные наушники Celebrat A35', 10, 42.00, 3, 5),
	(34, 'Наушники Evolution BH501', 4, 34.00, 9, 5),
	(35, 'Оперативная память Kingston FURY Beast 2x16GB', 22, 356.00, 7, 6),
	(36, 'Видеокарта Gigabyte GeForce RTX 4060', 6, 1455.00, 12, 6),
	(37, 'Материнская плата Gigabyte B760M', 8, 431.00, 9, 6),
	(38, 'Блок питания Ginzzu SA400', 4, 49.00, 7, 6),
	(39, 'Кулер ID-Cooling', 7, 44.00, 12, 6),
	(40, 'SSD Kingston NV2 1TB', 1, 299.00, 11, 6),
	(41, 'Sony PlayStation 5 Slim', 10, 2099.00, 2, 7),
	(42, 'Microsoft Xbox Series S', 14, 1199.00, 2, 7),
	(43, 'Игровая консоль Wiisun X80', 10, 200.00, 7, 7),
	(44, 'Беспроводной геймпад DualSense', 5, 319.00, 2, 7),
	(45, 'Беспроводной геймпад MICROSOFT XBOX XS', 5, 249.00, 2, 7),
	(46, 'Принтер Epson L1210', 4, 739.00, 7, 8),
	(47, 'Монитор UltraGear LG', 14, 895.00, 2, 8),
	(48, 'Мышь LOGITECH M170', 5, 49.00, 1, 8),
	(49, 'Клавиатура Redragon Lakshmi', 2, 95.00, 10, 8),
	(50, 'Клавиатура A4Tech', 7, 45.00, 8, 8),
	(51, 'Шредер BRAUBERG', 6, 200.00, 7, 8),
	(52, 'Пылесос Deerma DX118C', 10, 79.00, 1, 9),
	(53, 'Стиральная машина LG F2J3HS4L', 3, 1599.00, 3, 9),
	(54, 'Увлажнитель воздуха Ballu', 2, 60.00, 1, 9),
	(55, 'Кондиционер TCL GentleCool', 9, 1599.00, 11, 9),
	(56, 'Вентилятор Electrolux', 10, 157.00, 12, 9),
	(57, 'Робот-пылесос Ecovacs Deebot', 0, 799.00, 10, 9),
	(58, 'Утюг Polaris PIR 2420AK', 8, 149.00, 12, 9),
	(59, 'Смарт-часы Xiaomi Redmi Watch З', 16, 299.00, 10, 10),
	(60, 'Беговая дорожка Calviano Sunny', 2, 950.00, 1, 10),
	(61, 'Массажное кресло iRest А', 3, 6440.00, 10, 10),
	(62, 'GPS навигатор Navitel', 4, 550.00, 1, 10),
	(63, 'Напольные весы Kitfort', 11, 52.00, 3, 10),
	(66, 'Тостер Mix 3000', 12, 99.00, 3, 4),
	(72, 'Видеокарта Gigabyte GeForce RTX', 1, 2222.00, 2, 6);

-- Дамп структуры для таблица storekurs.supplier
CREATE TABLE IF NOT EXISTS `supplier` (
  `SupplierID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Representative` varchar(50) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `AddressID` int DEFAULT NULL,
  PRIMARY KEY (`SupplierID`),
  KEY `AddressID` (`AddressID`),
  CONSTRAINT `supplier_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.supplier: ~9 rows (приблизительно)
INSERT INTO `supplier` (`SupplierID`, `Name`, `Representative`, `Phone`, `AddressID`) VALUES
	(1, 'Электромир', 'Королёв Алексей', '+375331111111', 1),
	(2, 'Киберлинк', 'Соколова Ольга', '+375292222222', 2),
	(3, 'ТехникПро', 'Иванов Максим', '+375253333333', 3),
	(7, 'Оптовик+', 'Мизинцева Светлана', '+375334444444', 7),
	(8, 'ЭнергоМаг', 'Минаев Вадим', '+375225555555', 8),
	(9, 'ВольтМаркет', 'Сидорова Валерия', '+375336666666', 9),
	(10, 'BalticSupply', 'Агне Шуминас', '+37090055610', 10),
	(11, 'LLL', 'Элина Калниня', '+37122140813', 11),
	(12, 'TechnoWorld', 'Пётр Вишневский', '+48711523142', 12);

-- Дамп структуры для таблица storekurs.user
CREATE TABLE IF NOT EXISTS `user` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Login` varchar(50) NOT NULL,
  `Password` varchar(100) NOT NULL,
  `Role` varchar(10) NOT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы storekurs.user: ~27 rows (приблизительно)
INSERT INTO `user` (`UserID`, `Login`, `Password`, `Role`) VALUES
	(1, 'usertest', '$2b$12$zFaywRbF0O1I35hAYpbSRebaHrYDroU1aZ5qg81ZEumvAG8WfFRWq', 'admin'),
	(2, 'USER1', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'user'),
	(3, 'loginnn', '428821350e9691491f616b754cd8315fb86d797ab35d843479e732ef90665324', 'user'),
	(5, 'asdfg', 'f969fdbe811d8a66010d6f8973246763147a2a0914afc8087839e29b563a5af0', 'user'),
	(6, '12345', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'user'),
	(8, '11111', '65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5', 'user'),
	(9, 'newuser', '9c9064c59f1ffa2e174ee754d2979be80dd30db552ec03e7e327e9b1a4bd594e', 'user'),
	(10, 'Admin', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'admin'),
	(11, 'qwerty', '65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5', 'user'),
	(13, '00000', 'd17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2', 'admin'),
	(17, '33333', '216e683ff0d2d25165b8bb7ba608c9a628ef299924ca49ab981ec7d2fecd6dad', 'admin'),
	(19, 'usertest2', 'd17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2', 'user'),
	(20, 'client', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'user'),
	(21, 'NewAdmin', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'admin'),
	(23, 'dziyana_zusko', '$2a$10$3znFGkyZU11iy444PhZHL.HRHAJbMj4Z8Z17tI8GPppLzneEtY9kC', 'user'),
	(24, '123456', '$2a$10$.pJId.rd7uWNv8osLPQBreJ6fv8DBKzPFGXCeJxgxl/EBzPnQGmhe', 'user'),
	(25, 'qwertyuiop', '$2a$10$wyBLTxADOLlOeENJYeOyNeQotc9NHYZWtii.4nkt18NAWJ/yWyCZe', 'user'),
	(26, 'usertest342', '$2a$10$aSdmENoXJR7XK6GXmv3k2OWOuUxQbZcP8bVh6Ml/lV9lke3mYCVxa', 'user'),
	(27, 'usertest352523', '$2a$10$dOAvUCYWxVkvjxOCkqzVZuS2Ws3RhIGJgNoxst9hFEpdyUM01DgXu', 'user'),
	(28, 'usertest10', '$2a$10$vZTGJLfBOE.sHvCdpnCNyuvWSsU8XDXHRCHPtqLbZxVGsisR4vzYm', 'user'),
	(30, 'ussser', '$2a$10$W.3FTyEahqct9fF9JFMPg..je6lFkDEOqyUQaWOrn4WrMlj6lVJrS', 'user'),
	(32, 'admin00', '$2a$10$WX0hCpqJ1E3QVOpRkJPPbeaYjkp0cElhWBgdjQYM/FhfRvgXMZlAK', 'admin'),
	(33, 'user00', '$2a$10$h3sNSrWnlAE/A5/vOGCMFOD2YVELbiGPZFodygUBcOeJ6DMF16vMK', 'user'),
	(34, 'profile1', '$2a$10$2yLZc5bAy2jxTh9jgQOb2ObaB0N30vWZBzQ0CcVnsJKkTegWfpNo2', 'user'),
	(35, 'qwerty11', '$2a$10$jaRm.kKnVI.0lBQtKKw5Ou/It.W5tkk8c3iKj37/srxJ3HZ4jBh7K', 'user'),
	(36, 'newlogin', '$2a$10$wVyYk05E36BcImEPiy3vTO617ohYtqRCldtCYl1KuOA9sEzn0i2Sq', 'admin'),
	(38, 'NewUser3', '$2a$10$UQN2T6u0QZsGCQCHtcw9OeQrlEOkp4gJkSTotOOY7OEkKSQXh4dwm', 'user'),
	(39, 'user6', '$2a$10$JqSRxvbqYxF4rqHaRumuhutZav/qkVnpNmPJ/VD9M7aCVMRwGuNoq', 'user');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
