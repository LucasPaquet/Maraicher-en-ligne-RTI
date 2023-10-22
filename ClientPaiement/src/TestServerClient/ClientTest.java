package TestServerClient;


import VESPAP.ClientVESPAP;

public class ClientTest {
    public static void main(String[] args) {
        ClientVESPAP cl = new ClientVESPAP("127.0.0.1", 50000);
    }
}
