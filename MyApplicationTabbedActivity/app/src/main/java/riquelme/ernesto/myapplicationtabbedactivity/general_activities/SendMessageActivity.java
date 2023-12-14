package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;

public class SendMessageActivity extends AppCompatActivity {

    SharedStore sharedStore;

    public SendMessageActivity() {
        this.sharedStore = SharedStore.getInstance();
    }

    Button search_button;
    Button inboxButton;
    Button courses_button;
    Button login_perfil_button;


    TextView senderReceiverTextField;
    TextView subjectTextView;
    TextView messageTextView;

    EditText messageTextMultiLine;
    EditText subjectEditText;
    EditText senderEditText;

    String message;
    String subject;
    String sender;

    Button switchToPerfilButton;
    Button sendMailButton;


    public void switchToPerfil() {
        startActivity(new Intent(this, PerfilActivity.class)); // otherUser
    }

    public void switchToCoursesList() {
        startActivity(new Intent(this, CoursesListActivity.class)); // otherUser
    }

    public void switchToInbox() {
        startActivity(new Intent(this, InboxActivity.class)); // otherUser
    }

    public void switchToSearch() {
        startActivity(new Intent(this, MainActivity.class)); // otherUser
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMail() {
        message = String.valueOf(messageTextMultiLine.getText());
        subject = String.valueOf(subjectEditText.getText());
        sender = String.valueOf(senderEditText.getText());

        if (checkErrors()) {

            LocalDate localDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String currentDate = localDate.format(dateFormatter);

            LocalTime localTime = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentTime = localTime.format(timeFormatter);

            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(7) // C7 # Asunto # Mensaje # Fecha # Hora # tipoMensaje # idCurso # SenderYo # Destino
                    + "#" + subject
                    + "#" + message
                    + "#" + currentDate
                    + "#" + currentTime
                    + "#" + "message"
                    + "#" + "-1"
                    + "#" + sharedStore.getUser().getIdUser()
                    + "#" + sharedStore.getOtherUser().getIdUser();

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait

            if (sharedStore.getResponseResults().equals("Ok")) {
                switchToPerfil();
            }
        }
    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;

        if (!sharedStore.stringComponentsValidation(subject)) {
            alright = false;
            senderReceiverTextField.setTextColor(Color.RED);
        } else {
            senderReceiverTextField.setTextColor(Color.BLACK);
        }

        if (!sharedStore.allComponentsValidation(message) || message.equals("")) {
            alright = false;
            subjectTextView.setTextColor(Color.RED);
        } else {
            subjectTextView.setTextColor(Color.BLACK);
        }

        if (!sharedStore.allComponentsValidation(message) || message.equals("")) {
            alright = false;
            messageTextView.setTextColor(Color.RED);
        } else {
            messageTextView.setTextColor(Color.BLACK);
        }
        return alright;
    }

    public void configComponents() {

        search_button = (Button) findViewById(R.id.search_button);
        inboxButton = (Button) findViewById(R.id.inbox_button);
        courses_button = (Button) findViewById(R.id.courses_button);
        login_perfil_button = (Button) findViewById(R.id.login_perfil_button);

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
                switchToUserPerfil();
            }
        });


        messageTextMultiLine = (EditText) findViewById(R.id.messageTextMultiLine);
        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        senderEditText = (EditText) findViewById(R.id.senderEditText);

        switchToPerfilButton = (Button) findViewById(R.id.switchToPerfilButton);
        sendMailButton = (Button) findViewById(R.id.sendMailButton);

        switchToPerfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToPerfil();
            }
        });

        sendMailButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        senderReceiverTextField = (TextView) findViewById(R.id.senderReceiverTextField);
        subjectTextView = (TextView) findViewById(R.id.subjectTextView);
        messageTextView = (TextView) findViewById(R.id.messageTextView);

    }

    public void loadData(){
        senderEditText.setText(sharedStore.getOtherUser().getName());
        senderEditText.setEnabled(false);
    }

    public void switchToUserPerfil() {
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(4) + "#" + sharedStore.getUser().getIdUser()); // "C4"
        sharedStore.waitUntilResponse(true); // wait()

        if (sharedStore.getResponseResults().equals("Ok")) {
            switchToPerfil();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);
        configComponents();
        loadData();
    }
}
