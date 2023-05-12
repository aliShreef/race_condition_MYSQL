# race_condition_MYSQL
race condition example with MySQL DB

# DB configuration
1- create Database with name "TRx" as in property file or create your Database with name you choose and update the preoprty file. \
2- run DB_script to create table and insert dummy data.

# Test cases 
run each case method to understand the behaviour of race condition and how it will affect the data integrity 


## Note
Don't forget to reset the data to initial state after you run any test case. since I leave to you to observe the result after each case.
useful DB command for more information. \
SHOW ENGINE INNODB STATUS; \
SET GLOBAL innodb_print_all_deadlocks = 1;

have fun :)
