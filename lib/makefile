.SILENT:

all:	testServer testClient
testServer:	testServer.cpp tcp.o
	echo Creation de testServer
	g++ testServer.cpp tcp.o -o testServer
testClient:	testClient.cpp tcp.o
	echo Creation de testClient
	g++ testClient.cpp tcp.o -o testClient
tcp.o:	tcp.cpp tcp.h
	echo Creation de tcp.o
	g++ -c tcp.cpp -o tcp.o
clean:
	rm -f ./*.o
	rm -f testServer
	rm -f testClient