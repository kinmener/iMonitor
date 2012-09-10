set term post eps
set output 'trw.eps'
set size 0.5, 0.5
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# readers/writers" font "Times-New-Roman, 16"
set ylabel "runtime(milliseconds)" font "Times-New-Roman, 16"
set key right top
#plot  "ReadersWriters/explicit.dat" using 2:xticlabels(1) title 'explicit' with linespoints, \
#      "ReadersWriters/naive.dat" using 2:xticlabels(1) title 'naive' with linespoints, \
#      "ReadersWriters/set.dat" using 2:xticlabels(1) title 'set' with linespoints, \
#      "ReadersWriters/hash.dat" using 2:xticlabels(1) title 'hash' with linespoints, \
#      "ReadersWriters/map.dat" using 2:xticlabels(1) title 'map' with linespoints
plot  "TicketReadersWriters/explicit.dat" using 2:xticlabels(1) title 'explicit' with linespoints, \
      "TicketReadersWriters/iMonitor.dat" using 2:xticlabels(1) title 'SuperSynch' with linespoints
