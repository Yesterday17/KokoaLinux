cd libkokoa
rm -rf ./CMakeFiles
rm -rf libkokoa.so
cmake .
make

cd ..
./gradlew build