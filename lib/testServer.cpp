#include "tcp.h"
#include <stdio.h>

int main()
{
    int port = 50000;
    int listenSocket;
    char ipClient[100];
    listenSocket = ServerSocket(port);
    Accept(listenSocket, ipClient);
    return 0;
}
