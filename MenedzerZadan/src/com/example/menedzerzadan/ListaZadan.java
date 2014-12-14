package com.example.menedzerzadan;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import com.example.menedzerzadan.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Klasa bedaca druga aktywnoscia
 * Jest odpowiedzialna za:
 * - przegladanie zada�
 * - tworzenie nowych zada�
 * - edytowanie zada�
 * @author Dawid Plichta
 *
 */

public class ListaZadan extends Activity {
	
	File nowyPlik;
	final ArrayList<String> list = new ArrayList<String>();
	boolean isFile=true;
	boolean czyWczesniej=false;
	StableArrayAdapter adapter;
    String zadanie;
	String minutaStartu;
	String godzinaStartu;
	String godzinaKonca;
	String minutaKonca;
	String opisZadania="";
	String name="";
	/**
	 * Metoda odpowiedzialna za wyswietlenie listy zada� oraz przycisku (dodaj zadanie)
	 */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.zadania_dnia);
	final View view = LayoutInflater.from(ListaZadan.this).inflate(R.layout.nowe_zadanie, null);  //layout okna Zadania
	final TimePicker czasStartu = (TimePicker) view.findViewById(R.id.timePicker1);  //zegar czasu startu
	final TimePicker czasKonca = (TimePicker) view.findViewById(R.id.TimePicker01);  //zegra czasu konca
	final Button addButton = (Button) findViewById(R.id.button1);    // button dodawania
	final EditText editText = (EditText) view.findViewById(R.id.editText1);  //miejsce na opis zadania
	final Button locationButton = (Button) view.findViewById(R.id.locationButton);
	final ListView listview = (ListView) findViewById(R.id.listView1);   //lista na ekranie glownym
    final AlertDialog.Builder oknoZadania = new AlertDialog.Builder(this);  // okno zadania
    final AlertDialog.Builder oknoBledu=new AlertDialog.Builder(this);   //okno bledu
	Bundle bundle = getIntent().getExtras();
	final Context context= this.getApplicationContext();
	name=bundle.getString("name")+".txt";	
	CzyJestPlik(name);
	/*
	 * Jesli plik o podanej nazwie nie istnieje tworzymy nowy plik, jesli istnieje wczytujemy liste z pliku
	 * 
	 */
	if(isFile==false){
		StworzNowyPlik(name);
	}
	else {
		WczytajListeZPliku(name);
	}
	adapter = new StableArrayAdapter(context,android.R.layout.simple_list_item_1, list);
	czasStartu.setIs24HourView(true);   //ustawienie zegarow na 24-godzinne
	czasKonca.setIs24HourView(true);
	/*
	 * Zmienna typu OnClickListener
	 * Jej zadaniem jest obsluga klikania w przyciski "cofnij" i "usun"
	 * Nadpisuje liste w pliku oraz aktualizuje liste wyswietlona na glownym ekranie
	 * 
	 */
    final DialogInterface.OnClickListener Clickacz=new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	ZapisDoPliku(list,name);
        	adapter.notifyDataSetChanged();
        	((ViewGroup)view.getParent()).removeView(view);
            return;   
        }
    };
    /*
     * Ustawienie parametrow okna bledu
     */
    oknoBledu.setTitle("Blad!");
    oknoBledu.setMessage("Zly przedzial czasowy zadania lub pusty opis zadania");
    oknoBledu.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	return;
	    	
	    }});
    oknoBledu.setCancelable(false);
    /*
     * Ustawienie parametrow okna zadania
     */
    oknoZadania.setTitle("Dodaj zadanie");  
    oknoZadania.setView(view);
    /*
     * Metoda ustawiajaca parametry przycisku "ok"
     * Glownym zadaniem jest ustawienie obslugi klikania na przycisk:
     * - Jesli czas startu zadania jest wiekszy od czasu ko�ca zadania lub opis zadania jest pusty wyswietla okno bledu
     * - w kazdym innym przypadku zapisuje ustawione parametry do zmiennej typu string, przetwarza go i dopisuje do listy w odpowiednim miejscu
     * - aktualizuje liste na glownym ekranie oraz w pliku
     * 
     */
    oknoZadania.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	    public void onClick(DialogInterface dialog, int whichButton) {  
	    	if((czasStartu.getCurrentHour()>czasKonca.getCurrentHour())||((czasStartu.getCurrentHour()==czasKonca.getCurrentHour()&&czasStartu.getCurrentMinute()>czasKonca.getCurrentMinute()))
	    		||editText.getText().toString().length()==0){
	    		oknoBledu.show();
	    		((ViewGroup)view.getParent()).removeView(view);
	    	}
	    	else{
	    	zadanie=(String.valueOf(czasStartu.getCurrentHour())+":"+String.valueOf(czasStartu.getCurrentMinute())+"-"
	    	+String.valueOf(czasKonca.getCurrentHour())+":"+String.valueOf(czasKonca.getCurrentMinute())+"             "+editText.getText());	    	
	    	for(int i=0;i<list.size();i++){
	    		 przetworzStringa(list.get(i));
	    		 opisZadania="";
	    		if((czasStartu.getCurrentHour()<Integer.valueOf(godzinaStartu))||((czasStartu.getCurrentHour()==Integer.valueOf(godzinaStartu))&&(czasStartu.getCurrentMinute()<Integer.valueOf(minutaStartu)))){
	    			czyWczesniej=true;
	    			list.add(i,zadanie);
	    			break;
	    		}
	    	} 
	    	if(czyWczesniej==false)
	    	list.add(zadanie);
	    	adapter = new StableArrayAdapter(context,android.R.layout.simple_list_item_1, list);
	    	ZapisDoPliku(list,name);
	    	listview.setAdapter(adapter);
	    	((ViewGroup)view.getParent()).removeView(view);
	    	
	       }  }
	     });  
    oknoZadania.setNegativeButton("Cofnij", Clickacz);	         
    listview.setAdapter(adapter);
    /*
     * Metoda ustawiajaca obsluge klikania w przycisk "dodaj"
     * - wyswietla okno zadania
     * 
     */
    addButton.setOnClickListener(new Button.OnClickListener(){
    	  @Override
    	  public void onClick(View arg0) {
    		  oknoZadania.setNegativeButton("Cofnij", Clickacz);
    		  oknoZadania.show(); 
    		  
    	  }});
    
    locationButton.setOnClickListener(new Button.OnClickListener() {
    	
  	  @Override
  	  public void onClick(View arg0) {
  		  if(isConn()) startActivity(new Intent(ListaZadan.this, MapActivity.class));
  		  else Toast.makeText(ListaZadan.this, "Potrzebne polaczenie z internetem", Toast.LENGTH_LONG).show();
  	  }});
    /*
     * Metoda ustawiajaca obsluge klikania na poszczegolne elementy listy
     * - przetwarza stringa z zadaniem
     * - ustawia parametry okna zadania
     * - wyswietla okno zadania
     * - usuwa klikniety element z listy
     * 
     */
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, final View view,
        int position, long id) {
        final String item = (String) parent.getItemAtPosition(position);
        view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
              @Override
              public void run() {
            	    przetworzStringa(item);
            	    czasStartu.setCurrentHour(Integer.valueOf(godzinaStartu));
            	    czasStartu.setCurrentMinute(Integer.valueOf(minutaStartu));
            	    czasKonca.setCurrentHour(Integer.valueOf(godzinaKonca));
            	    czasKonca.setCurrentMinute(Integer.valueOf(minutaKonca));
    	    		editText.setText(opisZadania);
    	    		oknoZadania.setNegativeButton("Usun", Clickacz);
    	    		oknoZadania.show();
            	    opisZadania="";
            	    list.remove(item);
            	    view.setAlpha(1);
              }
              
            });  
      }
    });
  }
  
	public boolean isConn() { //sprawdzenie czy jest połączenie z internetem
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
	
  /**
   * Metoda odpowiedzialna za sprawdzenie czy istnieje plik o podanej jako parametr nazwie
   * 
   * @param nazwa
   * @return czyJestPlik
   */
  public boolean CzyJestPlik(String nazwa){
	  final Context context= this.getApplicationContext();
	  isFile=true;
	  try{   
	      FileInputStream fIn = context.openFileInput(nazwa);
	      DataInputStream in = new DataInputStream(fIn);
	      if(in!=null)
	      in.close(); 
	  }
	 catch(Exception e){
		  isFile=false;
   	  }
	  System.out.println(isFile);
	  return isFile;	  
  }
  /**
   * Metoda odpowiedzialna za stworzenie nowego pliku, do ktorego bedzie zapisywana lista zadan
   * 
   * @param nazwa
   */
  public void StworzNowyPlik(String nazwa){
		try {
			nowyPlik=(new File(getFilesDir()+File.separator+nazwa));
			nowyPlik.createNewFile();
			} catch (IOException e) {
			e.printStackTrace();
			}  
  }
  /**
   * Metoda odpowiedzialna za wczytanie listy z pliku do zmiennej typu ArrayList
   * 
   * @param nazwa
   */
  public void WczytajListeZPliku(String nazwa){
		File istniejacyPlik = (new File(getFilesDir()+File.separator+nazwa));
		Scanner in = null;
		try {
			in = new Scanner(istniejacyPlik);
		} catch (FileNotFoundException e) {
System.out.println("tutaj");
			e.printStackTrace();
		}
		int iteratorListy=0;
		while(in.hasNextLine()==true){
		list.add(iteratorListy,in.nextLine());
		iteratorListy++;
		}
	}
  /**
   * Metoda odpowiedzialna za wyluskanie parametrow zadania ze stringa:
   * - godzina startu
   * - minuta startu
   * - godzina konca
   * - minuta konca
   * - opis zadania
   * 
   * @param zadanie
   */
  public void przetworzStringa(String zadanie){
	  String przedzialCzasowyZadania="";
		int koniecGodzinyStartuWStringu = 0;
		int koniecMinutyStartuWStringu = 0;
		int koniecGodzinyKoncaWStringu = 0;
		 for(int i=0;i<zadanie.length();i++){
	 		
			  if(zadanie.charAt(i)!=' '){
				  przedzialCzasowyZadania+=zadanie.charAt(i);
			  }
			  else break;  		  
		  }
		 for(int i=0;i<przedzialCzasowyZadania.length();i++){
			  if(przedzialCzasowyZadania.charAt(i)==':'){
				  if(koniecGodzinyStartuWStringu==0)koniecGodzinyStartuWStringu=i;
				  else koniecGodzinyKoncaWStringu=i;
				  
			  }
			  if(przedzialCzasowyZadania.charAt(i)=='-'){koniecMinutyStartuWStringu=i;}	  
		  }
		  int temp=0;
		  for(int i=przedzialCzasowyZadania.length()+5;i<zadanie.length();i++){
			  if(temp==0)
			  {
			  if(zadanie.charAt(i)!=' '){
				 opisZadania+=zadanie.charAt(i);
				 temp=1;  
			  }}
			  else  opisZadania+=zadanie.charAt(i);  
		  }
		  godzinaStartu=przedzialCzasowyZadania.substring(0, koniecGodzinyStartuWStringu);
		  minutaStartu=przedzialCzasowyZadania.substring(koniecGodzinyStartuWStringu+1, koniecMinutyStartuWStringu);
		  godzinaKonca=przedzialCzasowyZadania.substring(koniecMinutyStartuWStringu+1, koniecGodzinyKoncaWStringu);
		  minutaKonca=przedzialCzasowyZadania.substring(koniecGodzinyKoncaWStringu+1, przedzialCzasowyZadania.length());
		  przedzialCzasowyZadania="";	  
	}
  /**
   * 
   * Metoda odpowiedzialna za zapis listy do pliku
   * @param list
   * @param nazwa
   */
  public void ZapisDoPliku(ArrayList<String> list,String nazwa){
	  BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(new 
			File(getFilesDir()+File.separator+nazwa)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i=0;i<list.size();i++){
			try {
				bufferedWriter.write(list.get(i).toString());
				bufferedWriter.newLine();
				} catch (IOException e) {
				e.printStackTrace();
				}}
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  
  }
  /**
   * Klasa sluzaca do obslugi klikania na poszczegolne elementy listy
   * 
   * @author Dawid
   *
   */
  private class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
        List<String> objects) {
      super(context, textViewResourceId, objects);
      for (int i = 0; i < objects.size(); ++i) {
        mIdMap.put(objects.get(i), i);
      }
    }

    @Override
    public long getItemId(int position) {
      String item = getItem(position);
      return mIdMap.get(item);
    }

  }


} 