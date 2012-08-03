set term post eps
set output 'pc.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# producers/consumers" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:128]
plot  "pc.prn" using 1:2 title 'explicit' with linespoints, \
      "pc.prn" using 1:3 title 'naive' with linespoints, \
      "pc.prn" using 1:4 title 'n-condition' with linespoints, \
      "pc.prn" using 1:5 title 'map' with linespoints
