-- =============================================================================
-- Mzansi Chat Zone - Database Schema & Seed Data
-- Database Engine: MySQL / MariaDB (utf8mb4)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `mzansi_chat_zone`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `mzansi_chat_zone`;

-- -----------------------------------------------------------------------------
-- 1. Users Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `display_name` VARCHAR(100) NOT NULL,
  `password_hash` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. Conversations Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `conversations` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. Conversation Participants Table (Mapping 1-to-1 or group chat users)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `conversation_participants` (
  `conversation_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  `joined_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`conversation_id`, `user_id`),
  KEY `fk_cp_user` (`user_id`),
  CONSTRAINT `fk_cp_conversation` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cp_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. Messages Table (Text & Camera Image Uploads)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `messages` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `conversation_id` INT(11) NOT NULL,
  `sender_id` INT(11) NOT NULL,
  `message_type` ENUM('TEXT', 'IMAGE') NOT NULL DEFAULT 'TEXT',
  `content` TEXT DEFAULT NULL,
  `image_path` VARCHAR(255) DEFAULT NULL,
  `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id`),
  KEY `fk_msg_sender` (`sender_id`),
  KEY `idx_messages_conversation_sent` (`conversation_id`, `sent_at`),
  CONSTRAINT `fk_msg_conversation` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_msg_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. Default Seed Users
-- Passwords hashed using BCrypt (Default password for demo accounts is 'Password123!')
-- -----------------------------------------------------------------------------
INSERT INTO `users` (`id`, `username`, `display_name`, `password_hash`)
VALUES
  (1, 'thanyani', 'Thanyani Sithole', '$2a$12$0QPTFMu7Kn3E0lCZ.18Fde5cjooDRu3otKkTNJF8nhmK/HQ1RFhjG'),
  (2, 'fulu.makgoka', 'Fulu Makgoka', '$2b$12$2Xzu9WVufzLQeHZTzt4WxO0xVf/3VlrPsc9mju2UcoruwrsMPevIm'),
  (3, 'maanda.n', 'Maanda Netshisumbewa', '$2b$12$2Xzu9WVufzLQeHZTzt4WxO0xVf/3VlrPsc9mju2UcoruwrsMPevIm'),
  (4, 'lenah.eduvos', 'Lenah Eduvos', '$2b$12$2Xzu9WVufzLQeHZTzt4WxO0xVf/3VlrPsc9mju2UcoruwrsMPevIm'),
  (5, 'sannah.m', 'Sannah Mokoena', '$2b$12$2Xzu9WVufzLQeHZTzt4WxO0xVf/3VlrPsc9mju2UcoruwrsMPevIm'),
  (6, 'patrick.k', 'Patrick Khumalo', '$2b$12$2Xzu9WVufzLQeHZTzt4WxO0xVf/3VlrPsc9mju2UcoruwrsMPevIm')
ON DUPLICATE KEY UPDATE `display_name` = VALUES(`display_name`);
