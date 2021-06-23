#!/bin/bash
ssh 10.3.15.121 "sudo /smartpay/scripts/clean_cache.sh"
ssh 10.4.16.182 "sudo /smartpay/scripts/restart_website.sh"