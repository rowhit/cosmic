#! /bin/bash

#set -x

usage() {
  printf "Usage: %s \n" $(basename $0) >&2

}

remove_device() {
  be=$1
  xenstore-write /local/domain/0/backend/tap/0/$be/online 0 &>/dev/null
  xenstore-write /local/domain/0/backend/tap/0/$be/shutdown-request normal &>/dev/null
  for i in $(seq 20)
  do
    sleep 1
    xenstore-exists /local/domain/0/backend/tap/0/$be/shutdown-done &>/dev/null
    if [ $? -eq 0 ] ; then
      xenstore-rm /local/domain/0/device/vbd/$be &>/dev/null
      xenstore-rm /local/domain/0/backend/tap/0/$be &>/dev/null
      xenstore-rm /local/domain/0/error/backend/tap/0/$be &>/dev/null
      xenstore-rm /local/domain/0/error/device/vbd/$be &>/dev/null
      return
    fi
    xenstore-exists /local/domain/0/backend/tap/0/$be &>/dev/null
    if [ $? -ne 0 ] ; then
      return
    fi
  done

  echo "unplug device $be failed"
  exit 2
}

bes=`xenstore-list /local/domain/0/backend/tap/0`

if [ -z "$bes" ]; then
  exit 0
fi

for be in $bes
do
  device=`xenstore-read /local/domain/0/backend/tap/0/$be/dev`
  ls $device >/dev/null 2>&1
  if [ $? -ne 0 ]; then
    remove_device $be
  fi
done


echo "======> DONE <======"
exit 0

