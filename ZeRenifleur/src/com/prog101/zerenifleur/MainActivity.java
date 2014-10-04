//Exécuter en testant  72.55.186. de 2 a 254 et port 80

package com.prog101.zerenifleur;

import java.net.InetSocketAddress;
import java.net.Socket;

import android.R.bool;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    private Button mBoutonStart;
    private Button mBoutonPause;
    private ProgressBar mBarre;
    private EditText mEditPort;
    private EditText mEditDebutIp;
    private EditText mEditFinIp;
    private EditText mEditDebutAdresse;
    private TextView mListeAdresse;
    private boolean estEnPause;
    private final long PAUSE = 500;  // delai quand en pause
    private final int TIMEOUT = 500; // delai d'écoute si aucune réponse au port donné.
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBoutonStart = (Button) findViewById(R.id.buttonStart);
        mBoutonPause = (Button) findViewById(R.id.buttonPause);
        mBarre = (ProgressBar) findViewById(R.id.progressBar1);
        mEditPort = (EditText)findViewById(R.id.port);
        mEditDebutIp = (EditText)findViewById(R.id.debutIP);
        mEditFinIp = (EditText)findViewById(R.id.finIP);
        mEditDebutAdresse = (EditText)findViewById(R.id.debutAdresse);
        mListeAdresse = (TextView)findViewById(R.id.liste_ip);
        estEnPause = true;
        
        mBoutonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                estEnPause = false;
                Renifleur sniffeur = new Renifleur();
                sniffeur.execute();
            }
        });  
        mBoutonPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                estEnPause = true;                
            }
        });        
    }
    
    //////////////// classe AsyncTask///////
    public class Renifleur extends AsyncTask<Void,String ,Void > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... args) {
            String ipFound = "";            
            for (int i = Integer.parseInt(mEditDebutIp.getText().toString()); i <= Integer.parseInt(mEditFinIp.getText().toString()) ; i++) {           
                //appelle la méthode qui vérifie si un port est ouvert.
                
                //  Voir méthode portIsOpen pour la gestion de pause...              
                if( portIsOpen(mEditDebutAdresse.getText().toString() + i , Integer.parseInt(mEditPort.getText().toString()), TIMEOUT)) {
                    ipFound =  mEditDebutAdresse.getText().toString()+ i; 
                }
                
              //Boucle ici tant qu'on est en pause... ;)                
                while (estEnPause){
                    try {Thread.sleep(PAUSE);}catch(Exception exe){}
                }
                
                // on envoi le no de la derniere adresse testée  ainsi que 
                //l'adresse complete (seulement si elle a répondu) sinon ce sera ""
                publishProgress(i + "", ipFound);           
            }             
            return null;          
        }

        @Override
        protected void onProgressUpdate(String... valeurs) {
            super.onProgressUpdate(valeurs);
            // Mise à jour de la barre de progression
            // valeurs[0] = ipActuel, où on est rendu
            // range ip 
            // progres = 100 * (ipActuel - ipDebut) / (range ip)
           int progres = (100*(Integer.parseInt(valeurs[0])-Integer.parseInt(mEditDebutIp.getText().toString())))
                   / ( Integer.parseInt(mEditFinIp.getText().toString())-Integer.parseInt(mEditDebutIp.getText().toString())) ;
            mBarre.setProgress(progres);
            
            if (!valeurs[1].equals("")){
             // et ajouter texte au textview par append si il y a eu réponse à l'adresse...
                mListeAdresse.append(valeurs[1] + "\n");                
            }  
        }
        
        @Override
        protected void onPostExecute(Void resultat) {
           
        }
        
        public boolean portIsOpen(String ip, int port, int timeout) {
            
          
            
            try {
                Socket socket = new Socket();
                InetSocketAddress adr = new InetSocketAddress(ip, port);
                socket.connect(adr, timeout);
                socket.close();
                return true;
            } catch (Exception ex) {          
                return false;
            }
        }
    }
}
