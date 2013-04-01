set term post eps
set output 'srr.eps'
set size 0.5, 0.5
# set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-Roman, 16"
set ytic font "Times-Roman, 16"
set xlabel "delay time(microseconds)" font "Times-Roman, 16"
set ylabel "runtime ratio" font "Times-Roman, 16"
set key left top
#set xr [0:10000]
plot  "rrexplicit.dat" using 1:($3/$2) title 'AutoSynch' with linespoints, \
      "rrexplicit.dat" using 1:($4/$2) title 'AutoSynch-T' with linespoints 
