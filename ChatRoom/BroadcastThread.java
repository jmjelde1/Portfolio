import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;

public class BroadcastThread implements Runnable
{
    ArrayList<BufferedWriter> Clients;
    Vector<String> MessageQueue;

    public BroadcastThread(ArrayList<BufferedWriter> clients, Vector<String> messageQueue)
    {
        Clients = clients;
        MessageQueue = messageQueue;
    }

    public void run() {
        while (true) {
            // sleep for 1/10th of a second
            try { Thread.sleep(100); } catch (InterruptedException ignore) { }

            /**
             * check if there are any messages in the Vector. If so, remove them
             * and broadcast the messages to the chatroom
             */
            while(!MessageQueue.isEmpty())
            {

                String message = MessageQueue.get(0);
                System.out.print(message);
                MessageQueue.remove(0);
                
                for (BufferedWriter client : Clients) {
                    try {
                        client.write(message + "\n");
                        client.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Clients.remove(client);
                    }
                }
            }
        }
    }
} 