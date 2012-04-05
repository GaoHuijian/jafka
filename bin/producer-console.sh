#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
# 
#    http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

if readlink -f "$0" > /dev/null 2>&1
then
    SERVER_BIN=`readlink -f "$0"`
else
    SERVER_BIN="$0"
fi
BIN_HOME=`dirname ${SERVER_BIN}`
JAFKA_HOME=`dirname ${BIN_HOME}`
export JAFKA_HOME


. ${BIN_HOME}/env.sh

java com.sohu.jafka.console.ConsoleProducer $*
