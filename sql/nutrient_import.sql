

DROP TABLE IF EXISTS cnf_food_name;
DROP TABLE IF EXISTS cnf_nutr_amount;
DROP TABLE IF EXISTS cnf_nutr_name;

CREATE TABLE cnf_food_name (
  ndb_no       INT         PRIMARY KEY,
  fdgrp_cd     VARCHAR(4),
  long_desc    VARCHAR(200),
  short_desc   VARCHAR(100),
  common_name  VARCHAR(100),
  manufac_name VARCHAR(100),
  survey       CHAR(1),
  refuse       INT,
  sci_name     VARCHAR(200),
  n_factor     DOUBLE,
  pro_factor   DOUBLE,
  fat_factor   DOUBLE,
  cho_factor   DOUBLE
);

CREATE TABLE cnf_nutr_amount (
  ndb_no        INT,
  nutr_no       INT,
  nutr_val      DOUBLE,
  num_data_pts  INT,
  std_error     DOUBLE,
  src_cd        CHAR(1),
  deriv_cd      CHAR(1),
  ref_ndb_no    INT,
  add_nutr_mark CHAR(1),
  cc            CHAR(1),
  PRIMARY KEY (ndb_no, nutr_no)
);

CREATE TABLE cnf_nutr_name (
  nutr_no   INT         PRIMARY KEY,
  units     VARCHAR(10),
  tagname   VARCHAR(20),
  nutr_desc VARCHAR(200),
  num_dec   INT
);


LOAD DATA LOCAL INFILE 'sql/FOOD NAME.csv'
INTO TABLE cnf_food_name
  CHARACTER SET latin1
  FIELDS TERMINATED BY ',' 
  ENCLOSED BY '"'  
  LINES TERMINATED BY '\r\n'
  IGNORE 1 ROWS
  (ndb_no, fdgrp_cd, long_desc, short_desc, common_name,
   manufac_name, survey, refuse, sci_name,
   n_factor, pro_factor, fat_factor, cho_factor);

LOAD DATA LOCAL INFILE 'sql/NUTRIENT AMOUNT.csv'
INTO TABLE cnf_nutr_amount
  CHARACTER SET latin1
  FIELDS TERMINATED BY ',' 
  ENCLOSED BY '"'  
  LINES TERMINATED BY '\r\n'
  IGNORE 1 ROWS
  (ndb_no, nutr_no, nutr_val, num_data_pts, std_error,
   src_cd, deriv_cd, ref_ndb_no, add_nutr_mark, cc);

LOAD DATA LOCAL INFILE 'sql/NUTRIENT NAME.csv'
INTO TABLE cnf_nutr_name
  CHARACTER SET latin1
  FIELDS TERMINATED BY ',' 
  ENCLOSED BY '"'  
  LINES TERMINATED BY '\r\n'
  IGNORE 1 ROWS
  (nutr_no, units, tagname, nutr_desc, num_dec);


DROP TABLE IF EXISTS nutrient_data;
CREATE TABLE nutrient_data (
  food_name           VARCHAR(200) PRIMARY KEY,
  calories_per_gram   DOUBLE,
  protein_per_gram    DOUBLE,
  carbs_per_gram      DOUBLE,
  fat_per_gram        DOUBLE,
  fibre_per_gram      DOUBLE
);


INSERT IGNORE INTO nutrient_data (
  food_name,
  calories_per_gram,
  protein_per_gram,
  carbs_per_gram,
  fat_per_gram,
  fibre_per_gram
)
SELECT

  COALESCE(NULLIF(fn.common_name, ''), fn.long_desc)      AS food_name,
  COALESCE(na208.nutr_val,0)/100                          AS calories_per_gram,  -- 208 = Energy (kcal)
  COALESCE(na203.nutr_val,0)/100                          AS protein_per_gram,   -- 203 = Protein (g)
  COALESCE(na205.nutr_val,0)/100                          AS carbs_per_gram,     -- 205 = Carbs by difference (g)
  COALESCE(na204.nutr_val,0)/100                          AS fat_per_gram,       -- 204 = Total lipid (fat) (g)
  COALESCE(na291.nutr_val,0)/100                          AS fibre_per_gram      -- 291 = Dietary fiber (g)
FROM cnf_food_name fn
LEFT JOIN cnf_nutr_amount na208 
  ON fn.ndb_no = na208.ndb_no AND na208.nutr_no = 208
LEFT JOIN cnf_nutr_amount na203 
  ON fn.ndb_no = na203.ndb_no AND na203.nutr_no = 203
LEFT JOIN cnf_nutr_amount na205 
  ON fn.ndb_no = na205.ndb_no AND na205.nutr_no = 205
LEFT JOIN cnf_nutr_amount na204 
  ON fn.ndb_no = na204.ndb_no AND na204.nutr_no = 204
LEFT JOIN cnf_nutr_amount na291 
  ON fn.ndb_no = na291.ndb_no AND na291.nutr_no = 291
;
