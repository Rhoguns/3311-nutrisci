
USE nutrisci;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS cnf_food_name;
CREATE TABLE cnf_food_name (
  FoodID          INT PRIMARY KEY,
  FoodCode        INT,
  FoodGroupID     INT,
  FoodSourceID    INT,
  FoodDescription TEXT,
  FoodDescriptionF TEXT,
  DateOfEntry     VARCHAR(20),        
  DateOfPublication VARCHAR(20),
  CountryCode     VARCHAR(10),
  ScientificName  TEXT
);

DROP TABLE IF EXISTS cnf_nutrient_amount;
CREATE TABLE cnf_nutrient_amount (
  FoodID         INT,
  NutrientID     INT,
  NutrientValue  DECIMAL(10,3),
  StandardError  DECIMAL(10,3),
  NumberOfObservations INT,
  NutrientSourceID INT,
  PRIMARY KEY (FoodID, NutrientID)
);

DROP TABLE IF EXISTS cnf_food_group;
CREATE TABLE cnf_food_group (
  FoodGroupID   INT PRIMARY KEY,
  FoodGroupCode VARCHAR(10),
  FoodGroupName TEXT,
  FoodGroupNameF TEXT
);

DROP TABLE IF EXISTS cnf_nutrient_name;
CREATE TABLE cnf_nutrient_name (
  NutrientID    INT PRIMARY KEY,
  NutrientCode  INT,                  
  NutrientSymbol VARCHAR(20),
  NutrientUnit   VARCHAR(20),
  NutrientName   TEXT,
  NutrientNameF  TEXT,
  Tagname        VARCHAR(20),
  NutrientDecimals INT
);

DROP TABLE IF EXISTS applied_swaps;
DROP TABLE IF EXISTS meal_ingredients;
DROP TABLE IF EXISTS meals;
DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS nutrient_data;
DROP TABLE IF EXISTS swap_rules;


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


CREATE TABLE meals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    profile_id INT NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(id)
);

CREATE TABLE meal_ingredients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    meal_id INT NOT NULL,
    food_name VARCHAR(255) NOT NULL,
    quantity_g DOUBLE NOT NULL,
    FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE
);


CREATE TABLE exercises (
  id               INT          NOT NULL AUTO_INCREMENT,
  profile_id       INT          NOT NULL,
  name             VARCHAR(100) NOT NULL,
  duration_minutes DOUBLE       NOT NULL,
  calories_burned  DOUBLE       NOT NULL,
  performed_at     DATETIME     NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (profile_id) REFERENCES profiles(id)
);


CREATE TABLE nutrient_data (
  id               INT          NOT NULL AUTO_INCREMENT,
  food_name        VARCHAR(100) NOT NULL,
  calories_per_gram DOUBLE      NOT NULL,
  protein_per_gram  DOUBLE      DEFAULT 0,
  carbs_per_gram    DOUBLE      DEFAULT 0,
  fat_per_gram      DOUBLE      DEFAULT 0,
  fibre_per_gram    DOUBLE      DEFAULT 0,
  food_group        VARCHAR(4)  DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uniq_food_name (food_name)
);


CREATE TABLE swap_rules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    goal             VARCHAR(50)    NOT NULL,
    original_food    VARCHAR(200)   NOT NULL,
    suggested_food   VARCHAR(200)   NOT NULL,
    improvement_value DOUBLE        NOT NULL,
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_goal (goal),
    INDEX idx_original (original_food)
);


CREATE TABLE applied_swaps (
    id INT AUTO_INCREMENT PRIMARY KEY,
    original_meal_id INT NOT NULL,
    swapped_meal_id INT NOT NULL,
    swap_rule_id INT NOT NULL,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    original_food VARCHAR(255) NOT NULL,
    swapped_food VARCHAR(255) NOT NULL,
    quantity DOUBLE NOT NULL,
    FOREIGN KEY (original_meal_id) REFERENCES meals(id),
    FOREIGN KEY (swapped_meal_id) REFERENCES meals(id),
    FOREIGN KEY (swap_rule_id) REFERENCES swap_rules(id)
);


SET FOREIGN_KEY_CHECKS = 1;
