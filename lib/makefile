.SILENT:

all:	testServer testClient
testServer:	testServer.cpp tcp.o ovesp.o
	echo Creation de testServer
	g++ testServer.cpp tcp.o ovesp.o -o testServer -lpthread
testClient:	testClient.cpp tcp.o
	echo Creation de testClient
	g++ testClient.cpp tcp.o -o testClient
tcp.o:	tcp.cpp tcp.h
	echo Creation de tcp.o
	g++ -c tcp.cpp -o tcp.o
ovesp.o:	ovesp.h ovesp.cpp
	echo Creation de ovesp.o
	g++ -c ovesp.cpp -o ovesp.o
clean:
	rm -f ./*.o
	rm -f testServer
	rm -f testClient