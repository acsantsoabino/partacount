/*******************************************************************************

 @file			CustomArrayAdapter.java
 @abstract		Définition de la classe CustomArrayAdapter pour définir une 
 				ArrayList personnalisée.
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

// IMPORTS JAVA
import java.util.ArrayList;

// IMPORTS ANDROID
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("hiding")
public class CustomArrayAdapter<Object> extends ArrayAdapter<Object> {

	// ATTRIBUTS
	protected final Context context;	// contexte de la liste
    private ArrayList<Object> data;		// liste d'objets qui remplira la listview
    int i = 0;							// index
    
    // CONSTRUCTEUR
	public CustomArrayAdapter(Context context, ArrayList<Object> data) {
		super(context, R.layout.list_layout);
        this.context = context;
        this.data = new ArrayList<Object>();
        this.data = data;
	}
	
	//GETTERS ET SETTERS
    @Override
    public int getCount() {
        return data.size();
    }
 
    // METHODES
    /*******************************************************************************
	 @function		getItem
	 @abstract		Méthode pour prendre l'élément d'une position de la liste
	 @param			int position dans la liste, de l'élément à desiré
	*******************************************************************************/
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    /*******************************************************************************
	 @function		removeItem
	 @abstract		Méthode pour enlever un élément de la liste
	 @param			int position dans la liste, de l'élément à retirer
	*******************************************************************************/
    public void removeItem(int position){
    	data.remove(position);
    }
    
    /*******************************************************************************
	 @function		getView
	 @abstract		Méthode pour définir la View pour notre ArrayList
	 @param			int position
	 @param			View convertView
	 @param			ViewGroup parent
	 @return		View rowView, return la ligne de la liste view
	*******************************************************************************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
    	
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	View rowView = inflater.inflate(R.layout.list_layout, parent, false);
    	TextView textView1 = (TextView) rowView.findViewById(R.id.row_title);
    	TextView textView2 = (TextView) rowView.findViewById(R.id.row_coment);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        
        // Pour definir la View de l'ArrayList des groupes 
        if(this.getItem(position).getClass()==Group.class){
        	
        	Group tempGroup = (Group)this.getItem(position);		// Pour l'élément correspondant de la liste :
	        imageView.setImageResource(R.drawable.group_icon);		// insertion d'une image
	        textView1.setText(tempGroup.getNameGp());				// nom du groupe en titre
	        textView2.setText("Balance: $"+String.format("%.2f",tempGroup.getBalance()));	// total dépensé en sous titre
	        
	    // Pour definir la View de l'ArrayList des notes de frais 
        } else if(this.getItem(position).getClass()==Bill.class){
        	
        	Bill tempBill = (Bill)this.getItem(position);			// Pour l'élément correspondant de la liste :
	        imageView.setImageResource(R.drawable.group_icon);		// insertion d'une image
	        textView1.setText(tempBill.getWhat());					// nom de la note en titre
	        textView2.setText(tempBill.getWho_paied().getNameM()+" : $"+String.format("%.2f",tempBill.getValue())); // qui a payé et valeur de la note en sous titre
	        
	    // Pour definir la View de l'ArrayList des membres
        } else if(this.getItem(position).getClass()==Member.class){
        	
        	Member tempMember = (Member)this.getItem(position);			// Pour l'élément correspondant de la liste :
	        imageView.setImageResource(R.drawable.contact_picture);		// insertion d'une image
	        textView1.setText(tempMember.getNameM());					// nom du membre en titre
	        textView2.setText("Balance : $"+String.format("%.2f",tempMember.getDebt()));		// ce qu'il doit encore au groupe en sous titre
	        
        }
        
    	return rowView;
    }
    
}
