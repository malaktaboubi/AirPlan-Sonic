-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 14, 2025 at 12:10 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `transportation`
--

-- --------------------------------------------------------

--
-- Table structure for table `transportation`
--

CREATE TABLE `transportation` (
  `id_transport` int(11) NOT NULL,
  `type` varchar(20) NOT NULL,
  `provider_name` varchar(100) NOT NULL,
  `departure_point` varchar(100) NOT NULL,
  `arrival_point` varchar(100) NOT NULL,
  `departure_lat` decimal(10,8) NOT NULL,
  `departure_lng` decimal(11,8) NOT NULL,
  `arrival_lat` decimal(10,8) NOT NULL,
  `arrival_lng` decimal(11,8) NOT NULL,
  `departure_time` time NOT NULL,
  `duration_minutes` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `operating_days` varchar(7) DEFAULT '1111111',
  `photo` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transportation`
--

INSERT INTO `transportation` (`id_transport`, `type`, `provider_name`, `departure_point`, `arrival_point`, `departure_lat`, `departure_lng`, `arrival_lat`, `arrival_lng`, `departure_time`, `duration_minutes`, `price`, `operating_days`, `photo`) VALUES
(1, 'bus', 'City Express', 'Central Station', 'Beach District', 40.71280000, -74.00600000, 40.73560000, -73.99060000, '08:00:00', 45, 4.00, '1111100', ''),
(2, 'bus', 'Green Line', 'Downtown', 'Mountain Resort', 40.71350000, -74.00650000, 40.78210000, -73.96520000, '09:30:00', 75, 8.25, '1111111', ''),
(3, 'train', 'National Rail', 'Main Terminal', 'Lakeside City', 40.71250000, -74.00620000, 40.75800000, -73.98550000, '07:15:00', 30, 12.00, '1111111', ''),
(4, 'train', 'Metro Express', 'City Center', 'Airport', 40.71300000, -74.00630000, 40.64130000, -73.77810000, '06:45:00', 50, 15.75, '1111111', ''),
(5, 'taxi', 'Yellow Cabs', 'Anywhere', 'Anywhere', 0.00000000, 0.00000000, 0.00000000, 0.00000000, '00:00:00', 0, 3.50, '1111111', ''),
(6, 'taxi', 'Eco Rides', 'Anywhere', 'Anywhere', 0.00000000, 0.00000000, 0.00000000, 0.00000000, '00:00:00', 0, 4.20, '1111111', ''),
(7, 'rental_car', 'Hertz', 'Downtown Office', 'Downtown Office', 40.71400000, -74.00640000, 40.71400000, -74.00640000, '08:00:00', 1440, 45.00, '1111111', ''),
(8, 'rental_car', 'Avis', 'Airport', 'Airport', 40.64130000, -73.77810000, 40.64130000, -73.77810000, '08:00:00', 1440, 50.00, '1111111', ''),
(9, 'bus', 'Greyhound', 'New York', 'Boston', 40.71280000, -74.00600000, 42.36010000, -71.05890000, '08:30:00', 150, 25.00, '1111100', ''),
(10, 'train', 'Amtrak', 'New York', 'Boston', 40.71280000, -74.00600000, 42.36010000, -71.05890000, '09:00:00', 105, 40.00, '1111110', ''),
(11, 'bus', 'Megabus', 'New York', 'Boston', 40.71280000, -74.00600000, 42.36010000, -71.05890000, '10:00:00', 160, 20.00, '1111111', ''),
(12, 'train', 'Amtrak Express', 'New York', 'Boston', 40.71280000, -74.00600000, 42.36010000, -71.05890000, '11:30:00', 90, 50.00, '0011111', ''),
(13, 'shuttle', 'Go Bus', 'New York', 'Boston', 40.71280000, -74.00600000, 42.36010000, -71.05890000, '13:00:00', 180, 30.00, '1010100', ''),
(15, 'uber', 'uber', 'N/A', 'N/A', 0.00000000, 0.00000000, 0.00000000, 0.00000000, '00:00:00', 0, 0.00, '1111111', ''),
(16, 'bus', 'FXML', 'tunis', 'marsa', 11.00000000, 11.00000000, 11.00000000, 11.00000000, '10:30:00', 30, 4.00, '1010100', ''),
(17, 'bus', 'Sonikku', 'tunis', 'ariana', 11.00000000, 77.00000000, 44.00000000, 44.00000000, '15:30:00', 30, 2.00, '0010100', ''),
(18, 'ship', 'arkenfortravels', 'new york', 'boston', 11.00000000, 1.00000000, 11.00000000, 1.00000000, '00:00:00', 120, 40.00, '1010100', ''),
(19, 'train', 'azizatravels', 'tunis', 'marsa', 11.00000000, 11.00000000, 11.00000000, 11.00000000, '12:30:00', 40, 1.00, '1010100', ''),
(20, 'bus', 'Megabus', 'tunis', 'ariana', 7.00000000, 44.00000000, 5.00000000, 5.00000000, '12:30:00', 40, 10.00, '1011100', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `transportation`
--
ALTER TABLE `transportation`
  ADD PRIMARY KEY (`id_transport`),
  ADD UNIQUE KEY `type` (`type`,`provider_name`,`departure_point`,`arrival_point`,`departure_time`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `transportation`
--
ALTER TABLE `transportation`
  MODIFY `id_transport` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
