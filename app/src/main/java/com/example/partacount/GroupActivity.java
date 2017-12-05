/*******************************************************************************

 @file			Bill.java
 @abstract		Définition de la classe Bill pour gérer les notes de frais
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

// IMPORTS
import java.util.ArrayList;
import java.util.Locale;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentTransaction;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupActivity extends AppCompatActivity implements
		ActionBar.TabListener, MyDialogFragment.NoticeDialogListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	// ATTRIBUTS
	private Group group;										// Groupe auquel correspond l'activitée
	private static ListView dummyListViewBill;					// ListView des notes de frais
	private static ListView dummyListViewMem2;					// ListView des membres
	private static EditText dummyEditText;						// Champs d'entrée de texte
	private static ArrayAdapter<String> adapter;				// ArrayAdapter pour l'affichage les noms des membres
	private static CustomArrayAdapter<Bill> mListData1;			// ArrayAdapter personnalisé pour l'affichage des notes
	private static CustomArrayAdapter<Member> mListData2;		// ArrayAdapter personnalisé pour l'affichage des membres
	private static ArrayAdapter<String> mListData3;				// ArrayAdapter personnalisé pour l'affichage des listes
	private static final int ID_DIALOG_NEWBILL = 2000;			// variable de dialogue informant de l'ajout nouvelle note
	private static final int ID_DIALOG_ALERTNOTDELET_MEMBER = 7;// variable de dialogue informant de la non suppression d'un membre
	private static final int ID_DIALOG_ALERTDELET_MEMBER = 6;	// variable de dialogue informant de la suppression d'un membre
	private static final int ID_DIALOG_ALERTDELET_BILL = 5;		// variable de dialogue informant de la suppression d'une note
	private static final int ID_DIALOG_DETAIL_BILL = 8;			// variable de dialogue informant de la demande de détail d'une note
	private static int index;									// index de positionnement dans une liste.
	private static DataDbHelper db;								// base de donnée de l'application

	// METHODES
	/*******************************************************************************
	 @function		onCreate
	 @abstract		Méthode pour creer l'activité
	 @param			Bundle savedInstanceState
	*******************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        final ActionBar actionBar = getSupportActionBar();

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_group);
		int id;
		
		DataDbHelper db = new DataDbHelper(this);
		
		Intent intent = this.getIntent();
		id = intent.getIntExtra("EXTRA_ID_GROUP",1);
    	group = db.getGroup(id);
		// Set up the title.
    	this.setTitle(group.getNameGp());

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Set up the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(
		        new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
					    actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
	}

	/*******************************************************************************
	 @function		onTabSelected
	 @abstract		Méthode pour créer plusieurs onglets pour l'activitée
	 @param			ActionBar.Tab tab pour les onglets
	 @param			FragmentTransaction fragmentTransaction
	*******************************************************************************/
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	// TODO
	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	// TODO
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		// CONSTRUCTEUR
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		// METHODES
		/*******************************************************************************
		 @function		getItem
		 @abstract		Méthode pour renvoyer le fragment à la position correspondante
		 @param			int position
		 @return		Fragment correspondant à la position
		*******************************************************************************/
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putInt("EXTRA_ID_GROUP",group.getId());
			fragment.setArguments(args);
			return fragment;
		}
		/*******************************************************************************
		 @function		getCount
		 @abstract		Méthode pour savoir le nombre de pages
		 @return		int correspondant au nombre de pages
		*******************************************************************************/
		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		/*******************************************************************************
		 @function		getPageTitle
		 @abstract		Méthode pour définir le titre de la page correspondante à la
		 				position données
		 @param			int position, position de la page
		 @return		CharSequence correspondant au titre de la page 
		*******************************************************************************/
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			// si page 1
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			// si page 2
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			// si page 3
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		//ATTRIBUTS
		private int section;	// section
		private Group group;	// groupe correpondant
		
		public static final String ARG_SECTION_NUMBER = "section_number";	// tag pour envoyer le numero de la section dans l'instance

		//CONSTRUCTEUR
		public DummySectionFragment() {
		}

		//METHODES
		/*******************************************************************************
		 @function		onCreateView
		 @abstract		Méthode pour retourner la vue correpondante à la section
		 @return		View 
		*******************************************************************************/
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.activity_main,container, false);
			// récupération de la base de donnée
			db = new DataDbHelper(this.getActivity());
			// récupération du groupe dans la base de données
			group = db.getGroup(getArguments().getInt("EXTRA_ID_GROUP"));
			// récupération de la section (1, 2 ou 3)
			section = getArguments().getInt(ARG_SECTION_NUMBER);
			
			// définition de la vue correspondante à la section
			switch (section){
				case 1:
					rootView = inflater.inflate(R.layout.activity_main,container, false);
					dummyListViewBill = (ListView) rootView.findViewById(R.id.list_groups);
					mListData1 = new CustomArrayAdapter<Bill>(this.getActivity(), group.getList_bills());
					dummyListViewBill.setAdapter(mListData1);
					break;
				case 2:
					rootView = inflater.inflate(R.layout.member_list,container, false);
					dummyListViewMem2 = (ListView) rootView.findViewById(R.id.list_member);
					mListData2 = new CustomArrayAdapter<Member>(getActivity(), group.getList_members());
					dummyListViewMem2.setAdapter(mListData2);
					break;
				case 3:
					rootView = inflater.inflate(R.layout.dialog_newgroup,container, false);
					break;
			}
			
			return rootView;
		}
		/*******************************************************************************
		 @function		onActivityCreated
		 @abstract		Méthode pour définir les vues
		 @param			Bundle savedInstanceState
		*******************************************************************************/
		public void onActivityCreated(Bundle savedInstanceState){
			
			super.onActivityCreated(savedInstanceState);
			db = new DataDbHelper(this.getActivity());
			// récupération de l'ID du groupe correspondant
			group = db.getGroup(getArguments().getInt("EXTRA_ID_GROUP"));
			// mise en place d'un bouton
			Button dummyButton = (Button) getView().findViewById(R.id.button1);
			// mise en place d'un texte éditable
			dummyEditText = (EditText) getView().findViewById(R.id.new_group);
		
			// pour la section 1
			if(section == 1) {
				// changement du texte par défaut du texte éditable
				dummyEditText.setHint(R.string.new_bill);
				// mise en place de la liste de notes
				CustomArrayAdapter<Bill> mListData1 = new CustomArrayAdapter<Bill>(this.getActivity(), group.getList_bills());
				dummyListViewBill.setAdapter(mListData1);
				// mise en place d'un listenner sur le bouton
				dummyButton.setOnClickListener(new OnClickListener(){
		        	
		        	@Override
		        	public void onClick(View v){
		        		// on récupère le nom de la note
		        		EditText dummyEditText = (EditText) getView().findViewById(R.id.new_group);
		        		// on ouvre la boite de dialogue de création de note
		        		showDialog(v,dummyEditText.getText().toString());
		        	
		        	}
		        });
				// mise en place d'un listener sur la liste de note
				dummyListViewBill.setOnItemClickListener(new OnItemClickListener(){
		        	// lorsqu'on appuie longtemps :
		        	@SuppressWarnings("deprecation")
		        	@Override
		        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		        		index = position;
		        		// on ouvre la boite de dialogue de confirmation avant de supprimer la note
		        		getActivity().showDialog(ID_DIALOG_DETAIL_BILL);
		        	}
		        });
				
				dummyListViewBill.setOnItemLongClickListener(new OnItemLongClickListener(){
		        	// lorsqu'on appuie longtemps :
		        	@SuppressWarnings("deprecation")
		        	@Override
		        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
		        		index = position;
		        		// on ouvre la boite de dialogue de confirmation avant de supprimer la note
		        		getActivity().showDialog(ID_DIALOG_ALERTDELET_BILL);
						return true;
		        	}
		        });
			}
			// pour la section 2
			if(section == 2) {
				// mise en place de la liste de membres
				mListData2 = new CustomArrayAdapter<Member>(this.getActivity(), group.getList_members());
				dummyListViewMem2.setAdapter(mListData2);
				// mise en place d'un listener sur la liste de membres
				dummyListViewMem2.setOnItemLongClickListener(new OnItemLongClickListener(){
					// lorsqu'on appuie longtemps :
					@SuppressWarnings("deprecation")
					@Override
		        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
		        		index = position;
		        		group = db.getGroup(group.getId());
		        		// si le membre est impliqué dans des additions :
		        		if (group.getList_members().get(index).getBills_count() != 0) {
		        			// on ouvre une boite de dialogue informant qu'il ne sera pas supprimé
		        			getActivity().showDialog(ID_DIALOG_ALERTNOTDELET_MEMBER);
		        		} else {
		        			// on ouvre une boite de dialogue de confirmation avant de supprimer la note
		        			getActivity().showDialog(ID_DIALOG_ALERTDELET_MEMBER);
		        		}
						return true;
		        	}
		        });

			} else if (section == 3) {
				//lancement du fragment 3
				Fragment3();
			}
		
		}
		/*******************************************************************************
		 @function		showDialog
		 @abstract		Méthode pour définir la boite de dialogue de création de note
		 @param			View v, vue dans laquelle on ouvre la boite
		 @param			String billName, nom de la nouvelle note que l'on va créer
		*******************************************************************************/
		public void showDialog(View v, String billName){
			
			android.app.FragmentManager manager = getActivity().getFragmentManager();
			MyDialogFragment dialog = new MyDialogFragment();
			Bundle args = new Bundle();
			args.putString("NEW_BILL_NAME", billName);
			args.putInt("EXTRA_ID_GROUP", group.getId());			
			args.putInt("DIALOG_ID", ID_DIALOG_NEWBILL);
			dialog.setArguments(args);
			dialog.show(manager, "NewGroup");
			
		}
		/*******************************************************************************
		 @function		Fragment3
		 @abstract		Méthode pour définir le fragment de modification de groupe
		*******************************************************************************/
		public void Fragment3(){
			
//			ArrayAdapter<String> mListData3;

    		group = db.getGroup(group.getId());
			// mise en place des boutons
		    Button btnAddmem = (Button) getView().findViewById(R.id.button_add);
		    Button btnSave = (Button) getView().findViewById(R.id.button_save);
		    
	        // mise en place d'un texte éditable
		    EditText newGroupName = (EditText) getView().findViewById(R.id.row_title);
	        newGroupName.setText(group.getNameGp());
	        
	        // mise en place d'une liste de membre
	        ListView dummyListViewMem3 = (ListView) getView().findViewById(R.id.listView1);
	        // création de l'adaptateur
	        mListData3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,group.membersToString());
	        // association de l'adaptateur à la liste
	        dummyListViewMem3.setAdapter(mListData3);

	        // mise en place d'un listener sur le bouton pour ajouter un membre
	        btnAddmem.setOnClickListener(new OnClickListener(){
	        	
	        	@Override
	        	public void onClick(View v){
	        		String name;
	        		EditText newMemName = (EditText) getView().findViewById(R.id.new_member);
	        		// on récupère le texte
	                name = newMemName.getText().toString();
	                // si c'est vide
	                if(name.isEmpty()){
	                	// informer l'utilisateur
	                	Toast.makeText(getActivity(), "Name of member empty", Toast.LENGTH_SHORT).show();
	                // sinon
	                } else {
	                	// créer le nouveau membre
	        	        newMemName.setText("");
	        	        Member newMember = new Member(name,group.getNb_members()+1);
	        	        group.memberAdd(newMember);
	        	        // et l'ajouter à la liste
	        	        mListData3.add(newMember.getNameM());
//	        	        dummyListViewMem3.setAdapter(mListData3);
	                }
	                // cacher le clavier
	        		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	        		imm.hideSoftInputFromWindow(newMemName.getWindowToken(), 0);
	        	}
	        });
	     	// mise en place d'un listener sur le bouton pour sauver un goupe
	        btnSave.setOnClickListener(new OnClickListener(){
	           @Override
	           public void onClick(View v) {
	        	   // si le groupe n'a aucun membre
	        	   if(group.getList_members().isEmpty()) {
	        		   // en informer l'utilisateur
	        		   Toast.makeText(getActivity(), "This group has no members", Toast.LENGTH_SHORT).show();
	        	   // sinon mettre à jour la base de donnée
	        	   } else {
	        		   DataDbHelper db = new DataDbHelper(v.getContext());
	        		   EditText newGroupName = (EditText) getView().findViewById(R.id.row_title);
	        		   getActivity().setTitle(newGroupName.getText().toString());
	        		   group.setNameGp(newGroupName.getText().toString());
	        		   db.updateGroup(group);
	        		   mListData2 = new CustomArrayAdapter<Member>(v.getContext(),group.getList_members());
	        		   dummyListViewMem2.setAdapter(mListData2);
	        		   mListData2.notifyDataSetChanged();
	        	   }
	           }
	       });
		}
	}
	/*******************************************************************************
	 @function		onCreateDialog
	 @abstract		Méthode pour définir les boites de dialogue
	 @param			int id, pour savoir quelle boite ouvrir
	*******************************************************************************/
	@Override
    public Dialog onCreateDialog (int id) {
    	
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	// si id "informe" qu'on ne peut pas supprimer un membre
    	if (id == ID_DIALOG_ALERTNOTDELET_MEMBER) {
    		// en informer l'utilisateur
    		builder.setMessage("Never forget a friend who owes you !");
    	// si id "informe" qu'on peut supprimer un membre
    	} else if(id == ID_DIALOG_ALERTDELET_MEMBER){
        	Log.d("ERASE:", "you are tring to erase the member nb "+index);
        	Log.d("ERASE:", "this member is in"+group.getList_members().get(index).getBills_count()+" bills");
        	// message pour la question
            builder.setMessage("Do you want to delete this Member?")
            		// bouton de confirmation
                   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   // enlever le membre de la base de donnée et des listes
                           db.deleteMember(group.getList_members().get(index));
                           mListData2.removeItem(index);
                           mListData3.remove(group.getList_members().get(index).getNameM());
                           // actualisation
                           mListData3.notifyDataSetChanged();
                           mListData2.notifyDataSetChanged();
                           // fermeture de la boite de dialogue
                           dialog.dismiss();
                       }
                   })
                   // bouton d'annulation
                   .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   // fermeture de la boite de dialogue
                           dialog.cancel();
                       }
                   });
        	
         // si id "informe" qu'on peut supprimer une note
        } else if(id==ID_DIALOG_ALERTDELET_BILL){
        	// message pour la question
        	builder.setMessage("Do you want to delete this Bill?")
        	// bouton de confirmation
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	// enlever la note de la base de donnée et des listes
                    db.deleteBill(group.getList_bills().get(index));
                    group.billRemove(group.getList_bills().get(index));
                    db.updateGroup(group);
                    // actualisation
                    listsUpdate();
                    // fermeture de la boite de dialogue
                    dialog.dismiss();
                }
            })
            // bouton d'annulation
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	// fermeture de la boite de dialogue
                	dialog.cancel();
                }
            });
        // si id "informe" qu'on veut consulter les informations d'une note
        } else if(id == ID_DIALOG_DETAIL_BILL) {
        	// affichage des informations
        	adapter = new ArrayAdapter<String>(this.getBaseContext(),android.R.layout.simple_list_item_1);
        	builder.setTitle(group.getList_bills().get(index).getWhat()).setAdapter(adapter, null);
        }

        return builder.create();
	
	}
	/*******************************************************************************
	 @function		onPrepareDialog
	 @abstract		Méthode pour faire la mise a jour de la boite a dialog
	 @param			int id, pour savoir quelle boite ouvrir
	 @param			Dialog dialog, la boite a dialog a etre aficher
	*******************************************************************************/
	@Override
	protected void onPrepareDialog (int id, Dialog dialog){
		
		ArrayList<String> memList = new ArrayList<String>();	// ArrayList de membre
		int i;													// compteur de boucle
		
		group = db.getGroup(group.getId());
		
		// si id "informe" qu'on veut consulter les informations d'une note
		if(id == ID_DIALOG_DETAIL_BILL){
		
			// pour chaque membre du groupe
			for(i=0; i<group.getNb_members(); i++) {
				// si i est inférieur au nombre de membre qu'il y avait lorsque la note fut créée
				if(i < group.getList_bills().get(index).getWhos_in().length) {
					// et si le membre i est concerné par la note
					if(group.getList_bills().get(index).getWhos_in()[i]) {
						// et encore si c'est la personne qui a payé
						if(group.getList_bills().get(index).getWho_paied().getId() == group.getList_members().get(i).getId()) {
							// remplissement de la liste avec le nom du membre et sa dett dans l'adition
							memList.add(group.getList_members().get(i).getNameM()+" 	-		"+String.format("%.2f",
									(group.getList_bills().get(index).getValue()-group.getList_bills().get(index).debtMember())));
						// sinon
						} else {
							// remplissement de la liste avec le nom du membre et sa dett dans l'adicion
							memList.add(group.getList_members().get(i).getNameM()+" 	-		"+String.format("%.2f",
									(-group.getList_bills().get(index).debtMember())));
						}
					// ou si la personne n'est pas concernée par la note mais a payé
					} else if(group.getList_bills().get(index).getWho_paied().getId() == group.getList_members().get(i).getId()) {
						// remplissement de la liste avec le nom du membre et sa dett dans l'adicion
						memList.add(group.getList_members().get(i).getNameM()+" 	-		"+String.format("%.2f",
								(group.getList_bills().get(index).getValue())));
					// ou sinon
					} else {
						memList.add(group.getList_members().get(i).getNameM()+" 	-		$0,00");
					}
				// ou encore sinon
				} else 
					memList.add(group.getList_members().get(i).getNameM()+" 	-		$0,00");
			}
			
			adapter.addAll(memList);
			adapter.notifyDataSetChanged();
		}
	}
	/*******************************************************************************
	 @function		onDialogSaveClick
	 @abstract		Méthode pour faire les modifications après ajout de note
	 @param			View v,
	*******************************************************************************/
	@Override
	public void onDialogSaveClick(View v) {
		
		DataDbHelper db = new DataDbHelper(this);
		// on récupère le groupe dans la BDD
		group = db.getGroup(group.getId());
		// mise à jour des listes de notes et de membres
		mListData1 = new CustomArrayAdapter<Bill>(v.getContext(),group.getList_bills());
		dummyListViewBill.setAdapter(mListData1);
		mListData2 = new CustomArrayAdapter<Member>(v.getContext(),group.getList_members());
	    dummyListViewMem2.setAdapter(mListData2);
	    mListData1.notifyDataSetChanged();
	    mListData2.notifyDataSetChanged();
	    // informe l'utilisateur que la note a bien été ajouté
		Toast.makeText(this, "New bill saved", Toast.LENGTH_SHORT).show();	
		
	}
	/*******************************************************************************
	 @function		listsUpdate
	 @abstract		Méthode pour mettre à jour les listes de notes et de membres
	*******************************************************************************/
	private void listsUpdate(){
		mListData1 = new CustomArrayAdapter<Bill>(this,group.getList_bills());
		dummyListViewBill.setAdapter(mListData1);
		mListData2 = new CustomArrayAdapter<Member>(this,group.getList_members());
	    dummyListViewMem2.setAdapter(mListData2);
	    mListData1.notifyDataSetChanged();
	    mListData2.notifyDataSetChanged();
	}

}
