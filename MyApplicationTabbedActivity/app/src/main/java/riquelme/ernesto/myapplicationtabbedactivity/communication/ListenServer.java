package riquelme.ernesto.myapplicationtabbedactivity.communication;


import java.io.IOException;
import java.net.*;

/**
 *
 * @author william
 */
public class ListenServer extends Thread {

    private String serverHost;
    private int serverPort;
    private SharedStore sharedStore;

    private DatagramSocket udpClientSocket;

    // CONSTRUCTOR
    public ListenServer(SharedStore sharedStore, String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.sharedStore = sharedStore;
    }

    // OPERATIONS
    public void initialize() { // Para que el Servidor coja la IP y el Puerto del Cliente
        byte[] buffer = new byte[1024];

        try {
            InetAddress serverHostAddress = InetAddress.getByName(serverHost);
            this.udpClientSocket = new DatagramSocket(); // "Socket" del Cliente
            String noMatter = "Este mensaje no es importante";

            buffer = noMatter.getBytes();
            DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length, serverHostAddress, 4556);

            System.out.println("Enviando Datagrampacket al Servidor");

            udpClientSocket.send(dataPacket);

            System.out.println("DatagramPacket enviado");

        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void listen() {
        byte[] buffer = new byte[1024];

        try {
            while (sharedStore.isListening()) {
                DatagramPacket udpServerNotification = new DatagramPacket(buffer, buffer.length);
                System.out.println("Esperando notificaciones...");
                udpClientSocket.receive(udpServerNotification);
                System.out.println("Notificación UDP recibida");

                String serverNotification = new String(udpServerNotification.getData());

                System.out.println(serverNotification);

                if (serverNotification.contains("A")) {
                    System.out.println("Notificación llega Puta Madre");
                    sharedStore.noReadMessagesPlusOne(); // Mensajes no Leidos +1
                }
                System.out.println("Mensajes sin leer: " + sharedStore.getNoReadMessages());
            }

            udpClientSocket.close(); // Cierro al Acabar

        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Hilo ListenServer Iniciado.");
        //initialize();
        //listen();
        System.out.println("Hilo ListenServer Finalizado.");
    }
}
