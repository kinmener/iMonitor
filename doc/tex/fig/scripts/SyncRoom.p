set term post eps
set output 'sr.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# process" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:256]
plot  "SyncRoom/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "SyncRoom/set.dat" using 1:2 title 'AutoSynch-T' with linespoints, \
      "SyncRoom/tag.dat" using 1:2 title 'AutoSynch' with linespoints