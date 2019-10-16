#!/usr/bin/env bash

java -jar device-simulator.jar mqtt.limit=1 mqtt.enableEvent=true mqtt.eventLimit=1 mqtt.eventRate=2000
