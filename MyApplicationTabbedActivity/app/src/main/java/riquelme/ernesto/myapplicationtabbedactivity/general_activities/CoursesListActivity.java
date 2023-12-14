package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Course;
import riquelme.ernesto.myapplicationtabbedactivity.recyclers.CourseAdapter;

public class CoursesListActivity extends AppCompatActivity {
    SharedStore sharedStore;
    boolean received;

    public CoursesListActivity() {
        this.sharedStore = SharedStore.getInstance();
        this.received = true;
    }

    Button search_button;
    Button inboxButton;
    Button courses_button;
    Button login_perfil_button;

    Button switchToVCButton;
    Button updateCoursesButton;
    Button createCourseButton;

    public void switchToSearch() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void switchToInbox() {
        startActivity(new Intent(this, InboxActivity.class));
    }

    public void switchToLoginPerfil() {
        startActivity(new Intent(this, PerfilActivity.class));
    }

    public void createUpdateCourse(){
        startActivity(new Intent(this, RegisterCourseActivity.class));
    }


    public void configComponents() {
        search_button = (Button) findViewById(R.id.search_button);
        inboxButton = (Button) findViewById(R.id.inbox_button);
        courses_button = (Button) findViewById(R.id.courses_button);
        login_perfil_button = (Button) findViewById(R.id.login_perfil_button);

        switchToVCButton = (Button) findViewById(R.id.switchToVCButton);
        updateCoursesButton = (Button) findViewById(R.id.updateCoursesButton);
        createCourseButton = (Button) findViewById(R.id.createCourseButton);

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
                loadData();
            }
        });

        login_perfil_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLoginPerfil();
            }
        });

        switchToVCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        updateCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedStore.getSelectedCourse() != null) {
                    createUpdateCourse();
                }
            }
        });

        createCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedStore.setSelectedCourse(null);
                createUpdateCourse();
            }
        });
    }

    public void loadData() {
        String clientMessage = "";
        if (!received) {
            courses_button.setText("Recibidos");
            clientMessage = sharedStore.getProtocolMessages().getClientArgument(18) // C18#IdUser Cursos Impartidos
                    + "#" + sharedStore.getUser().getIdUser();

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            recyclerViewCourses(sharedStore.getCoursesListTeacher());
            received = true;
        } else {
            courses_button.setText("Impartidos");
            clientMessage = sharedStore.getProtocolMessages().getClientArgument(19) // C19#IdUser Cursos Recibiendo
                    + "#" + sharedStore.getUser().getIdUser();

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            recyclerViewCourses(sharedStore.getCoursesListStudent());
            received = false;
        }
    }

    public void recyclerViewCourses(List<Course> courseList) {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CourseAdapter(getApplicationContext(), courseList));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), "Por alguna razón que desconozco explota " +
                                "al intentar coger la posición: " + position, Toast.LENGTH_SHORT).show();
                        sharedStore.setSelectedCourse(courseList.get(position));
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
        setContentView(R.layout.activity_courses_lists);

        sharedStore.setCourseWidthAdapted(false); // <- Para que Cursos cojan una vista distinta

        configComponents();
        loadData(); // Nada más empezar se actualiza
    }
}
