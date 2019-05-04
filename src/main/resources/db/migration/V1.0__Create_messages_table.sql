CREATE TABLE `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `body` varchar(255) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `sent_date` datetime NOT NULL,
  `subject` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);
