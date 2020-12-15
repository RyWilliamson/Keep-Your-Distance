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
- _1.5 hours_: Continued reading and annotating An Epidemiology-inspired Large-scale Analysis of Android App Accessibility

## Week 7

### 06 Nov 2020

- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typing up meeting minutes

### 11 Nov 2020

- _0.5 hours_: Interference vectors
- _1 hour_: Setting up Android Studio and the app project

### 12 Nov 2020

- _0.5 hours_: Finishing interference vectors
- _0.5 hours_: Research on how the roles at a high level will work for the Bluetooth connection
- _1.5 hours_: Learning how to and setting up a test user interface
- _4 hours_: Trying to prototype BLE interaction between phone and esp32
- _1 hour_: Getting Android to only return scans for ESP32.
- _2 hours_: Trying to get a "real" connection between the devices instead of just a scan.
- _2 hours_: Trying to read characteristics I set in the esp32 code from the app.

## Week 8

### 13 Nov 2020

- _1 hour_: Managed to get readCharacteristics working correctly, next work on esp32 giving useful data through it.
- _0.5 hours_: Meeting with Jeremy

### 18 Nov 2020

- _1.5 hours_: Refactoring code to create a high level Bluetooth Library.

### 19 Nov 2020

- _2.5 hours_: Trying to get write characteristic working.
- _1.5 hours_: Switching to using Blessed bluetooth library.
- _1 hour_: Getting writing from app to device working with Blessed.
- _1 hour_: Getting writing from device to app working with Blessed.
- _1 hour_: Debugging strange data interaction between data being read and written.
- _2 hours_: Trying to get RSSI data from the device to the app using different characteristic.
- _0.5 hours_: Manually conducting RSSI experimental measurements
- _2 hours_: Inputting data into Excel and generating some visualisations.
- _0.5 hours_: Typed up meeting minutes I'd forgotten about - whoops.

## Week 9

### 20 Nov 2020

- _2 hours_: Got RSSI with read to work
- _1 hour_: Try to get esp32 to notify app when RSSI changes
- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typed up meeting minutes

### 25 Nov 2020

- _1 hour_: Researching navigation view
- _1 hour_: Refactoring to use proper package name
- _6 hours_: Figuring out why Bluetooth doesn't run once changed package name

### 26 Nov 2020

- _3 hours_: Trying to get navhostfragments to work correctly with a shared bluetooth environment
- _1 hour_: Figure out rough programmatic design for experiment
- _1 hour_: Implement bluetooth part of experiment fragment
- _2 hours_: Implement csv support and modify esp32 code to give positive 99 to app for missed connections
- _1 hour_: Running experiment
- _0.5 hours_: Processing data

## Week 10

### 27 Nov 2020

- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typing up meeting minutes

### 01 Dec 2020

- _1 hour_: Research into multiple connections (one device advertising and one mobile connection)
- _6 hours_: Trying different approaches to get concurrent advertising working
- _0.5 hours_: Fixing broken data processing code
- _1 hour_: Set up Jupyter notebook and integration with vscode
- _3 hours_: Spending some time to get to grips with pandas and seaborn

### 02 Dec 2020

- _1 hour_: Finishing up with the plots based off raw data
- _0.5 hours_: Calculating average and missing values
- _2 hours_: Trying to create dataframe for measured power
- _2 hours_: Creating some visualisations with Measured Power

### 03 Dec 2020

- _0.5 hours_: Updated raw rssi violin / swarm plot to be stacked instead of overlayed
- _0.5 hours_: Fixing up graphs
- _2.5 hours_: Adding in and exploring medians for error estimation
- _0.5 hours_: Calculating best value for all distances at each environment

## Week 11

### 04 Dec 2020

- _0.5 hours_: Started work on protocol design
- _0.5 hours_: Meeting with Jeremy
- _0.5 hours_: Typed up meeting minutes
- _0.5 hours_: Continue working on text protocol design
- _1 hour_: Add characteristic properties to protocol design

### 07 Dec 2020

- _1 hour_: Create protocol architecture diagram
- _0.5 hours_: Read over exemplar status report
- _0.5 hours_: Read over ethics checklist
- _5 hours_: Fill out ethics approval form, along with creation of all related documents, checked by supervisor and sent off to Prof Stephen Brewster.
- _1.5 hours_: First three sections of status report draft completed.
- _2 hours_: Finish off status report.

### 08 Dec 2020

- _4 hours_: Level 4 Project Lectures
- _1.5 hours_: Finished up 1st draft of status report before sending to Jeremy.
- _1.5 hours_: Design mockups for device info page, in connected and disconnected state.
- _1 hour_: Design mockup for settings page, in connected and disconnected state.
- _1 hour_: Design mockup for graph page, only one state required.
- _2.5 hours_: Began to implement UI in app.

### 09 Dec 2020

- _1.5 hours_: Setting up global Nav Graph Directions.
- _1.5 hours_: Start implementing fragment ui.
- _1 hour_: Stuck on RecyclerView within fragment.
- _1 hour_: Replace RecyclerView with Spinner.
- _1.5 hours_: Implemented ui with connected state throughout for device fragments, also implemented bottom nav bar that accesses this state.
- _1.5 hours_: Implemented settings fragment with connected state.

### 10 Dec 2020

- _2 hours_: Implemented graph fragment and fixed broken spinners in settings.
- _0.5 hours_: Little bit of polish.

## Week 12

### 11 Dec 2020

- _0.5 hours_: Last meeting with Jeremy for semester.
- _0.5 hours_: Typed up meeting minutes.

### 14 Dec 2020

- _1.5 hours_: Callback design.
- _1 hour_: Restructure code to allow for easier and more readable implementation.
- _0.5 hours_: Add in service and characteristics.
- _1.5 hours_: Refactored connection code on both device and app.
- _2 hours_: Started implementing global and connect fragment callbacks.
- _0.5 hours_: Throwing away repeat scans within the same timeout duration.
- _1 hour_: Paper ER Diagram for RSSI callback.
