/*******************************************************************************

 @file			MyDialogFragment.java
 @abstract		Définition de la classe MyDialogFragment pour ouvrir les fenêtres
  				de création de groupe et de note de frais.
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyDialogFragment extends DialogFragment {
	
	// ATTRIBUTS
	private int dialog_id;								// id de al boite a dialog
	private String nameGroup;							// nom du groupe pour ce fragment
	private String nameBill;							// nom de la note de frais pour ce fragment
	private EditText newMemName;						// nom du nouveau membre en création
	private EditText newGroupName;						// nom du groupe membre en création
	private EditText newBillName;						// nom de la nouvelle note en création
	private ArrayAdapter<String> mListDatamem;			// liste des membres créés dans le groupe 
	private ArrayAdapter<String> adapterSpinner;		// liste déroulante des membres pour selectionner celui qui a payé
	private static ListView lvListmem;					// ListView des membres
	private Group newGroup;								// groupe en création
	private Bill newBill;								// note en création
	private DataDbHelper db;							// base de donnée de l'application
	private EditText newBillValue;						// valeur de la nouvelle note en création
	private static final int ID_DIALOG_NEWGROUP = 1000;	// variable de dialogue informant de la création d'un nouveau groupe
	private NoticeDialogListener mListener;				// Listener pour ?????
	private Spinner spinner;							// Spiner pour la liste déroulante des membres


	// METHODES
	/*******************************************************************************
	 @function		NoticeDialogListener
	 @abstract		Méthode pour ?????
	*******************************************************************************/
    public interface NoticeDialogListener {
        public void onDialogSaveClick(View v);
    }
	
	/*******************************************************************************
	 @function		NoticeDialogListener
	 @abstract		Méthode pour mettre en place l'événement qui declenche le methode
	  				implmenté par le host 
	 @param			Activity activity
	*******************************************************************************/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

	/*******************************************************************************
	 @function		onCreateView
	 @abstract		Méthode de création de View
	 @return		retourne la vue créée
	*******************************************************************************/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		View v;											// la vue
		db = new DataDbHelper(this.getActivity());		// la base de donnée de l'application
		dialog_id = getArguments().getInt("DIALOG_ID");	// l'id de la fenetre

		// si l'id de la fenetre correspond à celui de la création d'un nouveau groupe
		if (dialog_id == ID_DIALOG_NEWGROUP){
			// ouverture de la vue en conséquence
			v = inflater.inflate(R.layout.dialog_newgroup, container);
//	        dialog.setContentView(R.layout.dialog_newgroup);
	        // réccupération du nom du groupe
	        nameGroup = getArguments().getString("NEW_GROUP_NAME");
	        
	        // s'il n'y a pas de nom entré au préalable
	        if(nameGroup.equalsIgnoreCase("")){
	        	// mettre le nom par défaut
	        	nameGroup = getString(R.string.new_group);
	        }
	        // définition du titre
		    getDialog().setTitle(nameGroup);
		    // création du nouveau goupe
		    newGroup = new Group(nameGroup);
	        
	        // mise en place des boutons
		    Button btnAddmem = (Button) v.findViewById(R.id.button_add);	// bouton ajout de membre
		    Button btnSave = (Button) v.findViewById(R.id.button_save);		// bouton pour sauvegarder le groupe
		    
	        // mise en place des textes modifiables
	        newMemName = (EditText) v.findViewById(R.id.new_member);		// nom du membre à ajouter
	        newGroupName = (EditText) v.findViewById(R.id.row_title);		// nom du nouveau groupe (pour modifier)
	        newGroupName.setText(nameGroup);
	        
	        // liste des membres créés
	        lvListmem = (ListView) v.findViewById(R.id.listView1);
	        
	        // adaptateur pour la liste des membres créés
	        mListDatamem = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1);
	        
	        //Set Adapter to list
	        lvListmem.setAdapter(mListDatamem);
	        
	        // mise en place d'un listener pour le bouton d'ajout de membre
	        btnAddmem.setOnClickListener(new OnClickListener(){
	        	// lorsque l'on click sur ce bouton :
	        	@Override
	        	public void onClick(View v){
	        		// on ajoute le membre au groupe
	        		onAddMember2Group();
	        		// cache le clavier
	        		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        		imm.hideSoftInputFromWindow(newMemName.getWindowToken(), 0);
	        	}
	        });
			
	        // mise en place d'un listener pour le bouton de sauvegarde du groupe
	        btnSave.setOnClickListener(new OnClickListener(){
	           @Override
	           public void onClick(View v) {
	        	   // si aucun membre n'a été ajouté :
	        	   if(newGroup.getList_members().isEmpty()){
	        		   // informer l'utilisateur que le groupe ne sera pas créé
	        		   Toast.makeText(getActivity(), "This group has no members", Toast.LENGTH_SHORT).show();
	        	   // sinon :
	        	   } else {
	        		// réccupère le nom entré et le donne au groupe
	        		newGroup.setNameGp(newGroupName.getText().toString());
	        		// entre les données du groupe dans la base de données
	        	   	db.insertGroup(newGroup);
	        	   	// active l'evenement pour envoyer des information au host
	        	   	mListener.onDialogSaveClick(v);
	        	   	// ferme la fenêtre
	        	   	dismiss();
	        	   	
	        	   }
	           }
	       });
		} else {
			// ouverture de la vue en conséquence
			v = inflater.inflate(R.layout.dialog_newbill, null);
			
			// réccupération du nom de la note
	        nameBill = getArguments().getString("NEW_BILL_NAME");
	        // s'il n'y a pas de nom entré au préalable
	        int groupId = getArguments().getInt("EXTRA_ID_GROUP");
	       
	        if(nameBill.equalsIgnoreCase("")){
	        	// mettre le nom par défaut
	        	nameBill = getString(R.string.new_bill);
	        }

	        newGroup = db.getGroup(groupId);
	      
	        // création de la nouvelle note
		    getDialog().setTitle(nameBill);
		    newBill = new Bill(nameBill, -1, newGroup.getNb_members());
	        
	        // mise en place du bouton de sauvegarde de note
		    Button btnSave = (Button) v.findViewById(R.id.Bbutton_save);
	        // mise en place des textes modifiables
	        newBillName = (EditText) v.findViewById(R.id.Brow_title);	// nom de la nouvelle note (pour modifier)
	        newBillValue = (EditText) v.findViewById(R.id.Bcurrent);	// valeur de la note à entrer
	        newBillName.setText(nameBill);
	        
	        // liste des membres du groupe de la note
	        lvListmem = (ListView) v.findViewById(R.id.BlistView1);
	        
	        // adaptateur pour la liste des membres du groupe de la note
	        mListDatamem = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice,newGroup.membersToString());

	        //Set Adapter to list
	        lvListmem.setAdapter(mListDatamem);
	        lvListmem.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);	// liste cochable

	        // création du spinner
	        spinner = (Spinner) v.findViewById(R.id.Bspinner1);
	        // adaptateur utilisant un layout de spinner par defaut
	        adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,newGroup.membersToString());
	        // specification du layout à utiliser quand la liste de choix apparait
	        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        // applique l'adaptateur au spinner
	        spinner.setAdapter(adapterSpinner);
	        
	        // mise en place d'un listener pour le cochage des membres participants à la note
	        lvListmem.setOnItemClickListener(new OnItemClickListener(){
	        	// lorsque l'on click sur un nom :
	        	@Override
	        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
	        		// on modifie whos_in à la position correspondante
	        		newBill.memberIn(position);	        		
	        	}
	        });
	        
	        
	        // mise en place d'un listener pour le bouton de sauvegarde de la note
	        btnSave.setOnClickListener(new OnClickListener(){
	        
	        	public void onClick(View v) {
	        		// du spinner oin récupère who_paid
	        		spinner = (Spinner) getView().findViewById(R.id.Bspinner1);
	        		// si la valeur de la note n'a pas été insérée
	        	   if(newBillValue.getText().toString().isEmpty()){
	        		   // informer l'utilisateur que la note ne sera pas créée
	        		   Toast.makeText(getActivity(), "Enter a value over 0", Toast.LENGTH_SHORT).show();
	        		// sinon :
	        	   	} else {
	        	   		// récupération des données pour contruire la note
	        	   		newBill.setValue(Float.valueOf(newBillValue.getText().toString()));
	        	   		newBill.setId(newGroup.getList_bills().size()+1);
	        	   		newBill.setNb_members(newGroup.getNb_members());
	        	   		newBill.setWho_paied(newGroup.getList_members().get(spinner.getSelectedItemPosition())) ;
	        	   		newBill.setWhat(newBillName.getText().toString());
	        	   		// ajout de la note au groupe
	        	   		newGroup.billAdd(newBill);
	        	   		// et à la base de donnée
	        	   		db.insertBill(newBill, newGroup.getId());
	        	   		db.updateGroup(newGroup);
	        		   	// appelle la fonction qui fait le canal de comunication avec l'activite arent
	        	   		mListener.onDialogSaveClick(v);
	        		   	// ferme la fenêtre
		        	   	dismiss();
	        	   }
	        	   

	           }
	       });
		}
			
		return v;
	}
	
	/*******************************************************************************
	 @function		onAddMember2Group
	 @abstract		Méthode pour ajouter des membre au groupe
	*******************************************************************************/
	protected void onAddMember2Group(){
		String name;	// variable de sauvegarde du nom
		
		// récupération du nom du nouveau membre
        name = this.newMemName.getText().toString();
        // si le nom est vide, en informer l'utilisateur
        if(name.isEmpty()){
        	
        	Toast.makeText(getActivity(), "Name of member empty", Toast.LENGTH_SHORT).show();
        // sinon	
        } else {
        	// ajout du nouveau membre au groupe
	        newMemName.setText("");
	        Member newMember = new Member(name,newGroup.getNb_members()+1);
	        newGroup.memberAdd(newMember);
	        
			// ajouter le nom du membre dans la liste des membres
	    	this.mListDatamem.add(name);
	    	// mettre à jour l'affichage de cette liste
	    	mListDatamem.notifyDataSetChanged();
        }
    }

}
