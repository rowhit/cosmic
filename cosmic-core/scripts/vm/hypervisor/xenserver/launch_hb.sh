#! /bin/bash

#set -x

usage() {
  printf "Usage: %s [uuid of this host] [interval in seconds]\n" $(basename $0)

}

if [ -z $1 ]; then
  usage
  exit 2
fi

if [ -z $2 ]; then
  usage
  exit 3
fi

if [ -z $3 ]; then
  usage
  exit 3
fi

if [ ! -f /opt/cloud/bin/xenheartbeat.sh ]; then
  printf "Error: Unable to find xenheartbeat.sh to launch\n"
  exit 4
fi

for psid in `ps -ef | grep xenheartbeat | grep -v grep | awk '{print $2}'`; do
  kill $psid
done

nohup /opt/cloud/bin/xenheartbeat.sh $1 $2 $3 >/dev/null 2>/dev/null &
echo "======> DONE <======"
