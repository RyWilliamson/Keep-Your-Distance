# Timelog

- Keep your distance! Wearable Bluetooth proximity sensors
- Ryan Williamson
- 2306841w
- Jeremy Singer

## Week 1

### 29 Sep 2020

- _2 hours_: Sent inital email to advisor, prepared repo, and installed / setup git and LaTeX.

### 01 Oct 2020

- _4 hours_: Created Mindmap.
- _2 hours_: Worked on pre meeting notes and sourcing readings.
- _0.5 hours_: Read through some of the ESP 32 documentation and made some notes.

## Week 2

### 07 Oct 2020

- _1 hour_: Consolidate some thoughts I've had over the week into conveyable points.

### 08 Oct 2020

- _1 hour_: Modified Mindmap with thoughts that had come up over the week
- _0.5 hours_: User persona traits ideas
- _0.5 hours_: Looking for research papers
- _2 hours_: User persona research and development
- _0.5 hours_: User stories research and development
- _0.5 hours_: User scenarios research and beginning development

### 09 Oct 2020

- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typed up meeting minutes

## Week 3

### 09 Oct 2020

- _2.5 hours_: Finishing off user scenarios
- _1 hours_: Added user scenarios to github wiki, took longer than expected due to github markdown lacking good colour support
- _0.5 hours_: Created entry for an idea today in my Research Journal section in personal notes.

### 14 Oct 2020

- _1.5 hours_: Timeline

### 15 Oct 2020

- _1 hour_: Expanded collection of papers for literature review.
- _1 hour_: Skimmed some hall of fame dissertations to see structure, reference frequency, etc.
- _0.5 hours_: Wrote a very early draft of an abstract.
- _1 hour_: Tried to read a couple of the papers I'd found but after reading Title, Introduction, and Conclusion found that they weren't as useful as hoped
- _1 hour_: Read and summarised A Multi-step Approach for RSSI-Based Distance Estimation Using Smartphones

## Week 4

### 16 Oct 2020

- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typed up meeting minutes
- _1.5 hours_: Getting Micropython flashed onto one of the boards
- _0.5 hours_: Writing instructions for build process so far, including LaTeX.
- _1 hour_: Hit a roadblock with working from WSL2 - can't access the serial ports via WSL only through windows. This means that I either need to move to a full Linux virtual machine or do all development on Windows.
- _1.5 hours_: Switched project over to using Windows as opposed to developing via WSL.

### 18 Oct 2020

- _1.5 hours_: Setting up TeraTerm to access board repl - want to find a better way to do this.
- _4 hours_: Getting screen to display anything.
- _0.5 hours_: Setting up vscode esp stubs.

### 20 Oct 2020

- _0.5 hours_: Flashing micropython and testing second board works.
- _1 hour_: Researching flashing custom files onto board rather than using REPL and ampy for final version.
- _5.5 hours_: Setting up WSL toolchain for building custom firmware.
- _0.5 hours_: Trying to figure out why make erase and make deploy don't function correctly but copying the built firmware and using esptool on Windows does work.

### 21 Oct 2020

- _0.5 hours_: Built custom firmware with custom boot code, and put it onto board.
- _1.5 hours_: Trying a few things and setting up a couple of scripts to ease my life.

### 22 Oct 2020

- _0.5 hours_: Added the idea from Jeremy as a User Persona, Story, and Scenario.
- _1 hour_: Looking into ubluetooth.
- _1 hour_: Getting 5 papers on current contact tracing methods, their effects, and concerns for literature review.
- _1 hour_: Started reading and annotating A Survey of Contact Tracing COVID 19 apps
- _0.5 hours_: Looking at Hall of Fame dissertations and coming up with some of my dissertation structure.

## Week 5

### 23 Oct 2020

- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typed up meeting minutes and put them on Github.
- _0.5 hours_: Added instruction to readme and fixed up some commits.

### 24 Oct 2020

- _1 hour_: Continued reading and annotating A Survey of Contact Tracing COVID 19 apps
- _1 hour_: Wrote a level 3 summary for A Survey of Contact Tracing COVID 19 apps
- _1.5 hours_: Read and annotated A Privacy-preserving Mobile and Fog Computing Framework to Trace and Prevent COVID-19 Community Transmission
- _0.5 hours_: Wrote a level 3 summary for A Privacy-preserving Mobile and Fog Computing Framework to Trace and Prevent COVID-19 Community Transmission
- _1 hour_: Read and annotated ProxiTrak: a robust solution to enforce real-time social distancing & contact tracing in enterprise scenario
- _1 hour_: Wrote a level 3 summary for ProxiTrak: a robust solution to enforce real-time social distancing & contact tracing in enterprise scenario

### 25 Oct 2020

- _1 hour_: Read and Annotated Technical Perspectives of Contact-Tracing Applications on Wearables for COVID-19 Control
- _1 hour_: Wrote a level 3 summary for Technical Perspectives of Contact-Tracing Applications on Wearables for COVID-19 Control
- _1.5 hours_: Read and Annotated Evaluating How Smartphone Contact Tracing Technology Can Reduce the Spread of Infectious Diseases: The Case of COVID-19
- _1 hour_: Wrote a level 3 summary for Evaluating How Smartphone Contact Tracing Technology Can Reduce the Spread of Infectious Diseases: The Case of COVID-19

### 28 Oct 2020

- _0.5 hours_: Ruled out An efficient algorithm to estimate Covid-19 infectiousness risk from BLE-RSSI measurements as a good paper for my project
- _1 hour_: Read and Annotated A wearable magnetic field based proximity sensing system for monitoring COVID 19 social distancing
- _1 hour_: Wrote a level 3 summary for A wearable magnetic field based proximity sensing system for monitoring COVID 19 social distancing

### 29 Oct 2020

- _1.5 hour_: Read and Annotated Overview and Evaluation of Bluetooth Low Energy: An Emerging Low-Power Wireless Technology
- _1 hour_: Wrote a level 3 summary for Overview and Evaluation of Bluetooth Low Energy: An Emerging Low-Power Wireless Technology
- _1 hour_: Writing Functional and Non-functional MOSCOW requirements
- _0.5 hours_: Sourcing a paper on accessiblity considerations for apps.

## Week 6

### 30 Oct 2020

- _0.5 hours_: Started reading and annotating An Epidemiology-inspired Large-scale Analysis of Android App Accessibility
- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typing up meeting minutes

### 03 Nov 2020

- _1 hour_: Trying to get ubluetooth to broadcast
- _0.5 hours_: Give up and use Arduino IDE
- _1 hour_: Setup PlatformIO at recommendation of ESE friend
- _1.5 hours_: Get bluetooth broadcasting and screen example working
- _1 hour_: Bluetooth advertising and scanning

### 05 Nov 2020

- _0.5 hours_: Actually have two esp32's able to 'see' each other - Exciting!
- _1.5 hours_: Separated bluetooth logic into its own library for my own sanity.
- _1.5 hours_: Worked on displaying RSSI and very early crude version of distance estimation.
