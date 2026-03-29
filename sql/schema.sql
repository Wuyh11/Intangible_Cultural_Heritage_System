SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_operation_log;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS ich_project_media;
DROP TABLE IF EXISTS ich_project_inheritor;
DROP TABLE IF EXISTS ich_inheritor;
DROP TABLE IF EXISTS ich_project;
DROP TABLE IF EXISTS ich_region;
DROP TABLE IF EXISTS ich_category;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(120) NOT NULL,
  nickname VARCHAR(60) NOT NULL,
  email VARCHAR(100) DEFAULT NULL,
  phone VARCHAR(20) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(40) NOT NULL,
  role_name VARCHAR(60) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  KEY idx_sys_user_role_user_id (user_id),
  KEY idx_sys_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE sys_operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL,
  username VARCHAR(50) DEFAULT NULL,
  module VARCHAR(60) NOT NULL,
  action VARCHAR(100) NOT NULL,
  http_method VARCHAR(20) DEFAULT NULL,
  request_uri VARCHAR(255) DEFAULT NULL,
  request_ip VARCHAR(60) DEFAULT NULL,
  request_param TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_sys_operation_log_user_id (user_id),
  KEY idx_sys_operation_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE ich_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(60) NOT NULL,
  code VARCHAR(40) NOT NULL,
  sort_order INT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1,
  description VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_ich_category_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗分类表';

CREATE TABLE ich_region (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(60) NOT NULL,
  code VARCHAR(40) NOT NULL,
  sort_order INT NOT NULL DEFAULT 1,
  longitude VARCHAR(30) DEFAULT NULL,
  latitude VARCHAR(30) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_ich_region_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地区表';

CREATE TABLE ich_project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  category_id BIGINT NOT NULL,
  region_id BIGINT NOT NULL,
  level VARCHAR(30) NOT NULL,
  batch VARCHAR(60) DEFAULT NULL,
  protection_unit VARCHAR(160) DEFAULT NULL,
  cover_image VARCHAR(255) DEFAULT NULL,
  summary VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  longitude VARCHAR(30) DEFAULT NULL,
  latitude VARCHAR(30) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  view_count INT NOT NULL DEFAULT 0,
  featured TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_ich_project_category_id (category_id),
  KEY idx_ich_project_region_id (region_id),
  KEY idx_ich_project_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗项目表';

CREATE TABLE ich_inheritor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(60) NOT NULL,
  gender VARCHAR(10) DEFAULT NULL,
  birth_year INT DEFAULT NULL,
  region_id BIGINT NOT NULL,
  title VARCHAR(160) DEFAULT NULL,
  avatar VARCHAR(255) DEFAULT NULL,
  introduction TEXT NOT NULL,
  representative_works VARCHAR(255) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_ich_inheritor_region_id (region_id),
  KEY idx_ich_inheritor_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传承人表';

CREATE TABLE ich_project_inheritor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  inheritor_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  KEY idx_ich_project_inheritor_project_id (project_id),
  KEY idx_ich_project_inheritor_inheritor_id (inheritor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目传承人关联表';

CREATE TABLE ich_project_media (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  media_type VARCHAR(20) NOT NULL,
  media_url VARCHAR(255) NOT NULL,
  media_title VARCHAR(120) DEFAULT NULL,
  sort_order INT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_ich_project_media_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目媒体表';

SET FOREIGN_KEY_CHECKS = 1;
