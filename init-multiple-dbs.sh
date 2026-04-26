#!/bin/bash
set -e
set -u

function create_database() {
    local database=$1
    echo "  Creating database '$database'"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
        CREATE DATABASE $database;
        GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
}

echo "Creating multiple databases..."
for db in auth_db user_db goal_db finance_db analytics_db notification_db; do
    create_database $db
done
echo "All databases created successfully."
