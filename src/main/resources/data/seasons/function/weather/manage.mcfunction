# Manage seasonal weather patterns

# Spring: More rain
execute if score #global seasons_current matches 1 if predicate seasons:random/5_percent run weather rain 800

# Summer: Clear skies with occasional storms  
execute if score #global seasons_current matches 2 if predicate seasons:random/8_percent run weather clear 1200
execute if score #global seasons_current matches 2 if predicate seasons:random/2_percent run weather thunder 400

# Fall: Moderate weather
execute if score #global seasons_current matches 3 if predicate seasons:random/6_percent run weather rain 600
execute if score #global seasons_current matches 3 if predicate seasons:random/6_percent run weather clear 800

# Winter: Snow and storms
execute if score #global seasons_current matches 4 if predicate seasons:random/12_percent run weather rain 600
execute if score #global seasons_current matches 4 if predicate seasons:random/4_percent run weather thunder 300