set term post eps
set output 'rw.eps'
set size 0.5, 0.5
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# readers/writers" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key right top
plot  "rw.prn" using 2:xticlabels(1) title 'explicit' with linespoints, \
      "rw.prn" using 3:xticlabels(1) title 'naive' with linespoints, \
      "rw.prn" using 4:xticlabels(1) title 'n-condition' with linespoints, \
      "rw.prn" using 5:xticlabels(1) title 'map' with linespoints
