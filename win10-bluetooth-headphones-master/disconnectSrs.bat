set /p mac=<%~dp0macSrs.txt
btcom -b %mac% -r -s110b
btcom -b %mac% -r -s111e

exit 0
