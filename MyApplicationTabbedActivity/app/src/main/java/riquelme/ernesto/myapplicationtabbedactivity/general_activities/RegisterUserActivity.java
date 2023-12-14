package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.Buffer;
import java.util.regex.Pattern;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.User;

public class RegisterUserActivity extends AppCompatActivity {
    SharedStore sharedStore;

    private String name;
    private String password;
    private String email;
    private String confirmedPassword;
    private String shortPresentation;
    private String longPresentation;
    private String selectedProvince;
    private String selectedTownShip;
    private String photoPath;

    TextView nameTextView;
    TextView mailTextView;
    TextView provinceTextView;
    TextView townshipTextView;
    TextView passwordTextView;
    TextView passwordTextView2;
    TextView presentationTextView;

    EditText editTextTextPersonName;
    EditText editTextTextPersonEmail;
    EditText editTextTextPersonProvince;
    EditText editTextTextPersonTownship;
    EditText editTextTextPassword;
    EditText editTextTextPassword2;
    EditText editTextTextMultiLine;

    ImageView photoImageView;

    Button uploadPhotoButton;
    Button switchToLoginButton;
    Button registerButton;

    Button deleteButton;

    public RegisterUserActivity() {
        sharedStore = SharedStore.getInstance();
        this.photoPath = "";
    }

    public void switchToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void switchToSearch() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private Bitmap getBitmap(Buffer buffer, int width, int height) {
        buffer.rewind();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }

    public void loadComponents() {
        nameTextView = (TextView) findViewById(R.id.titleCourseTextView);
        mailTextView = (TextView) findViewById(R.id.tagsRTextView);
        provinceTextView = (TextView) findViewById(R.id.openDateTextView);
        townshipTextView = (TextView) findViewById(R.id.closeDateTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        passwordTextView2 = (TextView) findViewById(R.id.passwordTextView2);
        presentationTextView = (TextView) findViewById(R.id.presentationCourseTextView);

        editTextTextPersonName = (EditText) findViewById(R.id.titleCourseEditText);
        editTextTextPersonEmail = (EditText) findViewById(R.id.tagsREditText);
        editTextTextPersonProvince = (EditText) findViewById(R.id.editTextTextPersonProvince);
        editTextTextPersonTownship = (EditText) findViewById(R.id.editTextTextPersonTownship);
        editTextTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        editTextTextPassword2 = (EditText) findViewById(R.id.editTextTextPassword2);
        editTextTextMultiLine = (EditText) findViewById(R.id.presentationCourseTextMultiLine);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        uploadPhotoButton = (Button) findViewById(R.id.uploadPhotoButton);
        switchToLoginButton = (Button) findViewById(R.id.switchToCoursesListButton);
        registerButton = (Button) findViewById(R.id.registerCourseButton);
        deleteButton = (Button) findViewById(R.id.deleteCourseButton);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(6)
                        + "#" + sharedStore.getUser().getEmail());
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getResponseResults().equals("Ok")) {
                    sharedStore.setUser(new User());
                    switchToSearch();
                } else {
                    Toast.makeText(getApplicationContext(), sharedStore.getResponseResults(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteButton.setVisibility(View.INVISIBLE);

        if (sharedStore.getOtherUser().getIdUser() == sharedStore.getUser().getIdUser()) {
            if (sharedStore.getUser().getIdUser() != -1) {
                deleteButton.setVisibility(View.VISIBLE);
            }
        }

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilechooser(view);
            }
        });

        switchToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedStore.getUser().getIdUser() == -1) {
                    switchToLogin();
                } else {
                    switchToSearch();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void onActivityResult(int requestcode, int resulCode, Intent data) {
        super.onActivityResult(requestcode, resulCode, data);
        Context context = getApplicationContext();

        if (requestcode == requestcode && resulCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            } else {
                Uri uri = data.getData(); // Pasar la Fotografía : FileNotFoundException

                // File file = new File(uri.getPath());
                // final String[] split = file.getPath().split(":");
                // photoPath = split[1];

                photoImageView.setImageURI(uri);
            }
        }
    }

    int requestcode = 1;

    public void openFilechooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestcode);
    }

    public void register() {
        name = String.valueOf(editTextTextPersonName.getText());
        email = String.valueOf(editTextTextPersonEmail.getText());
        password = String.valueOf(editTextTextPassword.getText());
        confirmedPassword = String.valueOf(editTextTextPassword2.getText());
        shortPresentation = String.valueOf(editTextTextMultiLine.getText());
        longPresentation = ""; // No hay espacio en la pantalla del móvil
        selectedProvince = String.valueOf(editTextTextPersonProvince.getText());
        selectedTownShip = String.valueOf(editTextTextPersonTownship.getText());

        boolean photoExists;


        // Comprobación de Datos Opcionales
        photoExists = (!photoPath.equals(""));

        if (longPresentation.equals("")) {
            longPresentation = "noLongPresentation";
        }

        if (checkErrors()) {
            if (sharedStore.getUser().getIdUser() == -1) {
                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(1) + "#" // C1 # noPhoto # Juanito Perez # jp@gmail...
                        + photoExists + "#"
                        + name + "#"
                        + email + "#"
                        + password + "#"
                        + selectedProvince + "#"
                        + selectedTownShip + "#"
                        + shortPresentation + "#"
                        + longPresentation;

                sharedStore.setUser(new User(photoExists, name, email, selectedProvince, selectedTownShip, shortPresentation, longPresentation)); // Preparación del Login en el lado Cliente

                if (photoExists) { // En caso de tener fotografía la prepara para enviarsela al Servidor, en caso contrario no
                    sharedStore.setFilePath(photoPath);
                } else {
                    sharedStore.setFilePath("false");
                }

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getResponseResults().equals("Ok")) {
                    switchToSearch();
                } else {
                    Toast.makeText(getApplicationContext(), sharedStore.getResponseResults(), Toast.LENGTH_SHORT).show();
                }
            } else {
                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(5) + "#" // C5#noPhoto#Juanito Perez#jp@gmail...
                        + sharedStore.getUser().getIdUser() + "#"
                        + photoExists + "#"
                        + name + "#"
                        + email + "#"
                        + password + "#"
                        + selectedProvince + "#"
                        + selectedTownShip + "#"
                        + shortPresentation + "#"
                        + longPresentation;

                sharedStore.setUser(new User(photoExists, name, email, selectedProvince, selectedTownShip, shortPresentation, longPresentation)); // Preparación del Login en el lado Cliente

                if (photoExists) {
                    sharedStore.setFilePath(photoPath);
                } else {
                    sharedStore.setFilePath("false");
                }

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getResponseResults().equals("Ok")) {
                    switchToSearch();
                } else {
                    Toast.makeText(getApplicationContext(), sharedStore.getResponseResults(), Toast.LENGTH_SHORT).show();
                }
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

        if (!password.equals(confirmedPassword)) {
            alright = false;
            passwordTextView2.setTextColor(Color.RED);
        } else {
            passwordTextView2.setTextColor(Color.BLACK);
        }

        if (!emailValidation()) {
            alright = false;
            mailTextView.setTextColor(Color.RED);

        } else {
            mailTextView.setTextColor(Color.BLACK);
        }

        if (shortPresentation.equals("") || !allComponentsValidation(shortPresentation)) {
            alright = false;
            presentationTextView.setTextColor(Color.RED);
        } else {
            presentationTextView.setTextColor(Color.BLACK);
        }
        return alright;
    }

    public void loadData(){
        editTextTextPersonName.setText(sharedStore.getUser().getName());
        editTextTextPersonEmail.setText(sharedStore.getUser().getEmail());
        editTextTextPersonProvince.setText(sharedStore.getUser().getProvince());
        editTextTextPersonTownship.setText(sharedStore.getUser().getTownship());
        editTextTextMultiLine.setText(sharedStore.getUser().getShortPresentation());
        editTextTextPersonEmail.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        sharedStore.setCourseWidthAdapted(false); // <- Para que Cursos cojan una vista distinta
        loadComponents();

        if (sharedStore.getUser().getIdUser() != -1) {
            loadData();
        }
    }
}
