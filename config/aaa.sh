#!/bin/bash
echo "$FINAL_LINE"
VAR="export HOME=/smartpay/apps/prod.account-service"
LINE=""
if [[ "$VAR" == *"export"* ]]; then
  LINE="${VAR/export/ENV}"
fi

echo "$LINE"
