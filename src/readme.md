# Readme

## Build instructions - Data Processing

### Requirements

- Python 3.8.6
- Tested on Windows 10

### Build Steps

Run the python files with the relevant raw data.

### Test Steps

N/A

## Build instructions - Android

### Requirements

- Android Studio 4.1.2+
- Java 8 (Tested on version 1.8.0_242)
- Package dependencies defined by Gradle and do not need to be manually installed
- Tested on Windows 10

### Build Steps

Load the project, Configurator folder, in Android Studio, allow the Gradle dependencies to be downloaded. For best results the app should be deployed on a physical smartphone as opposed to the emulator. Connect the smartphone to the computer through USB; follow your phone manufacturers instructions to enable developer mode first. Finally, use the "Run" option within Android Studio, this will build the project and deploy the app to the smartphone.

### Test Steps

Within Android Studio right-click on the tests/java folder and choose either "Run 'Tests in 'java''" or "Run 'Tests in 'java'' with Coverage"

## Build instructions - ESP32

### Requirements

- Visual Studio Code (Tested using version 1.54.3)
- PlatformIO IDE extension for Visual Studio Code v2.3.0
- Tested on Windows 10 (Different build methods will be required for building on Linux)

### Build steps

Open project, esp32 folder, through PlatformIO homescreen in VSCode (**It\'s important to open using the extension and not through vscode natively**). There will be a bottom bar containing buttons related to PlatformIO functions.  
Ensure the profile selected is heltec_wifi_lora_32 (the folder icon).  
Finally, ensure the Heltec ESP32 device is connected to the computer via micro-usb. Then press the upload button (right arrow icon) which will compile the project and upload to the board.

### Test steps

To run the tests select the heltec_wifi_lora_32_test environment then run the test.bat file from the main esp32 directory. This will compile and upload the tests to the board. The test output will then be displayed in a PlatformIO serial terminal.

