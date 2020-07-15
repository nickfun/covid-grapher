#!/bin/bash
cd /home/nick/projects/covid-grapher/
HERE=$(pwd)
cd ../covid-19-data
git pull
cd $HERE
cp ../covid-19-data/us-counties.csv $HERE/src/main/resources/us-counties.csv

echo copied!

echo "Begin SBT"
sbt run

./setup.sh "alameda" "Alameda County"
./setup.sh "bay-area" "SF Bay Area"
cp *.png ~/public-static/

echo "Done!"
