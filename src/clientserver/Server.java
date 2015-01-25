package clientserver;
import BankCzasu.service.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    private static ServerSocket s = null;
    private static Runnable r = null;
    private static Thread t = null;

    private static List<Service> services = new ArrayList<Service>();


    public static void main(String [] args){
        Socket incoming = null;
        int port = 8189;
        int licznik = 0;

        try
        {
            int i = 1;
            s = new ServerSocket(port);

            while(true)
            {
                System.out.println("xxx");
                incoming = s.accept(); // oczekuje az klient przylaczy sie do portu
                licznik++;
                System.out.println("Klient " + i);
                r = new ServerHandler(incoming, services, i);
                t = new Thread(r); // utworzenie watku
                t.start(); // uruchomienie watku
                i++; // zliczanie klientow
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
