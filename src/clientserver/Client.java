package clientserver;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable
{

    private static Socket clientSocket = null;
    private static PrintStream os = null;
    private static DataInputStream is = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public Client(){
        //ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfigure.class);
        //ServiceController serviceController = (ServiceController) context.getBean("serviceController");
    }



    public static void main(String [] args)
    {
        int portNumber = 8189;
        String host = "localhost";

        try
        {
            clientSocket = new Socket(host, portNumber);

            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        }
        catch (UnknownHostException e)
        {
            System.err.println("Nieznany host " + host);
        }
        catch (IOException e)
        {
            System.err.println("Blad polaczenia");
        }

        try
        {
            new Thread(new Client()).start();
            while (!closed)
            {
                os.println(inputLine.readLine().trim());
            }

            os.close();
            is.close();
            clientSocket.close();
        }
        catch (IOException e)
        {
            System.err.println("IOException:  " + e);
        }

    }

    @SuppressWarnings("deprecation")
    public void run()
    {
        String responseLine;

        try
        {
            while((responseLine = is.readLine()) != null)
            {
                System.out.println(responseLine);
                if (responseLine.indexOf("ww") != -1)
                    break;
            }
            closed = true;
        }
        catch (IOException e)
        {
            System.err.println("IOException:  " + e);
        }
    }
}
