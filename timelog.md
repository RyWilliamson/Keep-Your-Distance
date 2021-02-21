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

### 15 Dec 2020

- _1 hour_: Electronic ER Diagram for RSSI callback.
- _4 hours_: Working on database implementation within Android.
- _4 hours_: Needed to switch to using LiveData, Repositories, and a ViewModel for async database calls as Android (rightfully) rejects making database calls on the UI thread.

### 16 Dec 2020

- _1.5 hours_: Figuring out non-observable queries (queries with variable runtime parameters).
- _0.5 hours_: toString methods for database entities for my sanity.
- _3 hours_: Working on implementing some callback functionality and also integrating the Shared Preferences system to store the previous mac address.
- _1 hour_: Refactoring code.
- _0.5 hours_: Creating algorithm to send both mac address and rssi in a single characteristic without hitting byte limit.
- _0.5 hours_: Implementing this algorithm on the device side.
- _1 hour_: Implementing this algorithm on the app side.

### 17 Dec 2020

- _4 hours_: Adding RSSI functionality to database, involved handling timeout waiting and start time caching.
- _3 hours_: Trying to add a timeout to the interactions so the interactions end.

## Winter Break Weeks

### 18 Dec 2020

- _0 hours_: Break day :)

### 19 Dec 2020

- _0.5 hours_: Added ethics approval pdfs and sorted out the main readme a bit.
- _1.5 hours_: Getting the device info page characteristic update callback functioning.

### 20 Dec 2020

- _0.5 hours_: Fixing small bug on device causing ble notification of null value.
- _1 hours_: Implementing clear button in device info page.
- _1 hours_: Implement Graph and Settings BLE callbacks.

### 21 Dec 2020

- _0.5 hours_: Setting up profile prefs enum with values.
- _3 hours_: Begin Implementation of settings on Android side.

### 06 Jan 2021

- _1 hour_: Got profile spinner to work with profile enums and shared preferences.
- _0.5 hours_: Got distance spinner working similarly.
- _2 hours_: Trying to get config data to send from Android app to ESP32.
- _1.5 hours_: Got measured power and environment variable transferring and read correctly.
- _2 hours_: Dealt with problems converting bytes to float on the ESP32.
- _1.5 hours_: Weird issue with not having multiple notifications be able to work concurrently.

### 07 Jan 2021

- _0.5 hours_: Figured out the above issue was to do with the Arduino code not correctly setting descriptors as I was trying to reuse the same descriptor object rather than creating new ones through the BLE2902 template.
- _0.5 hours_: Got the settings screen to display if it is Synced or Not Synced.
- _1 hour_: Refactoring arduino code.
- _0.5 hours_: Reading up on link layer of BLE, realised that implementing my own ACKs us not necessary as this is handled in the link layer - similar for data redundancy checks.
- _0.5 hours_: Trying to understand write responses on the arduino side - these are working on Android side from the Blessed library.
- _1.5 hours_: Started implementing bulk rssi transfer - still do need ack here since arduino ble library doesn't expose a callback that works in response to the acknowledgement.
- _1 hour_: Research on how to handle the timestamps for the bulk rssi transfer as I can't rely on the Android app like I do for regular transfers.

### 08 Jan 2021

- _0.5 hours_: More research on timing solutions for esp32.
- _2.5 hours_: Rewriting RSSI packet on Arduino side in preparation for adding bulk rssi storage and transfer.
- _0.5 hours_: Writing the bulk packet transfer on Arduino side.
- _0.5 hours_: Refactored the RSSI packet receiving code on the Android side.
- _2.5 hours_: Working to fully implement bulk transfer.
- _1.5 hours_: Bulk transfer is in reverse order - implementing a circular queue to solve this.
- _1 hour_: Implemented circular queue.
- _1 hour_: Rewrote timeout code in a completely new way so that it can work with bulk rssi transfer - it is also more efficient overall.
- _2 hours_: Updated callback design spec and diagram, also started creating some packet diagrams.

### 09 Jan 2021

- _1.5 hours_: Final packet diagram - config packet - also implemented a function on device common library to print byte array as hex string.
- _1 hour_: Reversed the distance calculation formula to be able to calculate a target rssi to use from the config distance provided.
- _2 hours_: Working on partial screen filling as there is no default function for this in U8x8.
- _0.5 hours_: Finished on-device notification via screen - function separated out so can be expanded for other forms of feedback.
- _0.5 hours_: Implementing nested fragment switching - allows for switching between graphs in graph view.
- _1.5 hours_: Exploring options for charting libraries in Android.
- _2 hours_: Learning MPAndroidChart.

