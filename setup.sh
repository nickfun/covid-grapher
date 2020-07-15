#!/bin/bash
NAME=$1
TITLE=$2
echo "name is $NAME"
echo "title is $TITLE"

#
# csv setup
#
NUM_CASE=$(tail -n 1 $NAME.csv | awk -F, '{print $2}')
NUM_DEAD=$(tail -n 1 $NAME.csv | awk -F, '{print $3}')
COL_DATE=$(tail -n 1 $NAME.csv | awk -F, '{print $1}')

echo cases: $NUM_CASE
echo deaths: $NUM_DEAD
echo column date: $COL_DATE

#
# gnuplot setup
#
cat > $NAME.gnuplot <<-END_OUTPUT
set datafile separator ','
set title "$TITLE COVID-19\n $NUM_CASE cases, $NUM_DEAD deaths\n Data up to $COL_DATE"
set xdata time
set timefmt "%Y-%m-%d"
set format x "%m-%d"
set xtics rotate 
set key autotitle columnhead
set key inside left top

set terminal png size 800,600
set output '$NAME.png'

plot '$NAME.csv' using 1:2 with lines, '' using 1:3 with lines
END_OUTPUT
gnuplot $NAME.gnuplot

D=$(date)
LINE="$NAME , $D"
echo $LINE >> plotting.log
echo $LINE
echo "End processing $NAME"


