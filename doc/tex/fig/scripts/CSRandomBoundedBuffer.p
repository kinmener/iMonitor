set term post eps
set output 'csrpc.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# consumers" font "Times-New-Roman, 16"
set ylabel "# context switches" font "Times-New-Roman, 16"
set key left top
set xr [2:128]
plot  "CSRandomBoundedBuffer/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "CSRandomBoundedBuffer/naive.dat" using 1:2 title 'baseline' with linespoints, \
      "CSRandomBoundedBuffer/set.dat" using 1:2 title 'iMonitor-T' with linespoints, \
      "CSRandomBoundedBuffer/tag.dat" using 1:2 title 'iMonitor' with linespoints