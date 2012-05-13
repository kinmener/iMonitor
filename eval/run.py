#! /opt/local/bin/python3.3
# /nfs/site/disks/an_umg_disk2409/whung7/local/bin/python3.2
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

      params_list.append(exp_params[param])
      params_idx.append(0)
      total_num_comb *= len(exp_params[param])

   if(group_by_idx != len(params_idx) - 1):
      map_list_idx[group_by_idx] = len(params_idx) - 1
      map_list_idx[len(params_idx) - 1] = group_by_idx
  
   exp_dir = exp_info["name"] + "_" + strftime("%Y%m%d_%H%M%S", localtime())
   if not os.path.exists(exp_info["result_base_dir"]):
      os.makedirs(exp_info["result_base_dir"])

   os.makedirs(exp_info["result_base_dir"] + "/" + exp_dir)
   os.chdir(exp_info["main_dir"])
   for i in range(0, total_num_comb):
      print(str(params_idx))

      cmd = ["java", "-cp", "../dist/lib/iMonitor-" + str(date.today()).replace("-", "") + ".jar:."] 
      cmd.append(exp_info["main_class"])
      for j in range(0, len(params_idx)):
         cmd.append(str(params_list[j][params_idx[j]]))

      print(str(cmd))

      # open file to write
      if params_idx[group_by_idx] == 0:
         try:
            for j in range(0, 3):
               result_file[i].close()
         except: 
            result_file = [] 
            for j in range(0, 3):
               result_file.append(None)
         
         file_name = working_dir + "/" + exp_info["result_base_dir"] + "/" + exp_dir + "/exp_results_"
         for j in range(0, len(params_idx)):
            if j != group_by_idx:
               file_name += str(params_list[j][params_idx[j]]) 
            if j != len(params_idx) - 1:
               file_name += "_"

         result_file[0] = open(file_name + "_wall_time.csv", "w")
         result_file[1] = open(file_name + "_cpu_time.csv", "w")
         result_file[2] = open(file_name + "_sync_time.csv", "w")

         for j in range(0, 3):
            result_file[j].write(exp_info["group_by"] + ", ")
            for k in range(0, exp_info["num_times"]):
               result_file[j].write(str(k + 1) + ", ")

            result_file[j].write("max, min, avg. w/o max and min\n")
            result_file[j].flush()

      # perform experiments num of times
      max = []
      min = []
      sum = []

      for j in range(0, 3):
         result_file[j].write(str(params_list[group_by_idx][params_idx[group_by_idx]]) + ", ")
         max.append(-1)
         min.append(sys.maxsize)
         sum.append(0)


      for j in range(0, exp_info["num_times"]):
         exp_thread = Popen(cmd, stdout=PIPE)

         for k in range(0, 3):
            line = exp_thread.stdout.readline()
            run_time = round(float(line))
            sum[k] += run_time
            if max[k] < run_time: 
               max[k] = run_time
            if min[k] > run_time:
               min[k] = run_time

            result_file[k].write(str(run_time) + ", ")
         
      for k in range(0, 3):
         sum[k] -= max[k]
         sum[k] -= min[k]
         result_file[k].write(str(max[k]) + ", " + str(min[k]) + ", " + str(sum[k]/(exp_info["num_times"] - 2)) + "\n")
         result_file[k].flush()

     
      # maintain the params idx
      for j in reversed(range(0, len(params_idx))):
         params_idx[map_list_idx[j]] += 1
         if params_idx[map_list_idx[j]] == len(params_list[map_list_idx[j]]):
            params_idx[map_list_idx[j]] = 0
         else:
            break

