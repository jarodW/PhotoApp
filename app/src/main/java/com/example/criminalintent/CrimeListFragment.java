package com.example.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by jarod on 5/19/2017.
 */

public class CrimeListFragment extends Fragment{

    private static final String TAG = "CrimeListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static final int REQUEST_CODE_POSITION = 0;
    private int mUpdatePosition;
    private boolean mSubtitleVisible;
    private UUID deletedPosition;
    private Button mAddCrimeButton;
    private TextView mEmptyListText;
    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false); //This is false cause we are not appending to the activity
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //RecyclerView has to hand off data to LayoutManager
        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        mAddCrimeButton = (Button) view.findViewById(R.id.add_crime_button);
        mEmptyListText = (TextView) view.findViewById(R.id.empty_list_view);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);
            }
        });
        //ReyclerView does not position items on the screen while LayoutManager does. LinearLayoutManager Positions the items verticaly.
        updateUI();
        return view;
    }

    //Connects adapter to Recycle View
    //This is set to public so that CrimeFragment can update the UI
    public void updateUI(){
        Log.d(TAG,"Update UI");
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.replaceList(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateEmptyList();
        updateSubtitle();
    }

    //updates a singular position instead of whole list. The pager allows you to update multiple elements
    //at a time so no longer in use.
    private void updateUI(int position){
        Log.d(TAG,"Update UI");
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        //Crime crime = crimeLab.getCrime(deletedPosition);
        //crimeLab.deleteCrime(crime);
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{

            mAdapter.notifyItemChanged(position);
        }
        updateSubtitle();
    }


    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            itemView.setOnClickListener(this);
            //inflate views
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }
        //bind data to views
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString().replaceAll("\\d+:\\d+:\\d+\\s+\\w+",""));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            /*Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent,REQUEST_CODE_POSITION);*/
            //Moved the previous action to the activity to keep independence of the fragments for different functionality
            //between the tablet and mobile modes.
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class SeriousCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Button mCallPolice;
        private ImageView mSolvedImageView;
        private Crime mCrime;
        public SeriousCrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_police,parent,false));
            itemView.setOnClickListener(this);
            //inflate views
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mCallPolice = (Button) itemView.findViewById(R.id.contact_police);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }
        //bind data to views
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString().replaceAll("\\d+:\\d+:\\d+\\s+\\w+",""));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent,REQUEST_CODE_POSITION);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if(viewType == 0)
                return new CrimeHolder(layoutInflater,parent);
            else
                return new SeriousCrimeHolder(layoutInflater,parent);
        }

        @Override
        //bind data everytime a new holder is to be bound. Always be effient here, if not, can cause lag
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            if(holder instanceof CrimeHolder)
                ((CrimeHolder) holder).bind(crime);
            if(holder instanceof SeriousCrimeHolder)
                ((SeriousCrimeHolder)holder).bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position){
            if(mCrimes.get(position).isRequiresPolice() == false)
                return 0;
            else
                return 1;
        }

        public void replaceList(List<Crime> crimes){
            mCrimes = crimes;
        }



    }

    @Override
    public void onResume() {
        Log.d(TAG,"RESUME");
        Log.d(TAG,"Size: " + CrimeLab.get(getActivity()).getCrimes().size());
        super.onResume();
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_POSITION){
            if(data == null){
                return;
            }
            //mUpdatePosition = CrimeFragment.getPositionChanged(data); Used in challenge to update a single list item instead of all no longer in use
            deletedPosition = CrimeFragment.getDeletedCrimeId(data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem =menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitile);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                /*Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);*/
                //Move the previous action to the activy to keep independence between fragments.
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,crimeCount,crimeCount);
        /*if(crimeCount ==1)
            subtitle = getString(R.string.subtitle_single_format,crimeCount);
        else
            subtitle = getString(R.string.subtitle_format,crimeCount);*/
        if(!mSubtitleVisible)
            subtitle = null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateEmptyList(){
        if(CrimeLab.get(getActivity()).getCrimes().size() !=0){
            mAddCrimeButton.setVisibility(View.GONE);
            mEmptyListText.setVisibility(View.GONE);
        }
    }

    private void deleteCrime(){
        Crime crime = CrimeLab.get(getActivity()).getCrime(deletedPosition);
        CrimeLab.get(getActivity()).deleteCrime(crime.getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    //Delegets functionallity back to activities to keep the independence of fragments.
    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

}
