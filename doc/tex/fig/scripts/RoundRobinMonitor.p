set term post eps
set output 'rr.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# threads" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:128]
#      "RoundRobinMonitor/naive.dat" using 1:2 title 'naive' with linespoints, \
plot  "RoundRobinMonitor/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "RoundRobinMonitor/map.dat" using 1:2 title 'map' with linespoints, \
      "RoundRobinMonitor/iMonitor.dat" using 1:2 title 'new' with linespoints
