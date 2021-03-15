#!/bin/bash
SMARTNET_20210301050102.csv
DATE=$(date '+%Y%m%d')
DATE_TOUCH=$(date '+%y%m%d')
DATE_CHECK="$DATE""00"
FILE_NAME="SMARTNET_""$DATE""05012"

PREFIX_TABLE=$(echo $(date -d "-$(date +%d) days" +%Y%m))
POSTFIX_TIME=$(echo $(date -d "-$(date +%d) days" +%Y-%m-%d))

START_TIME=$POSTFIX_TIME"T00:00:00.000+07:00"
END_TIME=$POSTFIX_TIME"T23:59:59.999+07:00"
TABLE_NAME="fec_repayment"$PREFIX_TABLE

QUERY="SELECT count(*) as counter FROM "$TABLE_NAME" WHERE trans_time >= '$START_TIME' and  trans_time <= '$END_TIME'"

DIR_IPAT='/smartpay/data/fec-repayment/IPATFile/'
DIR_SMARTTEST='/smartpay/data/fec-repayment/smarttest/'

TEMP=$(/bin/ls -al $DIR_SMARTTEST | grep $DATE_CHECK | wc -l)

total_row_excel=''
total_record_mysql=''

file=$(/bin/ls $DIR_SMARTTEST | grep $DATE_CHECK)
file_smarttest="$DIR_SMARTTEST""$file"

checkMySQL() {
  echo "SQL query: "$QUERY
  myvariable=$(echo "$QUERY" | mysql sn_utility_reconciliation --host=10.5.18.110 -u thinh.nguyen.2 -p#@j7wjk3Tk.[BLim9KEP7T3V?vT)
  total_row_mysql=$(echo $myvariable | cut -c9-10000)
  echo $total_row_mysql
}

checkFileExcel() {
  echo "path file excel: "$file_smarttest
  if [ $TEMP -eq 0 ]; then
    echo "file not found"
    exit 1
  fi
  total_record_excel=$(echo $(wc -l <$file_smarttest))
  total_record_excel=$(echo $(expr $total_record_excel - 2))
  echo $total_record_excel

}

moveFileExcel() {
  is_check=`expr $total_record_excel == $total_row_mysql`
  if [ $is_check -eq 0 ]; then
    echo "total_excel != total_mysql, please verify."
    exit 1
  fi
  mv file_smarttest "$DIR_IPAT""$FILE_NAME"
  chown chown repay-smn-sys:repay-smn-sys "$DIR_IPAT""$FILE_NAME"
  touch -t DATE_TOUCH"0501" "$DIR_IPAT""$FILE_NAME"
}

if [ $# -lt 1 ]; then
  usage
  exit 1
fi

echo "-------"
echo "STEP 1: Check file excel"
echo "-------"
checkFileExcel
echo "-------"
echo "STEP 2: Check record database mysql"
echo "-------"
checkMySQL
echo "-------"
echo "STEP 3: Move file excel"
echo "-------"
echo ""
echo "CAUTION! Check above files list carefully"
read -p "> Do you want to move file? [y/n] " yn
case $yn in
[Yy]*)
  echo "> Move file..."
  moveFileExcel
  exit 1
  ;;
[Nn]*)
  echo "> Don't move file"
  exit 1
  ;;
*)
  echo "Please answer yes or no."
  exit 1
  ;;
esac
exit $?
