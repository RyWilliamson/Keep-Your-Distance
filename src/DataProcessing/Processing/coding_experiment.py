import os
import csv
import sys
from collections import defaultdict

def def_value():
    return "Other"

open_to_axial_map = defaultdict(def_value)

open_to_axial_map["O1"] = "A1"
open_to_axial_map["O2"] = "A1"
open_to_axial_map["O3"] = "A1"
open_to_axial_map["O4"] = "A6"
open_to_axial_map["O5"] = "A5"
open_to_axial_map["O6"] = "A5"
open_to_axial_map["O7"] = "A1"
open_to_axial_map["O8"] = "A6"
open_to_axial_map["O9"] = "A1"
open_to_axial_map["O10"] = "A4"
open_to_axial_map["O11"] = "A4"
open_to_axial_map["O12"] = "A4"
open_to_axial_map["O13"] = "A4"
open_to_axial_map["O14"] = "A8"
open_to_axial_map["O15"] = "A7"
open_to_axial_map["O16"] = "A6"
open_to_axial_map["O17"] = "A6"
open_to_axial_map["O18"] = "A6"
open_to_axial_map["O19"] = "A6"
open_to_axial_map["O20"] = "A7"
open_to_axial_map["O21"] = "A6"
open_to_axial_map["O22"] = "A7"
open_to_axial_map["O23"] = "A6"
open_to_axial_map["O24"] = "A7"
open_to_axial_map["O25"] = "A7"
open_to_axial_map["O26"] = "A6"
open_to_axial_map["O27"] = "A7"
open_to_axial_map["O28"] = "A6"
open_to_axial_map["O29"] = "A7"
open_to_axial_map["O30"] = "A6"
open_to_axial_map["O31"] = "A7"
open_to_axial_map["O32"] = "A6"
open_to_axial_map["O33"] = "A7"
open_to_axial_map["O34"] = "A6"
open_to_axial_map["O35"] = "A7"
open_to_axial_map["O36"] = "A6"
open_to_axial_map["O37"] = "A7"
open_to_axial_map["O38"] = "A6"
open_to_axial_map["O39"] = "A6"
open_to_axial_map["O40"] = "A7"
open_to_axial_map["O41"] = "A2"
open_to_axial_map["O42"] = "A3"
open_to_axial_map["O43"] = "A3"
open_to_axial_map["O44"] = "A2"
open_to_axial_map["O45"] = "A7"
open_to_axial_map["O46"] = "A7"
open_to_axial_map["O47"] = "A1"
open_to_axial_map["O48"] = "A2"
open_to_axial_map["O49"] = "A7"
open_to_axial_map["O50"] = "A6"
open_to_axial_map["O51"] = "A6"
open_to_axial_map["O52"] = "A7"
open_to_axial_map["O53"] = "A6"
open_to_axial_map["O54"] = "A7"
open_to_axial_map["O55"] = "A7"
open_to_axial_map["O56"] = "A9"
open_to_axial_map["O57"] = "A7"

result_dict = {}
main_codes = ["A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "Other"]

with open(sys.argv[1], "r") as input_file:
    lines = input_file.readlines()
    for line in lines:
        name = line.lstrip("ï»¿").split(",")[0]
        code_list = line.strip("\n").replace("\"", "").split(",")[1:]

        result_dict[name] = defaultdict(int)

        for code in main_codes:
            result_dict[name][code] = 0

        for code in code_list:
            mapping = open_to_axial_map[code]
            result_dict[name][mapping] += 1

with open(sys.argv[2], "w", newline='') as output_file:
    datawriter = csv.writer(output_file, delimiter=",")
    datawriter.writerow( ["Participants"] + main_codes )
    for person in result_dict:
        record = [person]
        sorted_keys = sorted(result_dict[person].keys())
        for code in sorted_keys:
            record.append(result_dict[person][code])
            # print(code + ": " + str(result_dict[person][code]))
        datawriter.writerow(record)
