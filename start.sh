#!/bin/bash

export MAVEN_OPTS="-Xmx1G  -Djetty.port=8081 -DsecretKey=secret"
mvn jetty:run