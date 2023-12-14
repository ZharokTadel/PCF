package riquelme.ernesto.myapplicationtabbedactivity.operators;


import riquelme.ernesto.myapplicationtabbedactivity.communication.ConnectionToServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.ListenServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.ResourceLink;

public class ResourceLinkOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    public ResourceLinkOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    public void saveLinkData(String[] serverArguments){
        ResourceLink resourceLink = new ResourceLink(sharedStore.getSelectedResource(), serverArguments[1]);
        sharedStore.setSelectedResource(resourceLink);
    }
}
