package com.hkw.arrivinginberlin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LanguageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LanguageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LanguageFragment() {
        // Required empty public constructor
    }

    public static LanguageFragment newInstance() {
        LanguageFragment fragment = new LanguageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_language, container, false);
        final Button german = (Button) layout.findViewById(R.id.german);
        german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSelected(LocaleUtils.GERMAN, german);
            }
        });
        final Button english = (Button) layout.findViewById(R.id.english);
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSelected(LocaleUtils.ENGLISH, english);
            }
        });
        final Button farsi = (Button) layout.findViewById(R.id.farsi);
        farsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSelected(LocaleUtils.FARSI, farsi);
            }
        });
        final Button arabic = (Button) layout.findViewById(R.id.arabic);
        arabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSelected(LocaleUtils.ARABIC, arabic);
            }
        });
        final Button kurdish = (Button) layout.findViewById(R.id.kurdish);
        kurdish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageSelected(LocaleUtils.KURDISH, kurdish);
            }
        });
        final Button french = (Button) layout.findViewById(R.id.french);
        french.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSelected(LocaleUtils.FRENCH, french);
            }
        });

        return layout;
    }


    public void languageSelected(@LocaleUtils.LocaleDef String language, Button button){
        LocaleUtils.setLocale(getActivity().getApplicationContext(), language);
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString(LocaleUtils.LANGUAGE, language).apply();
        button.setTextColor(getActivity().getResources().getColor(R.color.colorSelected));
        if (mListener != null){
            mListener.onLanguageSelection(true);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("SET LISTENER", String.valueOf(context));
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onLanguageSelection(boolean selected);
    }
}
