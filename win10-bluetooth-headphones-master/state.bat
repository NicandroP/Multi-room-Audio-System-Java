IF "%TOGGLE_BLUETOOTH_HEADPHONES_BUTTON_STATE%"=="1" (
    SETX TOGGLE_BLUETOOTH_HEADPHONES_BUTTON_STATE "2"
    exit 1
) else (
    SETX TOGGLE_BLUETOOTH_HEADPHONES_BUTTON_STATE "1"
    exit 2
)
