#! /home/ecelrc/students/whung1/local/bin/python3.2
from subprocess import check_call, Popen, PIPE
from time import sleep
from optparse import OptionParser
import sys, json, os


if __name__ == "__main__":

   parser = OptionParser()
   (options, args) = parser.parse_args()
   with open(args[0]) as config_file:
      exp_env = json.load(config_file)

   for type in exp_env["monitor_type"]:
      result_file = open("exp_results_" + type + ".csv", "w+")
      result_file.write("num_proc, ");
      for i in range(0, exp_env["num_times"]):
         result_file.write(str(i) + ", ")
      result_file.write("max, min, avg. w/o max min\n")

      for num_proc in exp_env["num_proc"]:
         max = -1
         min = sys.maxsize
         sum = 0
         result_file.write(str(num_proc) + ", ")
         for i in range(0, exp_env["num_times"]):
            exp_thread = Popen(["java", "-cp", "../../dist/lib/iMonitor-20120305.jar:.", 
               "Test", str(num_proc), str(int(exp_env["num_tasks"]/num_proc)), type[:1]],
               stdout=PIPE)

            for line in exp_thread.stdout:
               run_time = int(line)
               sum += run_time
               if max < run_time: 
                  max = run_time
               if min > run_time:
                  min = run_time

               result_file.write(str(run_time) + ", ")
         
         sum -= max
         sum -= min
         result_file.write(str(max) + ", " + str(min) + ", " + str(sum/(exp_env["num_times"] - 2)) + "\n")
         result_file.flush()

      result_file.close()
