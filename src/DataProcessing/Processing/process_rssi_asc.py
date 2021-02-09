import os
import csv

input_filename = "../../../data/raw/rssi_indoor_up.csv"
output_file = "../../../data/processed/rssi_processed_indoor_up.csv"

expectedAmount = 250
counter = 0
index = 1

prevTime = None

outputs = []

def writeHeaders(datawriter, headers):
    datawriter.writerow(headers)

for i in range(expectedAmount):
    outputs.append([i, 0, 0, 0, 0, 0])

with open(input_filename, "r", encoding="utf-8", newline='') as inputF:
    inputF.readline() # Skip headers
    while index < 6:
        data = inputF.readline().split(',')
        if data == ['']:
            break
        timestamp = int(data[3].replace("\"", ""))
        if (prevTime is not None and timestamp - prevTime > 40000):
            # Next set of values
            counter = 0
            index = index + 1
            if (index == 6):
                break
        elif (prevTime is not None and counter < expectedAmount and timestamp - prevTime > 2000):
            # Insert Nones
            for i in range( (timestamp - prevTime - 1000) // 1000 ):
                print(counter)
                print(timestamp)
                print(prevTime)
                outputs[counter][index] = None
                counter += 1
                if (counter >= expectedAmount):
                    break
        
        # Add to output
        prevTime = timestamp
        if (counter >= expectedAmount):
            continue

        outputs[counter][index] = int(data[4].replace("\"", ""))
        counter += 1

with open(output_file, "w", encoding="utf-8", newline='') as output:
    datawriter = csv.writer(output, delimiter=",")
    writeHeaders(datawriter, ["Scan No", "0.5m", "1.0m", "1.5m", "2.0m", "2.5m"])
    for i in range(expectedAmount):
        datawriter.writerow(outputs[i])

    