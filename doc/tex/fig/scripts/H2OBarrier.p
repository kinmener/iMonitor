set term post eps
set output 'h2o.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# H-Atom" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:256]
plot  "H2OBarrier/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "H2OBarrier/naive.dat" using 1:2 title 'baseline' with linespoints, \
      "H2OBarrier/set.dat" using 1:2 title 'AutoSynch-T' with linespoints, \
      "H2OBarrier/tag.dat" using 1:2 title 'AutoSynch' with linespoints
