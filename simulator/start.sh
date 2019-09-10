#!/usr/bin/env bash

java -jar device-simulator.jar mqtt.limit=1 mqtt.enableEvent=false mqtt.eventLimit=2
