package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;

public class ReadMessageActivity extends AppCompatActivity {
    SharedStore sharedStore;

    public ReadMessageActivity() {
        this.sharedStore = SharedStore.getInstance();
    }

    TextView senderReceiverTextField;
    TextView senderReceiverNameTextField;
    TextView subjectTextField;
    TextView mailTextField;

    Button switchInboxButton;
    Button solInvButton;
    Button deleteMailButton;

    Button search_button;
    Button inboxButton;
    Button courses_button;
    Button login_perfil_button;

    public void switchToSearch() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void switchToInbox() {
        startActivity(new Intent(this, InboxActivity.class));
    }

    public void switchToCoursesList() {
        startActivity(new Intent(this, CoursesListActivity.class));
    }

    public void switchToLoginPerfil() {
        startActivity(new Intent(this, PerfilActivity.class));
    }

    public void configComponents() {
        search_button = (Button) findViewById(R.id.search_button);
        inboxButton = (Button) findViewById(R.id.inbox_button);
        courses_button = (Button) findViewById(R.id.courses_button);
        login_perfil_button = (Button) findViewById(R.id.login_perfil_button);
        login_perfil_button.setText(sharedStore.getUser().getName()); // Nombre del Usuario

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSearch();
            }
        });

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
                sharedStore.setOtherUser(sharedStore.getUser());
                switchToLoginPerfil();
            }
        });


        senderReceiverTextField = (TextView) findViewById(R.id.senderReceiverTextField);
        senderReceiverNameTextField = (TextView) findViewById(R.id.senderReceiverNameTextField);
        subjectTextField = (TextView) findViewById(R.id.subjectTextField);
        mailTextField = (TextView) findViewById(R.id.mailTextField);

        switchInboxButton = (Button) findViewById(R.id.switchToPerfilButton);
        solInvButton = (Button) findViewById(R.id.solInvButton);
        deleteMailButton = (Button) findViewById(R.id.sendMailButton);

        if (sharedStore.getSelectedMessage().getType().equals("message")) {
            solInvButton.setVisibility(View.INVISIBLE);
        }

        switchInboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToInbox();
            }
        });

        if (sharedStore.getSelectedMessage().getType().equals("invitation") || sharedStore.getSelectedMessage().getType().equals("request")) {
            solInvButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    LocalDate localDate = LocalDate.now();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String currentDate = localDate.format(dateFormatter);

                    LocalTime localTime = LocalTime.now();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String currentTime = localTime.format(timeFormatter);

                    String messageToServer = "";
                    if (sharedStore.getSelectedMessage().getType().equals("invitation")) {
                        messageToServer = sharedStore.getProtocolMessages().getClientArgument(13) // C13 # ...
                                + "#" + sharedStore.getSelectedMessage().getIdSender() // 1. Id Solicitante
                                + "#" + sharedStore.getSelectedMessage().getIdTeachersCourse() // 2. Id Curso
                                + "#" + sharedStore.getSelectedMessage().getIdMessage() // 3. Id Mensaje (Para el Borrado)
                                + "#" + sharedStore.getSelectedMessage().getIdReceiver() // 4. Id Profesor
                                + "#Solicitud Aceptada." // 5. Asunto
                                + "#Se ha aceptado una solicitud a tu nombre, concediendote una plaza en el Curso. Revisa tus Cursos para acceder al mismo." // 6. Mensaje
                                + "#" + currentDate // 7. Fecha
                                + "#" + currentTime; // 8. Hora
                    } else if (sharedStore.getSelectedMessage().getType().equals("request")) {
                        messageToServer = sharedStore.getProtocolMessages().getClientArgument(14) // C14 # ...
                                + "#" + sharedStore.getSelectedMessage().getIdReceiver() // 1. Id Profesor
                                + "#" + sharedStore.getSelectedMessage().getIdTeachersCourse() // 2. Id Curso
                                + "#" + sharedStore.getSelectedMessage().getIdMessage() // 3. Id Mensaje (Para el Borrado)
                                + "#" + sharedStore.getSelectedMessage().getIdSender() // 4. Id Alumno
                                + "#Invitación Aceptada." // 5. Asunto
                                + "#Se ha aceptado una invitación a uno de tus cursos, concediendole una plaza en el mismo. Puedes comprobarlo en la Seccion \"Registros Alumnos\" del Curso." // 6. Mensaje
                                + "#" + currentDate // 7. Fecha
                                + "#" + currentTime; // 8. Hora
                    }
                }
            });
        }
        deleteMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(15) // C15 # IdMensaje # isSender
                        + "#" + sharedStore.getSelectedMessage().getIdMessage();
                if (sharedStore.getUser().getIdUser() == sharedStore.getSelectedMessage().getIdSender()) {
                    clientMessage += "#" + true;
                } else {
                    clientMessage += "#" + false;
                }
                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true);

                if (sharedStore.getResponseResults().equals("Ok")) {
                    switchToInbox();
                } else {
                    Toast.makeText(getApplicationContext(), sharedStore.getResponseResults(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadData() {
        senderReceiverNameTextField.setText(sharedStore.getSelectedMessage().getSenderReceiverName());
        subjectTextField.setText(sharedStore.getSelectedMessage().getSubject());
        mailTextField.setText(sharedStore.getSelectedMessage().getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_read);
        configComponents();
        loadData();
    }
}
