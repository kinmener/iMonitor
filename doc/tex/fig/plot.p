#set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic auto                          # set xtics automatically
set ytic auto                          # set ytics automatically
set title "Producer-Consumer Experimental Results"
set xlabel "# producers/consumers" font "Times-New-Roman, 10"
set ylabel "runtime(seconds)" font "Times-New-Roman, 10"
#set key 0.01,100
#set label "Yield Point" at 0.003,260
#set arrow from 0.0028,250 to 0.003,280
set xr [1:128]
#set yr [0.0:15000.0]
plot  "rw.prn" using 2:xticlabels(1) title 'exp' with linespoints, \
      "rw.prn" using 3:xticlabels(1) title 'naive' with linespoints, \
      "rw.prn" using 5:xticlabels(1) title 'map' with linespoints
#plot  "rw.prn" using 1:2 title 'exp' with linespoints, \
#      "rw.prn" using 1:3 title 'naive' with linespoints, \
#      "rw.prn" using 1:5 title 'map' with linespoints
