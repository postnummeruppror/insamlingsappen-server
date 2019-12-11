#!/bin/bash

mvn install -Dtest=false -DfailIfNoTests=false exec:java -Dexec.mainClass="nu.postnummeruppror.insamlingsappen.Nightly"