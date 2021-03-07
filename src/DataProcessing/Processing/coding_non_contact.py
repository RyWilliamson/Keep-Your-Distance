import os
import csv
import sys
from collections import defaultdict

def def_value():
    return "Other"

open_to_axial_map = defaultdict(def_value)

open_to_axial_map["O1"] = "A5"
open_to_axial_map["O2"] = "A6"
open_to_axial_map["O3"] = "A1"
open_to_axial_map["O4"] = "A2"
open_to_axial_map["O5"] = "A2"
open_to_axial_map["O6"] = "A1"
open_to_axial_map["O7"] = "A2"
open_to_axial_map["O8"] = "A2"
open_to_axial_map["O9"] = "A1"
open_to_axial_map["O10"] = "A2"
open_to_axial_map["O11"] = "A2"
open_to_axial_map["O12"] = "A10"
open_to_axial_map["O13"] = "A6"
open_to_axial_map["O14"] = "A10"
open_to_axial_map["O15"] = "A1"
open_to_axial_map["O16"] = "A2"
open_to_axial_map["O17"] = "A10"
open_to_axial_map["O18"] = "A1"
open_to_axial_map["O19"] = "A2"
open_to_axial_map["O20"] = "A1"
open_to_axial_map["O21"] = "A2"
open_to_axial_map["O22"] = "A1"
open_to_axial_map["O23"] = "A2"
open_to_axial_map["O24"] = "A9"
open_to_axial_map["O25"] = "A9"
open_to_axial_map["O26"] = "A1"
open_to_axial_map["O27"] = "A2"
open_to_axial_map["O28"] = "A1"
open_to_axial_map["O29"] = "A2"
open_to_axial_map["O30"] = "A1"
open_to_axial_map["O31"] = "A2"
open_to_axial_map["O32"] = "A3"
open_to_axial_map["O33"] = "A4"
open_to_axial_map["O34"] = "A3"
open_to_axial_map["O35"] = "A4"
open_to_axial_map["O36"] = "A3"
open_to_axial_map["O37"] = "A4"
open_to_axial_map["O38"] = "A3"
open_to_axial_map["O39"] = "A4"
open_to_axial_map["O40"] = "A4"
open_to_axial_map["O41"] = "A3"
open_to_axial_map["O42"] = "A4"
open_to_axial_map["O43"] = "A7"
open_to_axial_map["O44"] = "A9"
open_to_axial_map["O45"] = "A9"
open_to_axial_map["O46"] = "A7"
open_to_axial_map["O47"] = "A7"
open_to_axial_map["O48"] = "A9"
open_to_axial_map["O49"] = "A7"
open_to_axial_map["O50"] = "A7"
open_to_axial_map["O51"] = "A7"
open_to_axial_map["O52"] = "A4"
open_to_axial_map["O53"] = "A3"
open_to_axial_map["O54"] = "A4"
open_to_axial_map["O55"] = "A4"
open_to_axial_map["O56"] = "A8"
open_to_axial_map["O57"] = "A10"
open_to_axial_map["O58"] = "A7"
open_to_axial_map["O59"] = "A10"
open_to_axial_map["O60"] = "A10"
open_to_axial_map["O61"] = "A8"
open_to_axial_map["O62"] = "A7"
open_to_axial_map["O63"] = "A7"
open_to_axial_map["O64"] = "A9"
open_to_axial_map["O65"] = "A7"
open_to_axial_map["O66"] = "A2"
open_to_axial_map["O67"] = "A8"
open_to_axial_map["O68"] = "A7"
open_to_axial_map["O69"] = "A7"
open_to_axial_map["O70"] = "A9"
open_to_axial_map["O71"] = "A9"
open_to_axial_map["O72"] = "A8"
open_to_axial_map["O73"] = "A3"
open_to_axial_map["O74"] = "A3"
open_to_axial_map["O75"] = "A10"
open_to_axial_map["O76"] = "A9"


result_dict = {}
main_codes = ["A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "Other"]

def sorting_func(element):
    if element == "Other":
        return len(main_codes)
    return int(element[1:])

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
        sorted_keys = sorted(result_dict[person].keys(), key=sorting_func)
        for code in sorted_keys:
            record.append(result_dict[person][code])
            # print(code + ": " + str(result_dict[person][code]))
        datawriter.writerow(record)
