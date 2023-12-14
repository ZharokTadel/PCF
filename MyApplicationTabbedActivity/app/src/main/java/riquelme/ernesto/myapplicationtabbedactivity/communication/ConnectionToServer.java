package riquelme.ernesto.myapplicationtabbedactivity.communication;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

import riquelme.ernesto.myapplicationtabbedactivity.hybrid_encrypt.Decryption;
import riquelme.ernesto.myapplicationtabbedactivity.hybrid_encrypt.Encryption;
import riquelme.ernesto.myapplicationtabbedactivity.objects.User;
import riquelme.ernesto.myapplicationtabbedactivity.operators.CoursesOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.MessagesOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.ResourceFileOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.ResourceHomeworkOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.ResourceLinkOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.ResourceTestOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.UnitsResourcesOperator;
import riquelme.ernesto.myapplicationtabbedactivity.operators.UsersOperator;
import riquelme.ernesto.myapplicationtabbedactivity.tools.Conversions;
import riquelme.ernesto.myapplicationtabbedactivity.tools.ResponseResult;

/**
 * @author william
 */
public class ConnectionToServer extends Thread {

    private SharedStore sharedStore;

    private String serverHost;
    private int serverPort;
    private Socket conexion;
    private DataInputStream input;
    private DataOutputStream output;

    private Encryption encryption;
    private Decryption decryption;
    private ProtocolMessages messages;
    private String account;
    private String passwordToEncrypt;

    private ListenServer listener;

    private Conversions conversions;

