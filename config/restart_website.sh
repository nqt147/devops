#!/bin/bash

SMARTPAY_WEBSITE="smartpay-audit-cms"
APPUSER="sdeploy"
su - ${APPUSER} -c "pm2 restart $SMARTPAY_WEBSITE"