package clientserver;

import BankCzasu.service.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


class ServerHandler implements Runnable
{
    private Socket incoming;
    private Scanner in;
    private List<Service> services;
    private int licznik;

    public ServerHandler(Socket i, List<Service> s,int licznik)
    {
        incoming = i;
        services = s;
        this.licznik = licznik;
    }

    public void run()
    {
        try
        {
            try
            {
                java.io.InputStream inStream = incoming.getInputStream();
                java.io.OutputStream outStream = incoming.getOutputStream();

                in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
                out.println("Czesc, zostales polaczony do baku czasu!\n Wpisz komende");

                boolean done = false;
                while (!done && in.hasNextLine())
                {
                    String line = in.nextLine();

                    String[] words = line.split(" ");

                    if (words != null && words.length > 0) {
                        if (words[0].equals("insert") && words.length > 2) {
                            String name = words[1];
                            int seconds = Integer.parseInt(words[2]);

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, seconds);
                            Date expirationDate = calendar.getTime();

                            Service s = new Service();
                            s.setName(name);
                            s.setExpirationDate(expirationDate);
                            if (services == null) {
                                services = new ArrayList<Service>();
                            }
                            services.add(s);
                            out.println("Dodano usluge " + s.getName());

                        } else if (words[0].equals("print")) {
                            if (services != null) {
                                for (Service s : services) {
                                    Date expirationDate = s.getExpirationDate();
                                    Date now = new Date();
                                    if (now.after(expirationDate)) {
                                        s.setActive(false);
                                        out.println(s.getName() + " --");
                                    }
                                    if (s.isActive()) {
                                        out.println(s.getName() + " " + s.isActive() + " " + (s.getExpirationDate().getSeconds() - (new Date()).getSeconds()));
                                    }
                                }
                            }
                        } else if (words[0].equals("reserve") && words.length > 1) {
                            String name = words[1];
                            Service serviceToReserve = null;
                            if (services != null) {
                                for (Service s : services) {
                                    if (s.getName().equals(name)) {
                                        serviceToReserve = s;
                                        break;
                                    }
                                }
                            } else if (words[0].equals("delete") && words.length > 0) {
                                String nameUslugi = words[1];
                                System.out.println(nameUslugi);
                                for (int i = 0 ; i < services.size() ; i++) {
                                    if (services.get(i).getName().equals(nameUslugi)) {
                                        System.out.println("Wycofano usluge "+services.get(i).getName());
                                        services.remove(i);
                                        break;
                                    }
                                }
                            }

                            if (serviceToReserve == null) {
                                out.println("Nie ma usługi " + name);
                            } else if (!serviceToReserve.isActive()) {
                                out.println("Usluga " + name + " jest nieaktywna");
                            } else if (serviceToReserve.isReserved()) {
                                out.println("Usluga " + name + " jest juz zarezerwowana ");
                            } else {
                                serviceToReserve.setReserved(true);
                                out.println("Zarezerwowano usluge " + name);
                            }
                        }
                    }

                    if(line.trim().equals("quit")) done = true;
                }
            }
            finally
            {
                incoming.close();
                System.out.println("Odlaczony");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
