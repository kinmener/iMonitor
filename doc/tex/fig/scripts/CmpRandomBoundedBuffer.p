set term post eps
set output 'rpch.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# consumers" font "Times-New-Roman, 16"
set ylabel "runtime(milliseconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:128]
plot  "RandomBoundedBuffer/set.dat" using 1:2 title 'iMonitor-T' with linespoints, \
      "RandomBoundedBuffer/tag.dat" using 1:2 title 'iMonitor' with linespoints