### 10 Jan 2021

- _1.5 hours_: Weekly histogram implemented with test data - not yet with real data.
- _1 hour_: Tweaking style of histogram.
- _0.5 hours_: Fixed arduino code so that it only transmits on a correct interaction.
- _2 hours_: Finished weekly histogram interaction with real data.
- _1 hour_: Adding ability to choose device that you want graphs for - allows for decoupling it from connection logic and just lets it use database information.
- _2 hours_: Making this new device spinner use the alias system in preparation for a more full-on move to this system.

# Week 13

### 11 Jan 2021

- _3 hours_: Trying to solve an async bug where the graph doesn't draw when you enter the page but only after a selection is made.
- _0.5 hours_: Fix x axis labels.
- _0.5 hours_: Allow graph update on change of device selection - this entire area of code needs serious refactoring.
- _0.5 hours_: Refactor graph and fix labels.
- _1 hour_: Begin implementation of trend graph.
- _0.5 hours_:  Meeting with Jeremy.
- _0.5 hours_:  Typed minutes and emailed Jeremy my address for mailing me parts.

### 12 Jan 2021

- _7 hours_: Continue trend graph implementation - Gradient lines are very difficult, this commit has the colour gradient working but no real data interaction yet.
- _1 hour_: Continue trend graph implementation - coupling it with real data.
- _0.5 hours_: Making gradient be sharper and also colour blind friendly.

### 13 Jan 2021

- _1 hour_: Trying to design the algorithms and data structures for handling smoothing out rssi values over time.
- _1 hour_: Implementing node type for red-black trees and creating node design of bytes.
- _9 hours_: Successfully? implemented red-black trees.

### 14 Jan 2021

- _1 hour_: Designing algorithm to use the red-black tree.
- _2 hours_: Implementing this algorithm.

### 16 Jan 2021

- _1.5 hours_: Fixing bugs with new RSSI system that I hadn't noticed.
- _1.5 hours_: Update red-black tree node struct and required code to move to using a float. Previously using int16_t which meant that on each EWA calculation some data was being lost which meant for less reliable and smooth averaging.
- _1.5 hours_: Trying to sort trend graph labels - best approach is to only display first and last labels but having to work around the library using a bit of a hacky approach.

### 17 Jan 2021

- _2 hours_: Still trying to fix trend labels - new approach to set label drawing to false and try overlay textviews at the start and end positions.
- _2.5 hours_: Implement device alias naming.
- _1 hour_: Fix reconnection bug.
- _1 hour_: Start Duration-Distance pie chart fragment implementation.

# Week 14

### 18 Jan 2021

- _5 hours_: Continue Duration-Distance pie chart fragment implementation.
- _0.5 hours_:  Meeting with Jeremy.
- _0.5 hours_:  Typed minutes.
- _1 hour_: Tracked and solved indexing bug on opening graph view - this was a race condition so added a new thread runnable to wait until the deviceList was ready before setting the graph.
- _2 hours_: Removing pie slice labels when they get too small - This functionality is heavily integrated with MPAndroidChart so the best solution was to remove it drawing all labels and have the user read the legend, then when the slice is too small set the value string to "" in the value formatter.

### 19 Jan 2021

- _0.5 hours_:  Fix remaining 0's bug on device log count.
- _1 hour_:  Research to see if it would be possible to implement resolvable private addresses with remaining implementation time (i.e today). Decided not feasible as using resolvable private addressing requires bonding which is not feasible on the esp 32 device.
- _2 hours_: Implement export functionality - forgot about this.
- _1 hours_: Research & implement auto-connect.

### 20 Jan 2021

- _1 hour_: Research on unit testing.

### 21 Jan 2021

- _2 hours_: Learning AUnit.
- _3 hours_: Trying to get some Red-Black tree tests written.

### 22 Jan 2021

- _1 hour_: Started to try implement tests for main functionality of the Arduino - this is going to require a large refactor.
- _3 hours_: Circular Queue Refactored into own class, can now be tested.

### 23 Jan 2021

- _0.5 hours_: Added search tests to tree and added testing flag to circular queue to avoid screen and serial print issues.
- _2 hours_: Added some Circular Queue tests.

### 24 Jan 2021

- _1 hour_: Changing test configuration to allow control of terminal after tests complete.
- _1 hour_: Implementing more log tests - fix bug with queue size.
- _5 hours_: Thinking of survey questions

# Week 15

