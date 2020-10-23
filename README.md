# Guidance

This is a suggested template for a project. You can modify it as you please, but
but remember to keep:

- a timelog, updated regularly in the `timelog.md` format;
- all source under version control;
- data well organised and with appropriate ethical approval (for human subject data);

Here's an overview of the structure as it stands:

- `timelog.md` The time log for your project.
- `plan.md` A skeleton week-by-week plan for the project.
- `data/` data you acquire during the project
- `src/` source code for your project
- `status_report/` the status report submitted in December
- `meetings/` Records of the meetings you have during the project.
- `dissertation/` source and for your project dissertation
- `presentation/` your presentation

- Make sure you add a `.gitignore` or similar for your VCS for the tools you are using!
- Add any appropriate continuous integration (e.g. Travis CI) in this directory.

- Remove this `readme.md` file from any repository and replace it with something more appropriate!

## Important

- It should be easy to rebuild and run your project and your dissertation \* Include clear instructions in the relevant directories to make this possible

# Build Process

I used Windows 10 throughout development. I have tried to include Linux instructions however these have not been tested so may not work as intended.

## Prerequisites

1. Install LaTeX I used [texlive](https://www.tug.org/texlive/acquire-netinstall.html) on Windows 10 or on Ubuntu: `apt-get install texlive-full`
1. Install [Python 3](https://www.python.org/downloads/) or alternatively for Ubuntu based distributions: `apt-get install python3 python3-pip`
1. Install esptool: `pip install esptool`
1. Install ampy: `pip install adafruit-ampy` (may need to be run in an elevanted prompt)
1. Install [Git](https://git-scm.com/downloads) or for Ubuntu `apt-get install git`.
1. Clone this project: `git clone https://github.com/RyWilliamson/Keep-Your-Distance.git`

## ESP32 Board

### Custom Firmware (WSL1)

Here I'll detail the process used to build the custom boot firmware. I'm using WSL for this as micropython was designed to be built on Linux systems.
Specifically I'm using Ubuntu. Note this is NOT required to build my project as I have provided the final custom bin file as part of this git repository. This is intended purely as a reference for the curious and for myself a few months down the line.

**Also note WSL2 WILL NOT work as it does not have support for USB access.**

1. Install Python 3 as in pre-requisites.
   1. Also install Python 3 venv: `apt-get install python3-venv`
1. Install build-essential: `apt-get install build-essential`
1. Install libffi-dev: `apt-get install libffi-dev`
1. Install pkg-config: `apt-get install pkg-config`
1. Clone my micropython fork: `git clone https://github.com/RyWilliamson/micropython.git`
1. Get required ESP32 dependencies

   ```console
    $ cd ports/esp32 # Start within micropython clone
    $ make ESPIDF=  # This will print the supported hashes, copy the 3.3 version.
    $ export ESPIDF="/mnt/e/Users/Ryan/Documents/University/4th_Year/Level_4_Project/esp-idf"  # Any path with **no spaces** can be used
    $ mkdir -p "$ESPIDF"
    $ cd "$ESPIDF"
    $ git clone https://github.com/espressif/esp-idf.git "$ESPIDF"
    $ git checkout 9e70825d1e1cbf7988cf36981774300066580ea7 # If hash is different use that one
    $ git submodule update --init --recursive
   ```

1. Now setup python virtual environment

   ```console
    $ cd ports/esp32 # start within micropython clone
    $ python3 -m venv build-venv
    $ source build-venv/bin/activate
    $ pip3 install --upgrade pip
    $ pip3 install -r "$ESPIDF/requirements.txt"
    $ # In future sessions do the following
    $ cd ports/esp32 # start within micropython clone
    $ source build-venv/bin/activate
   ```

1. Download and setup ESP32 Toolchain: [instructions here](https://docs.espressif.com/projects/esp-idf/en/v3.3.2/get-started/linux-setup.html)
1. Create a new file in esp32 directory called GNUmakefile then copy the following into it, change port number to where the serial device is plugged in (for me this was COM3)

   ```
   ESPIDF ?= "/mnt/e/Users/Ryan/Documents/University/4th_Year/Level_4_Project/esp-idf"
   BOARD ?= GENERIC
   PORT ?= /dev/ttyS3
   BAUD ?= 115200
   #FLASH_MODE ?= qio
   #FLASH_SIZE ?= 4MB
   #CROSS_COMPILE ?= xtensa-esp32-elf-

   include Makefile
   ```

1. To Build the firmware - starting from micropython main directory

   1. First build cross-compiler

      ```console
      $ cd mpy-cross
      $ make mpy-cross
      ```

   1. Then build to Micropython run

      ```console
      $ cd ../ports/esp32
      $ make submodules
      $ make
      ```

   1. This will get you a 'clean' firmware with no main program running
      1. To add programs copy python files into ports/esp32/modules in the micropython clone - Note: Will not appear in flash file system as they have been frozen as byte code into firmware.
      1. Should have a main.py file if you want esp32 to run a program at boot.

### Micropython Firmware (Windows 10)

1. Note down the COM port that the device is connected to - in my case COM3. I did this using device manager and looking under the Ports section but any method can be used, for example if you have the arduino IDE you can find the COM port of the device this way.
1. Download the device [firmware](http://micropython.org/download/esp32/). The specific version I used was **GENERIC : esp32-idf3-20200902-v1.13.bin**
1. Erase the existing flash on the ESP32: `esptool.py --chip esp32 --port <COM port> erase_flash`
1. Flash the firmware onto the board: `esptool.py --chip esp32 --port <COM port> --baud 460800 write_flash -z 0x1000 src/firmware/firmware.bin`
1. Use TeraTerm or equivalent way to enter the following commands in REPL. `import esp` then `esp.osdebug(None)`. Find better way to do this

### Micropython Firmware (Linux)

If you are using Linux you should be able to use very similar steps to above except you don't need to find the COM port.
You should be able to replace the COM port with /dev/ttyUSB0 - However it should be noted I have not tested this as it differed from my workflow.

`esptool.py --chip esp32 --port /dev/ttyUSB0 erase_flash`

`esptool.py --chip esp32 --port /dev/ttyUSB0 --baud 460800 write_flash -z 0x1000 firmware.bin`

### Micropython File Interaction (Windows 10 / Linux)

Here we use ampy to interact with the file system on the esp32 boards. There are a few key commands used during development namely: get, ls, put, reset, rm, and run.

1. Put the ss1306 driver onto the board: `ampy -p COM3 -b 115200 put ss1306.py`
1. Run the main file for testing on the board: `ampy -p COM3 -b 115200 run main.py`
1. If running a file that loops forever, or for a long time use the `--no-output` after `run`

## Dissertation (Windows 10 / Linux)

Navigate to the dissertation directory within the project, inside a terminal, and use the command: `pdflatex l4proj.tex`
