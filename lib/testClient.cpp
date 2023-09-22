#include "tcp.h"
#include <stdio.h>

int main()
{
    int port = 50000;
    int listenSocket;
    listenSocket = ClientSocket("127.0.0.1", 50000);
    return 0;
}
