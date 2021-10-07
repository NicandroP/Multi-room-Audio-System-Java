set /p mac=<%~dp0macFree.txt
btcom -b %mac% -r -s110b
btcom -b %mac% -r -s111e

exit 0
