#!/bin/sh

rsync ./from-test/SMARTNET_2020121205*.csv ./excel

cp sdeploy@10.5.16.111:/smartpay/data/fec-repayment/smarttest/$1 sdeploy@10.5.16.111:/smartpay/data/fec-repayment/thinh/$2
touch -t $3 sdeploy@10.5.16.111:/smartpay/data/fec-repayment/thinh/$2
chown repay-smn-sys:repay-smn-sys  /smartpay/data/fec-repayment/thinh/$2