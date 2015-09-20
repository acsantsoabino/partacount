/*******************************************************************************

 @file			MainActivity.java
 @abstract		Définition de la classe MainActivity pour gérer l'activité 
 				d'accueil
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

//IMPORTS
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements MyDialogFragment.NoticeDialogListener{
	
	//ATTRIBUTS
	private CustomArrayAdapter<Group> mListData;		// Liste à afficher des groupes
	private ArrayList<Group> mGroups;					// Array List des groupes
	private static EditText newGroupName;				// Nom du nouveau groupe
	private ListView lvList;							// ListeView des groupes
	private int index;									// Index dans la liste
	private static final int ID_DIALOG_NEWGROUP = 1000;	// variable de dialogue informant de la création d'un nouveau groupe
	private static final int ID_DIALOG_ALERTDELET = 0;	// variable de dialogue informant de la suppression d'un groupe de la liste
	private DataDbHelper db;							// base de données

	//METHODES
	/*******************************************************************************
	 @function		onCreate
	 @abstract		Méthode pour créer l'activité d'acceuil
	 @param			Bundle savedInstanceState ?????
	*******************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {	
    	
    	int i;	// variable de création de boucle
    	
    	// ?????
        super.onCreate(savedInstanceState);	
         setContentView(R.layout.activity_main);
        
        // initialisation des Attributs
        mGroups = new ArrayList<Group>();
        db = new DataDbHelper(this);
        mGroups = db.getListGroups();
        for(i = 0; i<mGroups.size(); i++) {
        	Log.d("GROUP:", mGroups.get(i).getNameGp());
        }
        
        // mise en place du bouton Add pour ajouter un groupe
        Button btnAdd = (Button)findViewById(R.id.button1);
        // mise en place d'une entrée texte pour y écrire le nom du nouveau groupe
        newGroupName = new EditText(this);
        newGroupName = (EditText)findViewById(R.id.new_group);
        // ListView pour afficher tous les groupes crées
        lvList = (ListView)findViewById(R.id.list_groups);
        
        // création de l'adapter
        mListData = new CustomArrayAdapter<Group>(this,mGroups);
        // on lie l'adaptater à la list
        lvList.setAdapter(mListData);
        
        // mise en place d'un listener pour le bouton d'ajout de groupe
        btnAdd.setOnClickListener(new OnClickListener(){
        	
        	// lorsque l'on click sur ce bouton :
        	@Override
        	public void onClick(View v){
        		// on ouvre la boite de dialogue de création de groupe
        		showMyDialog(v, newGroupName.getText().toString());
        	}
        });
        
        // mise en place d'un listener pour click long sur un des éléments de la liste pour supprimer un groupe
        lvList.setOnItemLongClickListener(new OnItemLongClickListener(){
        	
        	// lorsqu'on reste appuyé :
        	@SuppressWarnings("deprecation")
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
        		// on réccupère la position dans la liste
        		index = position;
        		// boite de dialogue pour confirmer la suppression
        		showDialog(ID_DIALOG_ALERTDELET);
				return true;
        	}
        });
        // mise en place d'un listener pour click sur un des éléments de la liste pour afficher un groupe
        lvList.setOnItemClickListener(new OnItemClickListener(){
        	// lorsque l'on click sur un élément de la liste :
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		// on ouvre l'activité de gestion de groupe
        		openGroup(view, mGroups.get(position));
        	}
        });
    }
    /*******************************************************************************
	 @function		onCreateDialog
	 @abstract		Méthode pour créer la boite de dialogue de suppression de groupe
	 @param			int id du groupe à supprimer
	*******************************************************************************/
    @Override
    public Dialog onCreateDialog (int id) {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        if(id == ID_DIALOG_ALERTDELET){
        	// fenetre de confirmation
            builder.setMessage("Do you want to delete this group?")
            		// bouton OK pour confirmer
                   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                	   // si on click sur OK
                       public void onClick(DialogInterface dialog, int id) {
                    	   //suppression du groupe de la bdd
                           db.deleteGroup(mGroups.get(index));
                           // suppression du groupe de la liste
                           mListData.removeItem(index);
                           // mise à jour de l'affichage
                           mListData.notifyDataSetChanged();
                           Log.d("MAIN:","sizeof mGroup is "+ mGroups.size());
                           // fermeture de la boite de dialogue
                           dialog.dismiss();
                       }
                   })
                   // bouton CANCEL pour annuler
                   .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                	   // si on click sur cancel :
                       public void onClick(DialogInterface dialog, int id) {
                    	   // fermeture de la boite de dialogue
                           dialog.cancel();
                       }
                   });
        }
        
        return builder.create();
    }
    /*******************************************************************************
	 @function		openGroup
	 @abstract		Méthode pour ouvrir l'activité du group
	 @param			View view
	 @param			Group group, group qui sera affiché
	*******************************************************************************/
    public void openGroup(View view, Group group){
    	Intent intent = new Intent(this,GroupActivity.class);
    	intent.putExtra("EXTRA_ID_GROUP", group.getId());
       	startActivity(intent);
    }
    
    /*******************************************************************************
	 @function		showMyDialog
	 @abstract		Méthode pour ouvrir la fenetre de création de groupe
	 @param			View view
	 @param			String nameGroup nom du groupe à créer
	*******************************************************************************/
	public void showMyDialog(View v, String nameGroup){
		
		FragmentManager manager = getFragmentManager();
		MyDialogFragment dialog = new MyDialogFragment();
		Bundle args = new Bundle();
		args.putString("NEW_GROUP_NAME", nameGroup);
		args.putInt("DIALOG_ID", ID_DIALOG_NEWGROUP);
		dialog.setArguments(args);
		dialog.show(manager, "NewGroup");
		
	}

	@Override
	public void onDialogSaveClick(View v) {
		mGroups = db.getListGroups();
		mListData = new CustomArrayAdapter<Group>(v.getContext(),mGroups);
		lvList.setAdapter(mListData);
		newGroupName.setText("");
		Toast.makeText(this, "New group saved", Toast.LENGTH_SHORT).show();	
	}

}
