.SILENT:

testTCP:	testTCP.cpp tcp.o
	echo Creation de testTCP
	g++ testTCP.cpp tcp.o -o testTCP
tcp.o:	tcp.cpp tcp.h
	echo Creation de tcp.o
	g++ -c tcp.cpp -o tcp.o
clean:
	rm -f ./*.o
	rm -f testTCP