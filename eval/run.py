#! /opt/local/bin/python3.3
# /home/ecelrc/students/whung1/local/bin/python3
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

    # results directory
    exp_dir = working_dir + "/" + exp_info["result_base_dir"] + "/" \
            +  exp_info["name"] + "_" + strftime("%Y%m%d_%H%M%S", localtime())

    os.makedirs(exp_dir)

    outputs = exp_info["output_order"]
    for output in outputs:
        os.makedirs(exp_dir + "/" + output)
        os.makedirs(exp_dir + "/" + output + "/raw")
        os.makedirs(exp_dir + "/" + output + "/stat")
        os.makedirs(exp_dir + "/" + output + "/fig")

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
                raw_file = []
                stat_file = []
                for j in range(0, len(outputs)):
                    raw_file.append(None)
                    stat_file.append(None)

            file_name = ""

            for j in range(0, len(params_idx)):
                if j != group_by_idx:
                    file_name += str(params_list[j][params_idx[j]]) 
                if j != len(params_idx) - 1:
                    file_name += "_"

            for j in range(0, len(outputs)):

                raw_file[j] = open(exp_dir + "/" + outputs[j] \
                        + "/raw/" + file_name + ".csv", "w")

                stat_param = params_list[stat_by_idx][params_idx[stat_by_idx]]
                stat_file[j] = open(exp_dir + "/" + outputs[j] + "/stat/" \
                        + str(stat_param) + ".dat", "w")

                raw_file[j].write(exp_info["group_by"] + ", ")

                for k in range(0, exp_info["num_times"]):
                    raw_file[j].write(str(k + 1) + ", ")

                raw_file[j].write("max, min, avg. w/o max and min\n")
                raw_file[j].flush()

        # perform experiments num of times
        max = []
        min = []
        sum = []

        for j in range(0, len(outputs)):
            group_param = params_list[group_by_idx][params_idx[group_by_idx]]
            raw_file[j].write(str(group_param) + ", ")
            max.append(-1)
            min.append(sys.maxsize)
            sum.append(0)

        for j in range(0, exp_info["num_times"]):
            exp_thread = Popen(cmd, stdout=PIPE)

            for k in range(0, len(outputs)):
                line = exp_thread.stdout.readline()
                ret = float(line)
                sum[k] += ret 
                if max[k] < ret: 
                    max[k] = ret 
                if min[k] > ret:
                    min[k] = ret 

                for k in range(0, len(outputs)):
                    raw_file[k].write(str(ret) + ", ")

        for j in range(0, len(outputs)):
            if exp_info["num_times"] < 5:
                avg = sum[j]/(exp_info["num_times"])
            else:
                sum[j] -= max[j]
                sum[j] -= min[j]
                avg = sum[j]/(exp_info["num_times"] - 2)

            raw_file[j].write(str(max[j]) + ", " + str(min[j]) + ", " \
                    + str(avg) + "\n")
            raw_file[j].flush()

            stat_file[j].write(\
                    str(params_list[group_by_idx][params_idx[group_by_idx]]) \
                    + " " + str(round(avg/1000, 2)) + "\n")


        # maintain the params idx
        for j in reversed(range(0, len(params_idx))):
            params_idx[map_list_idx[j]] += 1
            if params_idx[map_list_idx[j]] \
                    == len(params_list[map_list_idx[j]]):
                params_idx[map_list_idx[j]] = 0
            else:
                break

    for i in range(0, len(outputs)):
        raw_file[i].close()
        stat_file[i].close()

    # generate fig
    fig_setup = exp_config["fig_setup"]
    os.chdir(fig_setup["fig_dir"])
    for fig in fig_setup["fig_data"]:
        check_call(["rm", "-f", fig["name"]])
        check_call(["ln", "-s", exp_dir + "/" + fig["source"] + "/stat", \
                fig["name"]])
        check_call(["gnuplot", "scripts/" + fig["name"] + ".p"])

