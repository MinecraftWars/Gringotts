#!/usr/bin/env bash

mvn -DskipTests=true deploy --settings .travis/znode_deploy.xml
