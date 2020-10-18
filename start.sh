#!/bin/bash

#cd /opt/mysongs-backend && java --add-modules java.xml.bind -jar backendapi-0.0.1-SNAPSHOT.jar & > log/app.out 2> log/app.err 
cd /opt/mysongs-backend && java -jar backendapi-0.0.1-SNAPSHOT.jar & > log/app.out 2> log/app.err
