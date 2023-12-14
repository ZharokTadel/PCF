package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;

public class LoginActivity extends AppCompatActivity {
    SharedStore sharedStore;

    public LoginActivity() {
        this.sharedStore = SharedStore.getInstance();
    }

    String name;
    String email;
    String password;

    TextView nameTextView;
    TextView mailTextView;
    TextView passwordTextView;
    TextView registerTextView;

    Button loginButton;
    Button switchToSearchButton;

    EditText editTextTextPersonName;
    EditText editTextTextPersonEmail;
    EditText editTextTextPassword;

    public void switchToSearch() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void switchToRegister() {
        startActivity(new Intent(this, RegisterUserActivity.class));
    }

    public void activateButtons() {
        registerTextView = (TextView) findViewById(R.id.registerTextView);
        loginButton = (Button) findViewById(R.id.loginButton);
        switchToSearchButton = (Button) findViewById(R.id.switchToSearchButton);

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });

        switchToSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSearch();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    public void login() {
        nameTextView = findViewById(R.id.titleCourseTextView);
        mailTextView = findViewById(R.id.tagsRTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        editTextTextPersonName = (EditText) findViewById(R.id.titleCourseEditText);
        editTextTextPersonEmail = (EditText) findViewById(R.id.tagsREditText);
        editTextTextPassword = (EditText) findViewById(R.id.editTextTextPassword);

        name = String.valueOf(editTextTextPersonName.getText());
        email = String.valueOf(editTextTextPersonEmail.getText());
        password = String.valueOf(editTextTextPassword.getText());

        if (checkErrors()) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(2) + "#" + name + "#" + email + "#" + password; // C2 # Nombre # Email # Contraseña

            sharedStore.setClientMessage(clientMessage); // notify() al hilo TCP ConnectionToServer
            sharedStore.waitUntilResponse(true); // wait() al Controlador del hilo Gráfico

            if (sharedStore.getResponseResults().equals("Ok")) {
                switchToSearch();
            } else {
                Toast.makeText(getApplicationContext(), sharedStore.getResponseResults(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean componentsValidation(String text) { // Formato de Texto genérico (Menos Presentaciones y Mensajes)
        return text.matches("^[a-zA-ZÀ-úÜüÛû,.0-9\\s]+$");
    }

    public boolean allComponentsValidation(String text) { // El único carácter prohibido para el Humano en cualquier Carácter de comunicación: (Cliente <-#-> Servidor)
        return !text.contains("#");
    }

    public boolean emailValidation() { // Formato de email
        return Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(email)
                .matches();
    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;
        if (name.equals("") || !componentsValidation(name)) {
            alright = false;
            nameTextView.setTextColor(Color.RED);
        } else {
            nameTextView.setTextColor(Color.BLACK);
        }

        if (password.equals("") || !allComponentsValidation(password)) {
            alright = false;
            passwordTextView.setTextColor(Color.RED);
        } else {
            passwordTextView.setTextColor(Color.BLACK);
        }

        if (!emailValidation()) {
            alright = false;
            mailTextView.setTextColor(Color.RED);
        } else {
            mailTextView.setTextColor(Color.BLACK);
        }
        return alright;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activateButtons();
    }

}
