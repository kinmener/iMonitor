set term post eps
set output 'trw.eps'
set size 0.5, 0.5
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# readers/writers" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key right top
#plot  "ReadersWriters/explicit.dat" using 2:xticlabels(1) title 'explicit' with linespoints, \
#      "ReadersWriters/naive.dat" using 2:xticlabels(1) title 'naive' with linespoints, \
#      "ReadersWriters/set.dat" using 2:xticlabels(1) title 'set' with linespoints, \
#      "ReadersWriters/hash.dat" using 2:xticlabels(1) title 'hash' with linespoints, \
#      "ReadersWriters/map.dat" using 2:xticlabels(1) title 'map' with linespoints
#      "TicketReadersWriters/naive.dat" using 1:2 title 'naive' with linespoints, \
plot  "TicketReadersWriters/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "TicketReadersWriters/map.dat" using 1:2 title 'map' with linespoints, \
      "TicketReadersWriters/iMonitor.dat" using 1:2 title 'new' with linespoints
