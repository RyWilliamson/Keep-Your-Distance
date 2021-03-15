import os
import csv

data_dir = "../../../data/raw/"
participant_a_file = data_dir + "Participant_A/rssi.csv"
participant_b_file = data_dir + "Participant_B/rssi.csv"
timecode_file = "device_search_terms.csv"

def extract_timecodes():
    timecodes = {"A": [], "B": []}
    with open(timecode_file, "r") as file:
        lines = file.readlines()
        for line in lines:
            cleared = line.lstrip("ï»¿").strip("\n").replace("\"", "")
            timecodes["A"].append(int(cleared.split(",")[1]))
            timecodes["B"].append(int(cleared.split(",")[2]))
    return timecodes

def extract_distance(key, in_file, timecodes, distance_dict):
    distance_dict[key] = {"start": [], "end": [], "avg": []}

    with open(in_file, "r") as file:
        lines = file.readlines()
        average = 0
        average_count = 0
        currently_counting = False
        for line in lines[1:]:
            data = line.strip("\n").replace("\"", "").split(",")
            timestamp = int(data[3])
            dist = float(data[5])
            if timestamp in timecodes[key]:
                if currently_counting: # found end of action
                    average += dist
                    average_count += 1
                    distance_dict[key]["end"].append(dist)
                    distance_dict[key]["avg"].append(average / average_count)
                    average = 0
                    average_count = 0
                    currently_counting = False
                else: # start of action
                    distance_dict[key]["start"].append(dist)
                    average += dist
                    average_count += 1
                    currently_counting = True
            else:
                if currently_counting: # middle of action
                    average += dist
                    average_count += 1

timecodes = extract_timecodes()
distance_dict = {}
extract_distance("A", participant_a_file, timecodes, distance_dict)
extract_distance("B", participant_b_file, timecodes, distance_dict)

with open("distances.csv", "w", newline='') as output_file:
    datawriter = csv.writer(output_file, delimiter=",")
    datawriter.writerow(["Device A", "Device B"])
    for i in range(len(distance_dict["A"]["start"])):
        record_start = [distance_dict["A"]["start"][i], distance_dict["B"]["start"][i]]
        record_end = [distance_dict["A"]["end"][i], distance_dict["B"]["end"][i]]
        datawriter.writerow(record_start)
        datawriter.writerow(record_end)