#!/bin/bash

echo `date` > /opt/mysongs-backend/log/wd.log
echo "Executing watchdog... " >> /opt/mysongs-backend/log/wd.log 
RESULT=`ps -elf | grep java | grep -v grep |  wc -l`
#echo $RESULT
if [ $RESULT -eq 0 ]
then
	echo "Launching app..." >> /opt/mysongs-backend/log/wd.log 
	/opt/mysongs-backend/start.sh
fi
