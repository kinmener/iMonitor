set term post eps
set output 'dp.eps'
set size 0.5, 0.5
set logscale x 2                         # log scale y axes 
unset label                            # remove any previous labels
set xtic font "Times-New-Roman, 16"
set ytic font "Times-New-Roman, 16"
set xlabel "# philosophies" font "Times-New-Roman, 16"
set ylabel "runtime(seconds)" font "Times-New-Roman, 16"
set key left top
set xr [2:128]
plot  "DiningPhilosophers/explicit.dat" using 1:2 title 'explicit' with linespoints, \
      "DiningPhilosophers/naive.dat" using 1:2 title 'naive' with linespoints, \
      "DiningPhilosophers/set.dat" using 1:2 title 'AytoSynch-T' with linespoints, \
      "DiningPhilosophers/tag.dat" using 1:2 title 'AutoSynch' with linespoints