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

1. Install LaTeX I used [texlive](https://www.tug.org/texlive/acquire-netinstall.html) on Windows 10 or on Ubuntu: `apt get install texlive-full`
1. Install [Python 3](https://www.python.org/downloads/) or alternatively for Ubuntu based distributions: `apt get install python3`
1. Install esptool: `pip install esptool`
1. Clone this project: `git clone https://github.com/RyWilliamson/Keep-Your-Distance.git`

## ESP32 Board

### Micropython Firmware (Windows 10)

1. Note down the COM port that the device is connected to - in my case COM3. I did this using device manager and looking under the Ports section but any method can be used, for example if you have the arduino IDE you can find the COM port of the device this way.
1. Download the device [firmware](http://micropython.org/download/esp32/). The specific version I used was **GENERIC : esp32-idf3-20200902-v1.13.bin**
1. Erase the existing flash on the ESP32: `esptool.py --chip esp32 --port <COM port> erase_flash`
1. Finally, flash the firmware onto the board: `esptool.py --chip esp32 --port <COM port> --baud 460800 write_flash -z 0x1000 esp32-20190125-v1.10.bin`

### Micropython Firmware (Linux)

If you are using Linux you should be able to use very similar steps to above except you don't need to find the COM port.
You should be able to replace the COM port with /dev/ttyUSB0 - However it should be noted I have not tested this as it differed from my workflow.

`esptool.py --chip esp32 --port /dev/ttyUSB0 erase_flash`

`esptool.py --chip esp32 --port /dev/ttyUSB0 --baud 460800 write_flash -z 0x1000 esp32-20190125-v1.10.bin`

## Dissertation (Windows 10 / Linux)

Navigate to the dissertation directory within the project, inside a terminal, and use the command: `pdflatex l4proj.tex`
