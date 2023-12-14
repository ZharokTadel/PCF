package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.ConnectionToServer;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Course;
import riquelme.ernesto.myapplicationtabbedactivity.objects.User;
import riquelme.ernesto.myapplicationtabbedactivity.recyclers.CourseAdapter;
import riquelme.ernesto.myapplicationtabbedactivity.recyclers.UsersAdapter;

public class MainActivity extends AppCompatActivity {
    ConnectionToServer connectionToServer;
    private String SERVER_HOST = "192.168.1.137";
    private final int SERVER_PORT = 4445;

    SharedStore sharedStore;

    LinkedList<Course> coursesList;
    LinkedList<User> usersList;

    RadioButton coursesRadioButton;
    RadioButton usersRadioButton;

    Button search_button;
    Button inboxButton;
    Button courses_button;
    Button login_perfil_button;
    Button selectButton;

    TextView nameTextView;
    TextView tagsTextView;
    TextView provinceTextView;
    TextView townshipTextView;

    EditText nameEditText;
    EditText tagsEditText;
    EditText provinceEditText;
    EditText townshipEditText;

    public MainActivity() {
        this.sharedStore = SharedStore.getInstance();
    }

    public void switchToInbox() {
        if (sharedStore.getUser().isLogged()) {
            startActivity(new Intent(MainActivity.this, InboxActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Debes logearte para acceder a tus mensajes.", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchToCoursesList() {
        if (sharedStore.getUser().isLogged()) {
            startActivity(new Intent(MainActivity.this, CoursesListActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Debes logearte para acceder a tus cursos.", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchToLoginPerfil() {
        if (sharedStore.getUser().isLogged()) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4)
                    + "#" + sharedStore.getUser().getIdUser(); // "C4"#IdUser
            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            startActivity(new Intent(MainActivity.this, PerfilActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendClientMessage() {

        nameTextView = (TextView) findViewById(R.id.titleCourseTextView);
        tagsTextView = (TextView) findViewById(R.id.tagsTextView);
        provinceTextView = (TextView) findViewById(R.id.openDateTextView);
        townshipTextView = (TextView) findViewById(R.id.closeDateTextView);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        tagsEditText = (EditText) findViewById(R.id.tagsEditText);
        provinceEditText = (EditText) findViewById(R.id.provinceEditText);
        townshipEditText = (EditText) findViewById(R.id.townshipEditText);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coursesRadioButton = (RadioButton) findViewById(R.id.coursesRadioButton);

                name = String.valueOf(nameEditText.getText());
                tags = String.valueOf(tagsEditText.getText());
                province = String.valueOf(provinceEditText.getText());
                township = String.valueOf(townshipEditText.getText());


                reColor(); // Por si había algún error

                if (checkErrors()) {

                    String searchName;
                    if (name.equals("")) {
                        searchName = "none";
                    } else {
                        searchName = name;
                    }
                    String searchTags;
                    if (tags.equals("")) {
                        searchTags = "none";
                    } else {
                        searchTags = tags;
                        searchTags = searchTags.toLowerCase(); // MySql no es case sensitive, peeero... por si acaso, que de sql es mejor no fiarse
                        searchTags = searchTags.replaceAll(", ", "#"); // "idiomas, tecnologías" -> "idiomas#tecnologías"
                        searchTags = searchTags.replaceAll(",", "#"); // "idiomas,tecnologías" -> "idiomas#tecnologías"
                        searchTags = searchTags.replace(" ", "#"); // "idiomas tecnologías" -> "idiomas#tecnologías"
                        searchTags = searchTags.replace("##", "#"); // Por si acaso
                    }
                    String searchProvince;
                    if (province.equals("")) {
                        searchProvince = "none";
                    } else {
                        searchProvince = province;
                    }
                    String searchTownship;
                    if (township.equals("")) {
                        searchTownship = "none";
                    } else {
                        searchTownship = township;
                    }

                    LocalDate localDate = LocalDate.now();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String currentDate = localDate.format(dateFormatter);

                    String clientMessage;
                    if (coursesRadioButton.isChecked()) {
                        clientMessage = sharedStore.getProtocolMessages().getClientArgument(22) // C22
                                + "#" + currentDate
                                + "#" + searchName
                                + "#" + searchProvince
                                + "#" + searchTownship
                                + "#" + searchTags;
                    } else {
                        clientMessage = sharedStore.getProtocolMessages().getClientArgument(21) // C21
                                + "#" + searchName
                                + "#" + searchProvince
                                + "#" + searchTownship
                                + "#" + searchTags;
                    }

                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()

                    if (sharedStore.getResponseResults().equals("Ok")) {
                        if (coursesRadioButton.isChecked()) {
                            coursesList = sharedStore.getCoursesList();
                            recyclerViewCourses(coursesList);
                        } else {
                            usersList = sharedStore.getUsersList();
                            recyclerViewUsers(usersList);
                        }
                    }

                }
            }
        });
    }

    String name;
    String tags;
    String province;
    String township;

    public void reColor() {
        nameTextView.setTextColor(Color.BLACK);
        tagsEditText.setTextColor(Color.BLACK);
        provinceTextView.setTextColor(Color.BLACK);
        townshipTextView.setTextColor(Color.BLACK);
    }

    public boolean checkErrors() {
        boolean alright = true;
        if (!name.equals("") && !componentsValidation(name)) {
            alright = false;
            nameTextView.setTextColor(Color.RED);
        }
        if (!tags.equals("") && !componentsValidation(tags)) {
            alright = false;
            tagsEditText.setTextColor(Color.RED);
        }
        if (!province.equals("") && !componentsValidation(province)) {
            alright = false;
            provinceTextView.setTextColor(Color.RED);
        }
        if (!township.equals("") && !componentsValidation(township)) {
            alright = false;
            townshipTextView.setTextColor(Color.RED);
        }
        return alright;
    }

    public boolean componentsValidation(String text) { // Formato de Texto genérico (Menos Presentaciones y Mensajes)
        return text.matches("^[a-zA-ZÀ-úÜüÛû,.0-9\\s]+$");
    }

    public boolean allComponentsValidation(String text) { // El único carácter prohibido para el Humano
        return !text.contains("#");
    }

    public void activateButtons() {
        search_button = (Button) findViewById(R.id.search_button);
        inboxButton = (Button) findViewById(R.id.inbox_button);
        courses_button = (Button) findViewById(R.id.courses_button);
        login_perfil_button = (Button) findViewById(R.id.login_perfil_button);
        selectButton = (Button) findViewById(R.id.selectButton);

        login_perfil_button.setText(sharedStore.getUser().getName()); // Nombre del Usuario

        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToInbox();
            }
        });

        courses_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToCoursesList();
            }
        });

        login_perfil_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLoginPerfil();
            }
        });

    }

    public void recyclerViewCourses(List<Course> courses) {
        sharedStore.setCourseWidthAdapted(true);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CourseAdapter(getApplicationContext(), courses));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), coursesList.get(position).getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // No es necesario
                    }
                })
        );
    }

    public void recyclerViewUsers(List<User> users) {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new UsersAdapter(getApplicationContext(), users));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4)
                                + "#" + usersList.get(position).getIdUser(); // "C4"#IdUser
                        sharedStore.setClientMessage(clientMessage);
                        sharedStore.waitUntilResponse(true); // wait()
                        startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // No es necesario
                    }
                })
        );
    }

    public void startComunication() {

        try {
            if (!sharedStore.isConnected()) {
                connectionToServer = new ConnectionToServer("192.168.1.135", 4445); // Se conecta al Servidor.
                connectionToServer.start(); // Crucemos los dedos
            }
            sharedStore.setConnected(true);

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activateButtons();

        startComunication();
        sendClientMessage(); // Al no existir App esto deben hacerlo TODAS las activities






    }
}