    // CONSTRUCTOR
    public ConnectionToServer(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        //this.encryption = new Encryption(); // Encriptación
        //this.decryption = new Decryption();
        this.conversions = new Conversions(); // Conversiones de Datos

        this.sharedStore = SharedStore.getInstance();
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clientMessage() { // COMUNICACIÓN: (C24) Cliente <-#-> Servidor (S73)
        try {
            String message = "";
            do {
                message = sharedStore.getClientMessage(); // <- wait();

                if (message.equals("C0") || message.equals("C3")) { // Si es Cierre de programa o Logout
                    sharedStore.setListening(false); // Preparamos el fin del hilo ListenServer
                }

                sendClientMessage(message); // Envia mensaje encriptado al Servidor

                if (message.equals("C0")) {
                    sharedStore.waitUntilResponse(false); // <- Despierta a App para cerrar el programa
                    disconnect(); // <- No preciso de recibir respuesta del Servidor
                    System.exit(0);
                } else {
                    sharedStore.setAnswered(false);
                    TimerThread timer = new TimerThread(this); // Inicia el hilo Temporizador
                    timer.start();
                    sharedStore.getTimers().add(timer);
                }

                String serverResponse = receiveServerResponse(); // Lee la respuesta del Servidor

                sharedStore.setAnswered(true); // Para evitar que el hilo Temporizador avise de Errores no ocurridos
                processServerResponse(serverResponse); // Procesa la respuesta del Servidor

                sharedStore.waitUntilResponse(false); // Despierta al Hilo Gráfico una vez la información se ha actualizado

            } while (!message.equals("C0"));

        } catch (InterruptedException ex) {
            // Esta Interrupción no se producirá nunca
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void processServerResponse(String response) { // Respuesta del Servidor
        UsersOperator usersOperator = new UsersOperator(this, listener);
        MessagesOperator messagesOperator = new MessagesOperator(this, listener);
        CoursesOperator coursesOperator = new CoursesOperator(this, listener);
        UnitsResourcesOperator unitsResourcesOperator = new UnitsResourcesOperator(this, listener);

        ResourceFileOperator resourceFileOperator = new ResourceFileOperator(this, listener);
        ResourceLinkOperator linksOperator = new ResourceLinkOperator(this, listener);
        ResourceHomeworkOperator resourceHomeworkOperator = new ResourceHomeworkOperator(this, listener);
        ResourceTestOperator resourceTestOperator = new ResourceTestOperator(this, listener);

        String[] serverArguments = response.split("#"); // S2#Pepito Perez#Almadena...


        switch (serverArguments[0]) {

            case "S0":
                disconnect();
                System.exit(0);
                break;
            case "S1": // REGISTRO ACEPTADO -> S1 # idUser
                usersOperator.registrationAccepted(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S1.1": // Error: Usuario existente
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResults("Error: Usuario Existente.");
                break;

            case "S1.2": // Error: Usuario existente
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResults("Error: Error de registro.");
                break;

            case "S2": // LOGIN ACEPTADO -> 0.S2, 1.IdUser, 2.hasPhoto?, 3.Nombre, 4.Email, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga, 9.MensajesNoLeídos
                usersOperator.loginAccepted(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S2.1": // Error: Login incorrecto
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResults("Error: Datos incorrectos.");
                break;

            case "S2.2": // Error: Login... "ocupado"?¿?
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResults("Error: El usuario ya se encuentra logeado.");
                break;

            case "S3": // LOGOUT
                usersOperator.logout(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S4": // PERFIL -> 0.S4, 1.HasPhoto?
                usersOperator.viewProfile(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;
            case "S4.1": // PERFIL -> 0.S4, 1.HasPhoto?
                sharedStore.setResponseResults("Error: Perfil Borrado");
                break;

            case "S5": // UPDATE USER -> 0.S5, 1.sendPhoto?
                usersOperator.updateProfile(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S6": // DELETE USER
                sharedStore.setResponseResults("Ok");

            case "S7": // Enviar Mensaje
                sharedStore.setResponseResults("Ok");
                break;

            case "S7.1":
                sharedStore.setResponseResults("Error: Error al enviar el mensaje.");
                break;

            case "S8": // Enviar Solicitud
                sharedStore.setResponseResults("Ok");
                break;

            case "S8.1": // Error al enviar la Solicitud
                sharedStore.setResponseResults("Error: Solicitud en curso.");
                break;

            case "S8.2": // Error al enviar la Solicitud
                sharedStore.setResponseResults("Error: Curso ya aplicado.");
                break;

            case "S8.3": // Error al enviar la Solicitud
                sharedStore.setResponseResults("Error: Error al enviar la Solicitud.");
                break;

            case "S9": // ENVIAR INVITACIÓN
                sharedStore.setResponseResults("Ok");
                break;
            case "S9.1":
                sharedStore.setResponseResults(serverArguments[1]);
                break;
            case "S9.2":
                sharedStore.setResponseResults(serverArguments[1]);
                break;
            case "S9.3":
                sharedStore.setResponseResults(serverArguments[1]);
                break;
            case "S9.4":
                sharedStore.setResponseResults(serverArguments[1]);
                break;

            case "S10": // MENSAJES RECIBIDOS -> 0.S9, 1.CantidadMensajesRecibidos
                messagesOperator.viewInbox(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S11": // MENSAJES ENVIADOS -> 0.S10, 1.CantidadMensajesEnviados
                messagesOperator.viewOutbox(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S12": // Leer Mensajes
                sharedStore.setResponseResults("Ok");
                break;

                    /*
                case "S11": // Notificación de Mensajes no Leido. Se muestra al cambiar de pantalla de forma automática
                    noReadedMessages = Integer.parseInt(serverArguments[1]);
                    sharedStore.setNoReadMessages(noReadedMessages);
                    break;
                    */
            case "S13": // ACEPTAR SOLICITUD
                sharedStore.setResponseResults("Ok");
                break;

            case "S13.1":
                sharedStore.setResponseResults("Error: Error al aceptar Solicitud.");
                break;

            case "S14": // ACEPTAR INVITACIÓN
                sharedStore.setResponseResults("Ok");
                break;

            case "S14.1":
                sharedStore.setResponseResults(serverArguments[1]);
                break;
            case "S14.2":
                sharedStore.setResponseResults(serverArguments[1]);
                break;
            case "S14.3":
                sharedStore.setResponseResults(serverArguments[1]);
                break;
            case "S14.4":
                sharedStore.setResponseResults(serverArguments[1]);
                break;

            case "S15": // "Borrado" de Mensajes
                sharedStore.setResponseResults("Ok");
                break;

            case "S16": // Alta de Cursos
                sharedStore.setResponseResults("Ok");
                break;

            case "S16.1": // Alta de Cursos: ERROR
                sharedStore.setResponseResults("Error: Error durante el registro.");
                break;

            case "S16.2": // Alta de Cursos: ERROR
                sharedStore.setResponseResults("Error: Nombre Duplicado.");
                break;

            case "S17": // Update Curso
                // Ocurre lo mismo que con las altas. No hay notificación directa, ya que se puede ver al acabar.
                break;


            case "S18": // Lista de Cursos que imparto
                coursesOperator.coursesITeach(serverArguments);
                break;

            case "S19": // Lista de Cursos que recibo
                coursesOperator.coursesIReceive(serverArguments);

            case "S20": // Delete Curso
                sharedStore.setResponseResults("Ok");
                break;

            case "S21": // Motor de Busqueda -> Usuarios
                usersOperator.searchUsers(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S22": // Motor de busqueda -> Cursos
                coursesOperator.searchCourse(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S23": // Apertura de Curso OK
                sharedStore.setResponseResults("Ok");
                break;

            case "S23.1": // Apertura de Curso MAL
                if (sharedStore.getUser().getIdUser() == sharedStore.getSelectedCourse().getIdTeacher()) { // Si es el Profesor
                    sharedStore.setResponseResults("Error: Error inesperado.");
                }
                // Función Automática, no se notifica. Si Ocurre habra un Checkbox seleccionado en Configurar Curso.
                break;

            case "S24": // AULA VIRTUAL -> S24 # Tema? # Recuso? # Tema? # Recurso?...
                coursesOperator.chargeVirtualClass(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S24.1":
                sharedStore.setResponseResults("Error: Error inesperado.");
                break;

            case "S25": // Alta Tema
                sharedStore.setResponseResults("Ok");
                break;

            case "S25.1":
                sharedStore.setResponseResults("Error: Error inesperado.");
                break;

            case "S25.2":
                sharedStore.setResponseResults("Error: Nombre Duplicado.");
                break;

            case "S26": // UPDATE TEMA
                sharedStore.setResponseResults("Ok");
                break;

            case "S26.1":
                sharedStore.setResponseResults("Error: Error inesperado.");
                break;

            case "S26.2":
                sharedStore.setResponseResults("Error: Nombre Duplicado.");
                break;

            case "S27": // Delete Tema
                sharedStore.setResponseResults("Ok");
                break;

            case "S28": // Reordenar Temas
                sharedStore.setResponseResults("Ok");
                break;

            case "S28.1": // Reordenar Temas -> Error
                sharedStore.setResponseResults("Error: Error al Reordenar.");
                break;

            case "S29": // ALTA ENLACE
                sharedStore.setResponseResults("Ok");
                break;

            case "S30": // ALTA ARCHIVO
                resourceFileOperator.uploadFile();
                sharedStore.setResponseResults("Ok");
                break;

            case "S31": // ALTA TAREA
                sharedStore.setResponseResults("Ok");
                break;

            case "S32": // ALTA TEST
                sharedStore.setResponseResults("Ok");
                break;

            case "S33": // Insert pregunta Test -> S47 # idPregunta # idRespuestaTrue # idRespuestaFalse # idRespuesta2False? # idRespuesta3False? # idRespuesta4False?
                resourceTestOperator.insertIds(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S34": // UPDATE ENLACE
                sharedStore.setResponseResults("Ok");
                break;

            case "S35": // UPDATE ARCHIVO
                resourceFileOperator.uploadFile();
                sharedStore.setResponseResults("Ok");
                break;

            case "S36": // UPDATE TAREA
                sharedStore.setResponseResults("Ok");
                break;

            case "S37": // UPDATE TEST -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                resourceTestOperator.saveTestData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S38": // Update pregunta Test
                resourceTestOperator.insertLastIds(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S39": // VER ENLACE -> S41 # url
                linksOperator.saveLinkData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S40": // VER ARCHIVO -> S42 # nombreArchivo
                resourceFileOperator.saveFileData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S41": // DESCARGAR ARCHIVO (Material o Tarea) -> S38 # nombreArchivo
                resourceFileOperator.downloadFile(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S41.1": // DESCARGAR ARCHIVO (Material o Tarea) -> S38 # nombreArchivo
                sharedStore.setResponseResults(serverArguments[1]);
                break;

            case "S42": // Consultar Tarea -> S42 # idHomework #  #  #
                resourceHomeworkOperator.saveHomeworkData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S43":
                resourceFileOperator.uploadHomeworkFiles();
                sharedStore.setResponseResults("Ok");
                break;

            case "S44": // Consultar Test [Profesor] -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                resourceTestOperator.saveTestData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S45": //  Consultar Test [Alumno] -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                resourceTestOperator.saveTestData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S46": // Resolver Test -> S49 # Ok
                sharedStore.setResponseResults("Ok");
                break;

            case "S46.1":
                sharedStore.setResponseResults("Error: Resolución duplicada.");
                break;

            case "S47": // BORRADO RECURSO
                sharedStore.setResponseResults("Ok");
                break;

            case "S48": // REORDENAR RECURSOS
                sharedStore.setResponseResults("Ok");
                break;

            case "S49": // LISTAR ALUMNOS Y TAREAS SUBIDAS
                resourceHomeworkOperator.saveHomeworkData(serverArguments);
                sharedStore.setResponseResults("Ok");
                break;

            case "S50": // ASIGNAR NOTA
                sharedStore.setResponseResults("Ok");
                break;

            case "S51": // ENVIO DE CORREOS EN MASA
                sharedStore.setResponseResults("Ok");
                break;

            case "S52": // LISTAR REGISTROS (MAPA)
                sharedStore.setResponseResults("Ok");
                break;

            case "S53": // FINALIZAR CURSO
                sharedStore.setResponseResults("Ok");
                break;

            default:
                break;
        }
    }


    // ENVIA MENSAJES Y ARCHIVOS
    //
    public void sendClientMessage(String message) throws IOException { // <- VERSION ANDROID
        output.writeUTF(message);
    }


    public void sendArchive(String path) throws IOException {
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        output.writeLong(file.length()); // Envia el tamaño del archivo

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) { // Divide el archivo en trozos
            output.write(buffer, 0, bytes);
            output.flush();
        }
        fileInputStream.close();

        sharedStore.setFilePath("false"); // Para modificaciones: Si no se actualizan los archivos a enviar filePath será "false"
    }

    // RECIBE MENSAJES Y ARCHIVOS
    //
    public String receiveServerResponse() { // <- PARA LA VERSIÓN ANDROID
        String serverResponse = null;
        try {
            serverResponse = input.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverResponse;
    }


    public void receiveArchive(String pathFile) {
        int bytes = 0;
        try (FileOutputStream fileOutputStream = new FileOutputStream(pathFile)) {
            long size = input.readLong(); // Lee tamaño del Archivo

            byte[] encryptedBuffer = new byte[4 * 1024];
            while (size > 0 && (bytes = input.read(encryptedBuffer, 0, (int) Math.min(encryptedBuffer.length, size))) != -1) { // Lee por Trozos
                fileOutputStream.write(encryptedBuffer, 0, bytes);
                size -= bytes;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] receiveArchivePreview() { // Fotografía del Perfil
        int bytes = 0;
        long size = 0; // Lee tamaño del Archivo
        byte[] preview = new byte[0];

        try {
            size = input.readLong();

            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = input.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) { // Lee por Trozos
                preview = conversions.concatByteArrays(preview, buffer);
                size -= bytes;
            }
        } catch (IOException e) {
            sharedStore.setResponseResults("Error: Error en la comunicación.");
        }
        return preview;
    }

    // DESCONEXIÓN
    //
    public void disconnect() {
        try {
            input.close();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            this.conexion = new Socket(serverHost, serverPort);
            this.input = new DataInputStream(conexion.getInputStream());
            this.output = new DataOutputStream(conexion.getOutputStream());
            output.writeUTF("android");
            input.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // hybridEncryptionPreparation(); // Comparte la Clave de Sesión con el Servidor
        clientMessage(); // Inicia el proceso de Comunicación
    }
}
