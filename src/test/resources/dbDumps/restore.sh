#! /bin/bash

# Check if first argument is an empty string
if [ -z "$1" ]
  then
    echo "Please specify dump name to restore, ex: '$0 some_db-empty-state.sql'."
    exit 1
fi

ACCOUNT=root

echo "'$ACCOUNT' account password required."
mysql -u $ACCOUNT -p < $1
