package com.example.admin.add_practica4_jaime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.admin.add_practica4_jaime.adapter.Adaptador;
import com.example.admin.add_practica4_jaime.gestion.GestorHibernate;
import com.example.admin.add_practica4_jaime.pojo.Keep;
import com.example.admin.add_practica4_jaime.pojo.Usuario;

import java.util.List;

public class Principal extends AppCompatActivity {

    private Usuario user;
    private List<Keep> listaHibernate;
    private GestorHibernate gestorHibernate = new GestorHibernate();

    private Adaptador adt;
    private ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        lv = (ListView) findViewById(R.id.listView);
        user = getIntent().getParcelableExtra("usuario");

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(hayInternet())
            sincronizar();

        super.onResume();
    }

    public void init() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Principal.this);
                LayoutInflater inflater = Principal.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.add_keep, null);
                final EditText et = (EditText) view.findViewById(R.id.etAdd);
                adb.setView(view)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Keep k = listaHibernate.get(position);
                                k.setContenido(et.getText().toString());
                                k.setEstado(true);
                                actualizar();
                                if (hayInternet()) {
                                    Runnable r = new Runnable() {
                                        @Override
                                        public void run() {
                                            gestorHibernate.update(k, user);
                                        }
                                    };
                                    Thread t = new Thread(r);
                                    t.start();
                                }
                                actualizar();
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                          @Override
                                          public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                              AlertDialog.Builder b = new AlertDialog.Builder(Principal.this);
                                              b.setMessage("¿Borrar?")
                                                      .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                              final Keep k = listaHibernate.get(position);
                                                              Runnable r = new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      gestorHibernate.delete(k, user);
                                                                  }
                                                              };
                                                              Thread t = new Thread(r);
                                                              t.start();
                                                              listaHibernate.remove(position);

                                                              actualizar();
                                                          }
                                                      })
                                                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                          }
                                                      }).show();
                                              return false;
                                          }
                                      }
        );

    }

    /**************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
         if (id== R.id.action_search){
            if(hayInternet())
                sincronizar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**************************************************************/


    public void addKeep(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_keep, null);
        final EditText et = (EditText) view.findViewById(R.id.etAdd);
        adb.setView(view)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Keep k= new Keep(gestorHibernate.getId(listaHibernate), et.getText().toString(), true);
                        listaHibernate.add(k);
                        actualizar();
                        if (hayInternet()) {
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    gestorHibernate.create(k, user);
                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                        }

                        actualizar();
                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }

    /**************************************************************/

    private boolean hayInternet() {
        ConnectivityManager m = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //boolean is3g = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWiFi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if (/*is3g ||*/ isWiFi) {
            return true;
        }
        return false;
    }


    private void actualizar() {
        adt.notifyDataSetChanged();
    }

    private void sincronizar(){
        CargarHibernate cargarHibernate = new CargarHibernate();
        cargarHibernate.execute();
    }

    /**************************************************************/

    private class CargarHibernate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            listaHibernate = gestorHibernate.getKeeps(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adt =new Adaptador(getBaseContext(),R.layout.item,listaHibernate);
            lv.setAdapter(adt);
        }
    }
}
