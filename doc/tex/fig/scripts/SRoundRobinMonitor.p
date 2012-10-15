set term post eps
set output 'rr.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "workload time(microseconds)" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key left top
set xr [0:1000]
plot  "SRoundRobinMonitor/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "SRoundRobinMonitor/set.dat" using 1:2 title 'baseline' with linespoints, \
      "SRoundRobinMonitor/tag.dat" using 1:2 title 'tag' with linespoints
