import os
import csv


input_dir = "../../../data/raw"
output_file = "../processed/rssi_processed.csv"
os.chdir(input_dir)

lines = 250


def getInputFiles():
    inputs = []
    for filename in os.listdir():
        inputs.append(open(filename, "r", encoding="utf-8", newline=''))
    return inputs


def writeHeaders(datawriter, headers):
    datawriter.writerow(headers)


def closeInputFiles(inputs):
    for file in inputs:
        file.close()


with open(output_file, "w", encoding="utf-8", newline='') as output:
    files = getInputFiles()
    datawriter = csv.writer(output, delimiter=",")
    writeHeaders(datawriter, ["Scan No", "0.5m",
                              "1.0m", "1.5m", "2.0m", "2.5m"])
    for i in range(lines):
        record = [i]
        for file in files:
            val = int(file.readline().strip().split(',')[1].strip("\"\""))
            if val == 99:
                val = None
            record.append(val)
        datawriter.writerow(record)
    closeInputFiles(files)


# all_filenames = [i for i in glob.glob('*.{}'.format('csv'))]
# combined_csv = pd.concat([pd.read_csv(f) for f in all_filenames])

# os.chdir("../processed")
# combined_csv.to_csv("combined_csv.csv", index=False, encoding='utf-8-sig')
