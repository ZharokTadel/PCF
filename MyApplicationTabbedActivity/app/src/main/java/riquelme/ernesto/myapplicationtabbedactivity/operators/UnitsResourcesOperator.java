package riquelme.ernesto.myapplicationtabbedactivity.operators;



import riquelme.ernesto.myapplicationtabbedactivity.communication.ConnectionToServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.ListenServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Resource;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Unit;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class UnitsResourcesOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    private Unit unit;

    private int idUnit;
    private String titleUnit;
    private int orderUnit;
    private boolean hiddenUnit;
    private int percentageUnit;
    private int idCourse;

    private Resource resource;

    private int idResource;
    private String titleResource;
    private String presentationResource;
    private String typeResource;
    private int orderResource;
    private boolean hiddenResource;
    //private int idUnit;

    public UnitsResourcesOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

}
