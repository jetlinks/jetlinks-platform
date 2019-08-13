#!/usr/bin/env bash

java -jar device-simulator.jar mqtt.limit=5 mqtt.enableEvent=true mqtt.eventLimit=2
