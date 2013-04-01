set term post eps
set output 'trw.eps'
set size 0.5, 0.5
unset label                            # remove any previous labels
set xtic font "Times-Roman, 16"
set ytic font "Times-Roman, 16"
set xlabel "# writers" font "Times-Roman, 16"
set ylabel "Runtime (seconds)" font "Times-Roman, 16"
set key left top
plot  "TicketReadersWriters/explicit.dat" using 2:xticlabels(1) title 'explicit' with linespoints, \
      "TicketReadersWriters/set.dat" using 2:xticlabels(1) title 'AutoSynch-T' with linespoints, \
      "TicketReadersWriters/tag.dat" using 2:xticlabels(1) title 'AutoSynch' with linespoints