### 25 Jan 2021

- _1 hour_: Setting up recording software and grabbing static app images that I need.
- _0.5 hours_: Started to record app videos then encountered app crashing rename bug. Fixed this bug.
- _1.5 hours_: Continuation of recording.
- _1 hour_: Editing photos and video.
- _2 hours_: Working on creating Google Form survey.
- _0.5 hours_: Meeting with Jeremy.
- _0.5 hours_: Typed minutes.
- _1 hour_: Fixed up and updated survey.
- _1 hour_: Re-recorded videos.

### 26 Jan 2021

- _1 hour_: Rewrote consent section of survey and added demographic question.
- _0.5 hours_: Sent email about needing to get ethics checklist signed before sending survey.
- _1 hour_: Refactored and tested config data section of arduino code.
- _3 hours_: Refactored and began testing RSSI handler.

### 27 Jan 2021

- _2.5 hours_: Finished writing RSSI Handler tests

### 28 Jan 2021

- _0.5 hours_: Sending my survey to various groups
- _1 hour_: Begin reading and highlighting COVID-19 what have we learned? The rise of social machines and connected devices in pandemic management following the concepts of predictive, preventive and personalized medicine

### 29 Jan 2021

- _1 hour_: Finish reading and highlighting COVID-19 what have we learned? The rise of social machines and connected devices in pandemic management following the concepts of predictive, preventive and personalized medicine
- _1 hour_: Summarise COVID-19 what have we learned? The rise of social machines and connected devices in pandemic management following the concepts of predictive, preventive and personalized medicine

# Week 15

### 01 Feb 2021

- _3 hours_: Writing the motivation section of the dissertation.

### 02 Feb 2021

- _0.5 hours_: Integrating the vibration motors into my notification system.

### 07 Feb 2021

- _1.5 hours_: Getting app working again after phone update broke some functionality.
- _1.5 hours_: Fixing other related bugs.
- _1.5 hours_: Setting up and performing outdoor experiment.
- _0.5 hours_: Re-doing experiment as results were not a good representation due to experimental setup of them still being inside the styrofoam.
- _1 hour_: Write updated output data parser to work with new csv output format.
- _0.5 hours_: Create and save new graphs.
- _1 hour_: Setup and perform same experiment again, except this time with the device facing pin side up.
- _0.5 hours_: Reverse indexing on data processing procedure.
- _0.5 hours_: Create and save new graphs.

# Week 16

### 08 Feb 2021

- _0.5 hours_: Fix App bug on loading after clear.
- _1.5 hours_: Setting up and performing both the up and down indoor test.
- _0.5 hours_: Creating graphs from the test data.
- _0.5 hours_: Writing email to Jeremy.
- _1.5 hours_: Finishing first draft on intro section.
- _0.5 hours_: Finish creating and saving graphs from test data.
- _0.5 hours_: Meeting with Jeremy.
- _0.5 hours_: Typing up meeting notes.

### 09 Feb 2021

- _1 hour_: Track down and fix evasive clear bug that drops all records in rssi - due to cascading delete from device
- _1 hour_: Track down and fix screen corruption bug - somewhat fixed but sometimes leftover artifacting when stopping an interaction while transferring stored bulk interactions.
- _0.5 hours_: Change default app screen text.
- _0.5 hours_: Fix crash on disconnecting during data clear.
- _1.5 hours_: Preparing experiment script, identifying experiment variables, and creating end of experiment survey.
- _2.5 hours_: Designing a new version of the exploratory data analysis, then carrying it out for indoor and outdoor settings.
- _0.5 hours_: Data analysis and graph saving.
- _0.5 hours_: Changing profile values on app and default device values.
- _0.5 hours_: Making changes suggested to intro section of dissertation based on Jeremy's feedback.

### 10 Feb 2021

- _3 hours_: Experiment with parents.

# Week 17

### 20 Feb 2021

- _2.5 hours_: Trying to get JUnit, Roboelectric, and Mockito to work.
- _2.5 hours_: Bluetooth Handler Unit Tests.
- _1 hour_: Profile / ProfileEnum Unit Tests.
- _0.5 hours_: Spinner Utils Unit Tests.
- _0.5 hours_: Timeout Unit Tests.
- _1 hour_: Main Activity Unit Tests.
- _4 hours_: Figure out how to unit test Room Repository.
- _1 hour_: Device Repository Unit Tests.
- _1 hour_: Interaction Repository Unit Tests.


### 21 Feb 2021

- _1.5 hours_: RSSI Repository Unit Tests.