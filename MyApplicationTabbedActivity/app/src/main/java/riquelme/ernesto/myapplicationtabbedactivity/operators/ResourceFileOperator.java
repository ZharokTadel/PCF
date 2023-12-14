package riquelme.ernesto.myapplicationtabbedactivity.operators;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import riquelme.ernesto.myapplicationtabbedactivity.communication.ConnectionToServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.ListenServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.ArchiveHomework;
import riquelme.ernesto.myapplicationtabbedactivity.objects.ResourceFile;
import riquelme.ernesto.myapplicationtabbedactivity.tools.ResponseResult;

public class ResourceFileOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    public ResourceFileOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    public void saveFileData(String[] serverArguments) {
        ResourceFile resourceFile = new ResourceFile(sharedStore.getSelectedResource(), serverArguments[1]);
        sharedStore.setSelectedResource(resourceFile);
    }

    public void uploadFile() { // [Usuario -> Foto] && [Profesor -> Archivo]
        try {
            if (!sharedStore.getFilePath().equals("false")) {
                connectionToServer.sendArchive(sharedStore.getFilePath());
                finalArguments = connectionToServer.receiveServerResponse().split("#"); // Fin del Envío
            }
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) { // Archivo no encontrado
                sharedStore.setResponseResults("Error: Archivo no encontrado.");
            } else {
                sharedStore.setResponseResults("Error: Error en la comunicación.");
            }
        }
    }

    public void uploadHomeworkFiles() { // [Usuario -> Foto] && [Profesor -> Archivo]
        try {
            for(ArchiveHomework archiveHomework: sharedStore.getFilesHomework()){
                if(archiveHomework.getId() == -1) { // Se suben los nuevos, no los antiguos
                    connectionToServer.sendArchive(archiveHomework.getPath());
                }
            }

            finalArguments = connectionToServer.receiveServerResponse().split("#"); // Fin del Envío
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) { // Archivo no encontrado
                sharedStore.setResponseResults("Error: Archivo no encontrado.");
            } else {
                sharedStore.setResponseResults("Error: Error en la comunicación."   );
            }
        }
    }

    public void downloadFile(String[] serverArguments) { // Rol Alumno (y Profesor)
        String downloadsFile = System.getProperty("user.home") + File.separator + "Descargas" + File.separator; // "Pepito/Descargas/"

        connectionToServer.receiveArchive(downloadsFile + serverArguments[1]); // "Pepito/Descargas/nombreArchivo.pdf"

        //ResourceFile resourceFile = new ResourceFile(sharedStore.getSelectedResource(), serverArguments[1]);
        //sharedStore.setSelectedResource(resourceFile);

        finalArguments = connectionToServer.receiveServerResponse().split("#"); // Fin del Envío
    }
}
