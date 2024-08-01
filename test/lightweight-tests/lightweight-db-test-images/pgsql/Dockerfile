FROM postgis/postgis:16-3.5.0alpha2-alpine

# copying all SQL files to this folder will automatically put them in the default DB
COPY sql/*.sql /docker-entrypoint-initdb.d/