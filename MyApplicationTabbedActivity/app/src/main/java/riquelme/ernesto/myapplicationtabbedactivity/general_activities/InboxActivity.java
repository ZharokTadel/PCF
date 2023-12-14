package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Message;
import riquelme.ernesto.myapplicationtabbedactivity.recyclers.MailAdapter;

public class InboxActivity extends AppCompatActivity {
    SharedStore sharedStore;

    Message selectedMessage;

    public InboxActivity() {
        this.sharedStore = SharedStore.getInstance();
        this.selectedMessage = new Message();
    }

    boolean received;
    Button search_button;
    Button inboxButton;
    Button courses_button;
    Button login_perfil_button;
    Button mailButton;

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
        sharedStore.setOtherUser(sharedStore.getUser());
        startActivity(new Intent(this, PerfilActivity.class));
    }

    public void activateButtons() {
        search_button = (Button) findViewById(R.id.search_button);
        inboxButton = (Button) findViewById(R.id.inbox_button);
        courses_button = (Button) findViewById(R.id.courses_button);
        login_perfil_button = (Button) findViewById(R.id.login_perfil_button);
        mailButton = (Button) findViewById(R.id.mailButton);

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
                switchToLoginPerfil();
            }
        });

        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (received) {
                    sendedMessages();
                } else {
                    receivedMessages();
                }
            }
        });
    }

    private void receivedMessages() {
        received = true;
        mailButton.setText("Recibidos");
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(10) + "#" + sharedStore.getUser().getIdUser()); // C10 # IdUser
        sharedStore.waitUntilResponse(true); // wait()
        recyclerViewMessages(sharedStore.getInboxReceivedList());
    }

    private void sendedMessages() {
        received = false;
        mailButton.setText("Enviados");
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(11) + "#" + sharedStore.getUser().getIdUser()); // C9 # IdUser
        sharedStore.waitUntilResponse(true); // wait()
        recyclerViewMessages(sharedStore.getInboxSentList());
    }

    public void switchToReadMessage() {
        startActivity(new Intent(this, ReadMessageActivity.class));
    }

    public void recyclerViewMessages(List<Message> messageList) {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MailAdapter(getApplicationContext(), messageList));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        sharedStore.setSelectedMessage(messageList.get(position));
                        switchToReadMessage();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // No es necesario
                    }
                })
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        activateButtons();
        receivedMessages(); // Nada más iniciar
    }
}
