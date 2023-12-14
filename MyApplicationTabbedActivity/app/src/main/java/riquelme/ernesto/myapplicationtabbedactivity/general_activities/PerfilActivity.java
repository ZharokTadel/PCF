package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.Buffer;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.User;

public class PerfilActivity extends AppCompatActivity {
    SharedStore sharedStore;

    public PerfilActivity() {
        this.sharedStore = SharedStore.getInstance();
    }

    Button search_button;
    Button inboxButton;
    Button courses_button;
    Button login_perfil_button;

    TextView nameTV;
    TextView provinceTV;
    TextView townshipTV;
    TextView shortPresentationTV;


    Button editButton;
    Button sendMessageButton;
    Button logoutButton;

    ImageView photoImageView;

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
        // startActivity(new Intent(this, InboxActivity.class));
    }

    public void activateButtons() {
        search_button = (Button) findViewById(R.id.search_button);
        inboxButton = (Button) findViewById(R.id.inbox_button);
        courses_button = (Button) findViewById(R.id.courses_button);
        login_perfil_button = (Button) findViewById(R.id.login_perfil_button);

        if (sharedStore.getUser().getIdUser() != -1) {
            login_perfil_button.setText(sharedStore.getUser().getName()); // Nombre del Usuario
        }

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
                switchToLoginPerfil();
            }
        });

        nameTV = (TextView) findViewById(R.id.nameTV);
        provinceTV = (TextView) findViewById(R.id.provinceTV);
        townshipTV = (TextView) findViewById(R.id.townshipTV);
        shortPresentationTV = (TextView) findViewById(R.id.shortPresentationTV);

        editButton = (Button) findViewById(R.id.editButton);
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        if (sharedStore.getOtherUser().getIdUser() == sharedStore.getUser().getIdUser()) {
            sendMessageButton.setVisibility(View.INVISIBLE);
        } else {
            editButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSendMessage();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(3)); // "C3"
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getResponseResults().equals("Ok")) {
                    sharedStore.setUser(new User());
                    switchToSearch();
                } else {
                    Toast.makeText(getApplicationContext(), sharedStore.getResponseResults(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        Bitmap bmp = sharedStore.getPhoto();
        photoImageView.setImageBitmap(bmp);
    }

    public void loadData() {
        nameTV.setText(sharedStore.getOtherUser().getName());
        provinceTV.setText(sharedStore.getOtherUser().getProvince());
        townshipTV.setText(sharedStore.getOtherUser().getTownship());
        shortPresentationTV.setText(sharedStore.getOtherUser().getShortPresentation());

        // login_perfil_button.setBackgroundColor("#6CAAE8");
    }

    public void switchToRegister() {
        startActivity(new Intent(this, RegisterUserActivity.class));
    }

    public void switchToSendMessage() {
        startActivity(new Intent(this, SendMessageActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_perfil);

        activateButtons();
        loadData();
    }


    private Bitmap getBitmap(Buffer buffer, int width, int height) {
        buffer.rewind();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }
}
