set term post eps
set output 'hrr.eps'
set boxwidth 0.75 absolute
#set style fill solid 1.00 border -1
set style histogram rowstacked
set style data histograms
set xtics 1000 nomirror
set ytics 100 nomirror
set mxtics 2
set mytics 2
set ytics 10
#set yrange [0:50]
set ylabel "Total time"
set xlabel "Session number"

plot "rr_cpu.dat" using 2 t "await", '' using 3 t "lock", '' using 4 t \
    "signalOne", '' using 5 t "tag management", '' using 6:xtic(1) t "others" 


pause -1 "Hit any key to continue"
