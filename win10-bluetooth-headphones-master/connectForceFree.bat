set /p mac=<%~dp0macFree.txt
btcom -b %mac% -r -s110b
btcom -b %mac% -r -s111e
btcom -b %mac% -c -s110b
btcom -b %mac% -c -s111e

exit 0
