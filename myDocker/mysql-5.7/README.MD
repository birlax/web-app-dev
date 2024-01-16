# Local MySQL 5.7

## How to connect

- Host     : localhost (127.0.0.1)
- Port     : 3306
- User     : root
- Password : <BLANK_NO_PASSWORD>
- Database : mysql

## Directory used to Store db-data

````shell
mkdir -p $HOME/DATABASE_DIR/MYSQL_DATA_DIR/mysql-57
````

- By default `$HOME/DATABASE_DIR/MYSQL_DATA_DIR/mysql-57` is used
- Change compose yml to change it

## How to Re-build or Compose

````shell

:; docker system prune -a

:; docker-compose up &
 
````
