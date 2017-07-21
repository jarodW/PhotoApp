package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

/**
 * Created by jarod on 5/26/2017.
 */

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{
    private static final String EXTRA_CRIME_ID = "com.example.criminalIntent.crime_id";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button firstButton;
    private Button lastButton;
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_page);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        firstButton = (Button) findViewById(R.id.first_crime);
        lastButton = (Button) findViewById(R.id.last_crime);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager){
            @Override
            public int getCount() {
                return mCrimes.size();
            }

            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                if(position == 0) {
                    firstButton.setEnabled(false);
                }
                else if(position == mCrimes.size() -1) {
                    lastButton.setEnabled(false);
                }else{
                    firstButton.setEnabled(true);
                    lastButton.setEnabled(true);
                }
            }
        });

        firstButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                firstButton.setEnabled(false);
                lastButton.setEnabled(true);
            }
        });

        lastButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size()-1);
                firstButton.setEnabled(true);
                lastButton.setEnabled(false);
            }
        });

        for(int i = 0; i <mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onCrimeUPdated(Crime crime) {
    }
}
