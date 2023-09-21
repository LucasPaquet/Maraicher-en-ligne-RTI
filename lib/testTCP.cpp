#include "tcp.h"
#include <stdio.h>

int main()
{
    int port = 50000;
    int socket;
    socket = ServerSocket(port); // Utilisation de la fonction de la librairie
    Accept(socket, "127.0.0.1");
    return 0;
}
