# Local Postgres 11.8

## How to connect

- Host     : `localhost (127.0.0.1) `
- Port     : `5432`
- User     : `postgres`
- Password : `postgres`
- Database : `postgres`

## Directory used to Store db-data

````shell
mkdir -p $HOME/DATABASE_DIR/PG_DATA_DIR/postgres-11.8
````

- By default `$HOME/DATABASE_DIR/PG_DATA_DIR/postgres-11.8` is used
- Change compose yml to change it

## How to Re-build or Compose

```shell script
:; docker system prune -a

:; docker-compose up &
 
```

