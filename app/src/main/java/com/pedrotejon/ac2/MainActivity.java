package com.pedrotejon.ac2;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView txtExercicio, txtContador;
    BancoHelper databaseHelper;
    ArrayAdapter<String> adapter;
    ListView listViewTreinos;
    ArrayList<String> listaTreinos;
    ArrayList<Integer> listaIds;
    ThreadTreino threadTreino;
    Integer tempoAtual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtExercicio = findViewById(R.id.txtExercicio);
        txtContador = findViewById(R.id.txtContador);
        listViewTreinos = findViewById(R.id.listViewTreinos);
        databaseHelper = new BancoHelper(this);
        carregarTreinos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTreinos();
    }

    public void iniciarTreino(View view) {
        threadTreino = new ThreadTreino();
        threadTreino.start();

        this.stopService(new Intent(this, MeuServico.class));
        this.startService(new Intent(this, MeuServico.class));
    }

    public void abrirCadastro(View view) {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    private void carregarTreinos() {
        Cursor cursor = databaseHelper.listarTreinos();
        listaTreinos = new ArrayList<>();
        listaIds = new ArrayList<>();
        if (cursor.moveToFirst()) {
            var ordem = 0;
            do {
                ordem++;
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                int tempo = cursor.getInt(2);
                listaTreinos.add(ordem + " - " + nome + " (" + tempo + "s)");
                listaIds.add(id);
            } while (cursor.moveToNext());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaTreinos);
        listViewTreinos.setAdapter(adapter);
    }


    class ThreadTreino extends Thread {

        @Override
        public void run() {
            try {
                var cursor = databaseHelper.listarTreinos();

                if (cursor.moveToFirst()) {

                    do {
                        String treino = cursor.getString(1);
                        int tempo = cursor.getInt(2);
                        runOnUiThread(() -> {
                            txtExercicio.setText(treino);
                            txtContador.setText(Integer.toString(tempo));
                        });

                        for(int i = 0; i < tempo; i++) {
                            tempoAtual = i;
                            runOnUiThread(() -> txtContador.setText(Integer.toString(tempoAtual)));
                            Thread.sleep(1000);
                        }
                    } while (cursor.moveToNext());
                }

                runOnUiThread(() -> {
                    txtExercicio.setText("Treino finalizado!");
                    txtContador.setText("N/A");
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}