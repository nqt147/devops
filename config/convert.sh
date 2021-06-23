#!/bin/bash

if [ -z $1 ]; then
  echo "Path file runserver.sh invalid"
  echo "Ex: /smartpay/script/convert_docker_file.sh runserver.sh"
  exit 1
fi

IS_CHECK=$(/bin/ls -al Dockerfile | wc -l)

if [ $IS_CHECK -eq 0 ]; then
  touch Dockerfile
  chmod +x Dockerfile
  echo "" > Dockerfile
fi


input="$1"
LINE=""
FINAL_LINE="FROM java:8 \n"
RUN='ENV TZ=Asia/Ho_Chi_Minh \n
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE $PORT
RUN useradd -ms /bin/bash sdeploy -u 1006
WORKDIR /home/sdeploy
WORKDIR /$HOME/logs
WORKDIR $JMXTRANS_HOME
ADD jmxtrans-agent-1.2.6.jar $JMXTRANS_HOME
ADD jmxtrans-agent.xml $JMXTRANS_HOME
WORKDIR $HOME
ADD . $HOME
RUN chown -R sdeploy:sdeploy $HOME;chown -R sdeploy:sdeploy $JMXTRANS_HOME; chown -R sdeploy:sdeploy $HOME/logs
RUN chmod 755 $HOME; chmod 755 $JMXTRANS_HOME; chmod 755 $HOME/logs
USER sdeploy
CMD java $JVM_OPTS -jar $JARFILE > $log_file'

while IFS= read -r line; do
  firstChar=${line:0:1}
  if [[ $firstChar == "#" ]]; then
    continue
  fi

  if [[ $firstChar == "-" ]]; then
    LINE="$line"
    FINAL_LINE="$FINAL_LINE$LINE \n"
    if [[ "$line" == *"javaagent"* ]]; then
      break
    fi
    continue
  fi

  if [[ "$line" == *"export"* ]]; then
    LINE="${line/export/ENV}"
    FINAL_LINE="$FINAL_LINE$LINE \n"
  fi

  if [[ "$line" == *"log_file"* ]] ||[[ "$line" == *"pid_file"* ]] || [[ "$line" == *"JVM_OPTS"* ]]; then
    LINE="ENV $line"
    FINAL_LINE="$FINAL_LINE$LINE \n"
  fi

done <"$input"
FINAL_LINE="$FINAL_LINE$RUN"


echo -e "$FINAL_LINE" >> Dockerfile