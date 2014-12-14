package com.example.menedzerzadan;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import com.example.menedzerzadan.R;
/**
 * Klasa bedaca pierwsza aktywnoscia.
 * Glownym zadaniem klasy jest pobranie daty, wyslanie jej do nastepnej aktywnosci oraz przejscie do nastepnej aktywnosci
 * @author Dawid Plichta
 *
 */
public class MenedzerZadan extends Activity { 

	@Override 
	/**
	 * Metoda odpowiedzialna na wyswietlenie kalendarza i dwoch przyciskow (dalej i wyjdz)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_entry);
		final Intent intent = new Intent(MenedzerZadan.this, ListaZadan.class);
		final Bundle b=new Bundle();
		final DatePicker datePicker = (DatePicker) this.findViewById(R.id.datePicker1);
		final Button nextButton=(Button) this.findViewById(R.id.button2);
	  	final Button exitButton=(Button) this.findViewById(R.id.button1);
		nextButton.setOnClickListener(new Button.OnClickListener(){
			/*
			 * Metoda obsluguj�ca klikni�cie na przycisk "dalej".
			 * Jej zadania to: 
			 * - pobranie ustawionej na kalendarzu daty
			 * - wpisanie jej w odpowiedniej postaci do stringa
			 * - wyslanie stringa do nastepnej aktywnosci 
			 * - przejscie do nastepnej aktywnosci
			 * 
			 */ 
         	  @Override 
         	  public void onClick(View arg0) {
         		  String value = String.valueOf(datePicker.getMonth())+"-"+String.valueOf(datePicker.getDayOfMonth());
 		          b.putString("name", value);
 		          intent.putExtras(b);
 	              startActivity(intent);             		   
         	  }});
			/* 
			 * Metoda obsluguj�ca klikni�cie na przycisk "wyjdz"
			 * Jej zadaniem jest wyjscie z aplikacji
			 * 
			 */
		     exitButton.setOnClickListener(new Button.OnClickListener(){
		          @Override
		      	  public void onClick(View arg0) {
	        	  System.exit(0);         		   
		          }});
	}

}
