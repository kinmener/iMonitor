set term post eps
set output 'strw.eps'
set size 0.5, 0.5
unset label                            # remove any previous labels
set xtic font "Times-Roman, 16"
set ytic font "Times-Roman, 16"
set xlabel "delay time(microseconds)" font "Times-Roman, 16"
set ylabel "runtime ratio" font "Times-Roman, 16"
set key left top
plot  "explicit.dat" using 1:($3/$2) title 'AutoSynch' with linespoints, \
      "explicit.dat" using 1:($4/$2) title 'AutoSynch-T' with linespoints 
