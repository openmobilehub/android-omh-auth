#!/bin/bash
echo "GOOGLE_CLIENT_ID=$1" >> ./local.properties
echo "FACEBOOK_APP_ID=$2" >> ./local.properties
echo "FACEBOOK_CLIENT_TOKEN=$3" >> ./local.properties
echo "MICROSOFT_CLIENT_ID=$4" >> ./local.properties
echo "MICROSOFT_SIGNATURE_HASH=$5" >> ./local.properties
echo "DROPBOX_APP_KEY=$6" >> ./local.properties