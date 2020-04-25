#!/bin/bash

# initialize input data
speedtrackers_filepath="$(readlink -f data/speedtrackers.2019.sorted.csv.gz)"
taxirides_filepath="$(readlink -f data/taxirides.2019.sorted.csv.gz)"

# initialize commands
dummy_consumer_cmd="./gradlew dummy-consumer:bootRun -Pargs=\"--kafka.topic="

taxirides_consumer_cmd="${dummy_consumer_cmd}taxirides\""
speedtrackers_consumer_cmd="${dummy_consumer_cmd}speedtrackers\""

taxiride_producer_cmd="./gradlew taxiride-producer:bootRun --args=\"-c ${taxirides_filepath} -s 2019-01-01T00:00:00\""
speedtracker_producer_cmd="./gradlew speedtracker-producer:bootRun --args=\"-c ${speedtrackers_filepath} -s 2019-01-01T00:00:00\""

# run stop command killing all but the current pane
if [[ "$1" == "stop" ]]; then
    tmux kill-pane -a -t0
    ./gradlew composeDown
    exit 0
elif [[ "$1" == "start" ]]; then
    # initialize environment
    ./gradlew composeDown composeUp

    tmux splitw -h "${taxirides_consumer_cmd}"
    tmux splitw -v "${speedtrackers_consumer_cmd}"
    tmux selectp -t0
    tmux splitw -v "${speedtracker_producer_cmd}"
    tmux selectp -t0
    eval "${taxiride_producer_cmd}"
fi
