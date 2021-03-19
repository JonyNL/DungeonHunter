package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jonyn.dungeonhunter.R;

import java.util.ArrayList;

import static com.jonyn.dungeonhunter.fragments.HeroSelectFragment.FB_USER_UID;
import static com.jonyn.dungeonhunter.fragments.HeroSelectFragment.HERO_POS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewHeroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewHeroFragment extends Fragment {

    private String usrUID;
    private int heroPos;
    private FirebaseFirestore db;

    private EditText etHeroName;
    private Spinner spClasses;
    private Button btnConfirm;

    public NewHeroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param usrUID ID del usuario.
     * @return A new instance of fragment NewHeroFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewHeroFragment newInstance(String usrUID, int Pos) {
        NewHeroFragment fragment = new NewHeroFragment();
        Bundle args = new Bundle();
        args.putString(FB_USER_UID, usrUID);
        args.putInt(HERO_POS, Pos);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_hero, container, false);

        Bundle b = getArguments();
        usrUID = b.getString(FB_USER_UID);
        heroPos = b.getInt(HERO_POS, -1);
        db = FirebaseFirestore.getInstance();

        etHeroName = v.findViewById(R.id.etHeroName);
        spClasses = v.findViewById(R.id.spClasses);
        btnConfirm = v.findViewById(R.id.btnConfirm);

        ArrayList<String> classesList = new ArrayList<>();
        classesList.add("Warrior");
        classesList.add("Wizard");
        final ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, classesList);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClasses.setAdapter(spAdapter);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();

                String name = etHeroName.getText().toString();
                String hClass = spClasses.getSelectedItem().toString();

                if (name.length() < 3){
                    etHeroName.setError("Name must be 3 characters or more");
                } else {

                    // Creamos un HeroSelectFragment pasandole como parametros el usrID, heroPos, name y hClass.
                    Fragment fragment = HeroSelectFragment.newInstance(usrUID, heroPos, name, hClass);

                    // A traves del FragmentManager que guardamos antes, realizamos la transaccion
                    // del fragment
                    String newFragment = fragment.getClass().getName();
                    manager.popBackStack();
                    manager.beginTransaction()
                            .replace(R.id.container, fragment, newFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return v;
    }


}
