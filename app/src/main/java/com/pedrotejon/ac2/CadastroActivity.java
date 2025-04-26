package com.pedrotejon.ac2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class CadastroActivity extends AppCompatActivity {

    EditText edtNome, edtTempo;
    Button btnSalvar;
    ListView listViewTreinos;
    BancoHelper databaseHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaTreinos;
    ArrayList<Integer> listaIds;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            edtNome = findViewById(R.id.edtNome);
            edtTempo = findViewById(R.id.edtTempo);
            btnSalvar = findViewById(R.id.btnSalvar);
            listViewTreinos = findViewById(R.id.listViewTreinos);
            databaseHelper = new BancoHelper(this);
            carregarTreinos();

            btnSalvar.setOnClickListener(this::salvarTreino);

            listViewTreinos.setOnItemClickListener((parent, view, position, x) -> {
                int id = listaIds.get(position);
                Cursor cursor = databaseHelper.obterTreinoPorId(id);
                String nome = cursor.getString(1);
                Integer tempo = cursor.getInt(2);
                edtNome.setText(nome);
                edtTempo.setText(String.valueOf(tempo));
                btnSalvar.setText("Atualizar");
                btnSalvar.setOnClickListener(v -> atualizarTreino(v, id));

                listViewTreinos.setOnItemLongClickListener((adapterView, view1, pos, l) -> {
                    int idTreino = listaIds.get(pos);
                    int deletado = databaseHelper.excluirTreino(idTreino);
                    if (deletado > 0) {
                        Toast.makeText(this, "Treino excluído!", Toast.LENGTH_SHORT).show();
                        carregarTreinos();
                    }
                    return true;
                });
            });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarTreino(View view, Integer treinoId) {
        String novoNome = edtNome.getText().toString();
        String novoTempoString = edtTempo.getText().toString();
        if (!novoNome.isEmpty() && !novoTempoString.isEmpty()) {
            int resultado = databaseHelper.atualizarTreino(treinoId, novoNome, Integer.parseInt(novoTempoString));
            if (resultado > 0) {
                Toast.makeText(this, "Treino atualizado!", Toast.LENGTH_SHORT).show();
                carregarTreinos();
                edtNome.setText("");
                edtTempo.setText("");
                btnSalvar.setText("Salvar");
                btnSalvar.setOnClickListener(this::salvarTreino);
            } else {
                Toast.makeText(this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void salvarTreino(View view) {
        String nome = edtNome.getText().toString();
        String tempoString = edtTempo.getText().toString();

        if (!nome.isEmpty() && !tempoString.isEmpty()) {
            int tempo = Integer.parseInt(tempoString);
            long resultado = databaseHelper.inserirTreino(nome, tempo);
            if (resultado != -1) {
                Toast.makeText(this, "Treino salvo!", Toast.LENGTH_SHORT).show();
                edtNome.setText("");
                edtTempo.setText("");
                carregarTreinos();
            } else {
                Toast.makeText(this, "Erro ao salvar!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
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
}