set term post eps
set output 'rr.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-Roman, 16"
set ytic font "Times-Roman, 16"
set xlabel "# threads" font "Times-Roman, 16"
set ylabel "runtime(seconds)" font "Times-Roman, 16"
set key left top
set xr [2:256]
plot  "RoundRobinMonitor/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "RoundRobinMonitor/set.dat" using 1:2 title 'AutoSynch-T' with linespoints, \
      "RoundRobinMonitor/tag.dat" using 1:2 title 'AutoSynch' with linespoints
