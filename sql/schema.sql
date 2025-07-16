-- schema.sql

DROP DATABASE IF EXISTS nutrisci;
CREATE DATABASE IF NOT EXISTS nutrisci
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE nutrisci;


-- Profiles (so profile_id FK works)
DROP TABLE IF EXISTS profiles;
CREATE TABLE profiles (
  id            INT            NOT NULL AUTO_INCREMENT,
  name          VARCHAR(100)   NOT NULL,
  sex           VARCHAR(10)    NOT NULL,
  date_of_birth DATE           NOT NULL,
  height_cm     DOUBLE         NOT NULL,
  weight_kg     DOUBLE         NOT NULL,
  unit          VARCHAR(10)    NOT NULL,
  email         VARCHAR(100)   DEFAULT NULL,
  PRIMARY KEY (id)
);

-- Meals & Ingredients
DROP TABLE IF EXISTS meal_ingredients;
DROP TABLE IF EXISTS meals;

CREATE TABLE meals (
  id         INT            NOT NULL AUTO_INCREMENT,
  profile_id INT            NOT NULL,
  meal_type  VARCHAR(50)    NOT NULL,
  logged_at  DATETIME       NOT NULL,
  PRIMARY KEY (id),
  KEY idx_meals_profile (profile_id),
  CONSTRAINT fk_meals_profiles FOREIGN KEY (profile_id)
    REFERENCES profiles(id)
);

CREATE TABLE meal_ingredients (
  meal_id         INT          NOT NULL,
  food_name VARCHAR(100) NOT NULL,
  quantity_g      DOUBLE       NOT NULL,
  PRIMARY KEY (meal_id, food_name),
  KEY idx_ingredients_meal (meal_id),
  CONSTRAINT fk_ingredients_meals FOREIGN KEY (meal_id)
    REFERENCES meals(id)
);

-- Exercises & Logs
DROP TABLE IF EXISTS exercise_log;
DROP TABLE IF EXISTS exercises;

CREATE TABLE exercises (
  id               INT          NOT NULL AUTO_INCREMENT,
  profile_id       INT          NOT NULL,
  name             VARCHAR(100) NOT NULL,
  duration_minutes DOUBLE       NOT NULL,
  calories_burned  DOUBLE       NOT NULL,
  performed_at     DATETIME     NOT NULL,
  PRIMARY KEY (id)
);

-- Nutrient Data
DROP TABLE IF EXISTS nutrient_data;

CREATE TABLE nutrient_data (
  id               INT          NOT NULL AUTO_INCREMENT,
  food_name  VARCHAR(100) NOT NULL,
  calories_per_gram DOUBLE      NOT NULL,
  -- add more nutrient columns as needed
  PRIMARY KEY (id),
  UNIQUE KEY uniq_ingredient (food_name)
);
