package riquelme.ernesto.myapplicationtabbedactivity.operators;



import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

import riquelme.ernesto.myapplicationtabbedactivity.communication.ConnectionToServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.ListenServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Message;

public class MessagesOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    private Message message;

    private int idMessage;
    private String subject;
    private String text;
    private LocalDate sentDate;
    private LocalTime sentTime;
    private boolean readed;
    private String type;
    private int idTeachersCourse; // Para las solicitudes
    private String senderReceiverName;
    private int idSender;
    private int idReceiver;

    public MessagesOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    // MENSAJES RECIBIDOS -> 0.S9, 1.CantidadMensajesRecibidos
    //
    //
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewInbox(String[] serverArguments){
        int mesagesReceivedQuantity = Integer.parseInt(serverArguments[1]);

        sharedStore.setInboxReceivedList(new LinkedList<>()); // Reinicio la lista

        for (int i = 0; i < mesagesReceivedQuantity; i++) {
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idMessage = Integer.parseInt(finalArguments[0]);
            subject = finalArguments[1];
            text = finalArguments[2];
            sentDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[3]);
            sentTime = sharedStore.getConversions().convertStringToLocalTime(finalArguments[4]);
            readed = Boolean.parseBoolean(finalArguments[5]);
            type = finalArguments[6];
            idTeachersCourse = Integer.parseInt(finalArguments[7]);
            senderReceiverName = finalArguments[8];
            idSender = Integer.parseInt(finalArguments[9]);
            idReceiver = Integer.parseInt(finalArguments[10]);

            message = new Message(idMessage, subject, text, sentDate, sentTime, readed, type, idTeachersCourse, senderReceiverName, idSender, idReceiver);

            sharedStore.getInboxReceivedList().add(message); // Almacenamos los Mensajes Recibidos

        }
        connectionToServer.receiveServerResponse(); // Fin del envio -> S9.0
    }

    // MENSAJES ENVIADOS -> 0.S10, 1.CantidadMensajesEnviados
    //
    //
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewOutbox(String[] serverArguments){
        int mesagesSendedQuantity = Integer.parseInt(serverArguments[1]);

        sharedStore.setInboxSentList(new LinkedList<>()); // Reinicio la lista

        for (int i = 0; i < mesagesSendedQuantity; i++) {
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idMessage = Integer.parseInt(finalArguments[0]);
            subject = finalArguments[1];
            text = finalArguments[2];
            sentDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[3]);
            sentTime = sharedStore.getConversions().convertStringToLocalTime(finalArguments[4]);
            readed = Boolean.parseBoolean(finalArguments[5]);
            type = finalArguments[6];
            idTeachersCourse = Integer.parseInt(finalArguments[7]);
            senderReceiverName = finalArguments[8];
            idSender = Integer.parseInt(finalArguments[9]);
            idReceiver = Integer.parseInt(finalArguments[10]);

            message = new Message(idMessage, subject, text, sentDate, sentTime, readed, type, idTeachersCourse, senderReceiverName, idSender, idReceiver);
            sharedStore.getInboxSentList().add(message); // Almacenamos los Mensajes Recibidos
        }
        connectionToServer.receiveServerResponse(); // Fin del envio -> S10.0
    }
}
