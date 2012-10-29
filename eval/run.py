#! /home/ecelrc/students/whung1/local/bin/python3
# /opt/local/bin/python3.3
from subprocess import check_call, Popen, PIPE
from time import sleep, localtime, strftime
from optparse import OptionParser
from datetime import date

import sys, json, os

if __name__ == "__main__":

    parser = OptionParser()
    (options, args) = parser.parse_args()

    with open(args[0]) as config_file:
        exp_config = json.load(config_file)

    exp_info = exp_config["exp_info"]

    working_dir = os.getcwd()
    # compile 
    os.chdir("..")
    check_call(["ant", "compile_test"])
    os.chdir(working_dir)

    # setup parameters
    exp_params = exp_config["exp_params"]

    total_num_comb = 1 

    params_list = []
    params_idx = []
    map_list_idx = {}


    for param in exp_info["params_order"]:
        map_list_idx[len(params_idx)] = len(params_idx)
        if param == exp_info["group_by"]:
            group_by_idx = len(params_idx)
        elif param == exp_info["stat_by"]:
            stat_by_idx = len(params_idx)

        params_list.append(exp_params[param])
        params_idx.append(0)
        total_num_comb *= len(exp_params[param])

    if(group_by_idx != len(params_idx) - 1):
        map_list_idx[group_by_idx] = len(params_idx) - 1
        map_list_idx[len(params_idx) - 1] = group_by_idx

    if not os.path.exists(exp_info["result_base_dir"]):
        os.makedirs(exp_info["result_base_dir"])

    exp_dir = working_dir + "/" + exp_info["result_base_dir"] + "/" \
            +  exp_info["name"] + "_" + strftime("%Y%m%d_%H%M%S", localtime())
    os.makedirs(exp_dir)
    os.makedirs(exp_dir + "/raw")
    os.makedirs(exp_dir + "/stat")
    os.makedirs(exp_dir + "/fig")

    os.chdir(exp_info["main_dir"])
    for i in range(0, total_num_comb):
        print(str(params_idx))

        cmd = ["java", "-cp", "../dist/lib/iMonitor-" \
                + str(date.today()).replace("-", "") + ".jar:."] 
        cmd.append(exp_info["main_class"])
        for j in range(0, len(params_idx)):
            cmd.append(str(params_list[j][params_idx[j]]))

        
        print(' '.join(cmd))

        # open file to write
        if params_idx[group_by_idx] == 0:
            try: 
                raw_file.close()
                stat_file.close()
            except: 
                raw_file = None
                stat_file = None

            file_name = ""

            for j in range(0, len(params_idx)):
                if j != group_by_idx:
                    file_name += str(params_list[j][params_idx[j]]) 
                if j != len(params_idx) - 1:
                    file_name += "_"

            raw_file = open(exp_dir + "/raw/" + file_name + ".csv", "w")
            stat_file = open(exp_dir + "/stat/" \
                    + str(params_list[stat_by_idx][params_idx[stat_by_idx]]) \
                    + ".dat", "w")
            raw_file.write(exp_info["group_by"] + ", ")
            for i in range(0, exp_info["num_times"]):
                raw_file.write(str(i + 1) + ", ")

            raw_file.write("max, min, avg. w/o max and min\n")
            raw_file.flush()

        # perform experiments num of times
        max = -1
        min = sys.maxsize
        sum = 0
        raw_file.write(\
                str(params_list[group_by_idx][params_idx[group_by_idx]]) \
                + ", ")
        for j in range(0, exp_info["num_times"]):
            exp_thread = Popen(cmd, stdout=PIPE)

            for line in exp_thread.stdout:
                run_time = float(line)
                sum += run_time
                if max < run_time: 
                    max = run_time
                if min > run_time:
                    min = run_time

                raw_file.write(str(run_time) + ", ")

        sum -= max
        sum -= min
        avg = sum/(exp_info["num_times"] - 2)
        raw_file.write(str(max) + ", " + str(min) + ", " + str(avg) + "\n")
        raw_file.flush()

        stat_file.write(\
                str(params_list[group_by_idx][params_idx[group_by_idx]]) \
                + " " + str(round(avg/2, 1)) + "\n")


        # maintain the params idx
        for j in reversed(range(0, len(params_idx))):
            params_idx[map_list_idx[j]] += 1
            if params_idx[map_list_idx[j]] \
                    == len(params_list[map_list_idx[j]]):
                params_idx[map_list_idx[j]] = 0
            else:
                break

    raw_file.close()
    stat_file.close()

    # generate fig
    os.chdir(exp_info["fig_dir"])
    check_call(["rm", "-f", exp_info["name"]])
    check_call(["ln", "-s", exp_dir + "/stat", exp_info["name"]])
    check_call(["gnuplot", "scripts/" + exp_info["name"] + ".p"])

