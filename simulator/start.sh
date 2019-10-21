#!/usr/bin/env bash

java -jar device-simulator.jar mqtt.limit=1000 mqtt.enableEvent=false mqtt.eventLimit=1 mqtt.eventRate=2000
