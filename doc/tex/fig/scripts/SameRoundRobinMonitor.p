set term post eps
set output 'srr.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# threads" font "Times-New-Roman, 16"
set ylabel "runtime(milliseconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:256]
plot "SameRoundRobinMonitor/project.dat" using 1:2 title 'sim_eval' with linespoints, \
     "SameRoundRobinMonitor/tag.dat" using 1:2 title 'autosynch' with linespoints
