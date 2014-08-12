#! /bin/bash

# Check if first argument is an empty string
if [ -z "$1" ]
  then
    echo "Please specify dump type, ex: '$0 empty-state'."
    exit 1
fi

ACCOUNT=root

echo "'$ACCOUNT' account password required."
mysqldump -u $ACCOUNT -p --databases ibeacon_db --single-transaction --master-data > ../dbDumps/ibeacon_db-$1.sql
