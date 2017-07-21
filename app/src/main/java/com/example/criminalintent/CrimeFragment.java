package com.example.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.GregorianCalendar;

/**
 * Created by jarod on 5/17/2017.
 */
//Only have one to three fragments on a screen at a time. Use views within fragments.
public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID= "crime_id";
    private static final String POSITION_CHANGED = "com.example.CrimeFragment.position_changed";
    private static final String CRIME_DELETED = "com.example.CrimeFragment.crime_delted";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PICTURE = "DialogPicture";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int CALL_CONTACT = 3;
    private static final int REQUEST_PHOTO = 4;
    private static final int REQUEST_DETAILED_PHOTO = 5;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallCriminal;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //This is the place where to wire up your widgets in a fragment.
        View v = inflater.inflate(R.layout.fragment_crime,container,false); //false because we are adding code to the paren's code and not attaching to the root.
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mCallCriminal = (Button) v.findViewById(R.id.call_criminal);
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        final Intent capturePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
                updateCrime();
                returnResult();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(manager,DIALOG_TIME);
            }
        });
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
                updateCrime();
                returnResult();
            }
        });
        mReportButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);*/
                //Instead of building own intent builds it with ShareCompat
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();
            }
        });
        mSuspectButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mCallCriminal.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact,CALL_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }
        boolean canTakePhoto = mPhotoButton != null && capturePhoto.resolveActivity(packageManager)!=null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.criminalintent.fileprovider",mPhotoFile);
                capturePhoto.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(capturePhoto,PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity: cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(capturePhoto,REQUEST_PHOTO);
                }
                startActivityForResult(capturePhoto,REQUEST_PHOTO);
            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                PictureViewerFragment dialog = PictureViewerFragment.newInstance(mPhotoFile);
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DETAILED_PHOTO);
                dialog.show(manager,DIALOG_PICTURE);

            }
        });
        getPermission();
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }
        if(requestCode == REQUEST_TIME){
            Date date = mCrime.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour =  data.getIntExtra(TimePickerFragment.EXTRA_TIME_HOUR, 0);
            int minute = data.getIntExtra(TimePickerFragment.EXTRA_TIME_MINUTE,0);
            Date newDate = new GregorianCalendar(year, month, day, hour,minute,0).getTime();
            mCrime.setDate(newDate);
            updateDate();
        }
        if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            try{
                if(c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }

        if(requestCode == CALL_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] queryFields;
            Cursor c = getActivity().getContentResolver().query(contactUri,null,null,null,null);
            String phoneNumber;
            String suspectId;
            try{
                if(c.getCount() == 0)
                    return;
                c.moveToFirst();
                suspectId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            }finally {
                c.close();
            }

            queryFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =  ?";
            String[] args = new String[]{suspectId};
            c = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,queryFields,whereClause,args,null);
            Log.d("First","HERE");
            if(c  == null)
                return;
            try{
                if(c.getCount() == 0)
                    return;
                c.moveToFirst();
                phoneNumber = c.getString(0);
            }finally {
                c.close();
            }
            Log.d("PHONE NUMBER: ", phoneNumber);
            Uri phoneUri = Uri.parse("tel:" + phoneNumber);
            Intent i = new Intent(Intent.ACTION_DIAL,phoneUri);
            startActivity(i);
        }

        if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    //Update the date and time of the date and time buttons
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString().replaceAll("\\d+:\\d+:\\d+\\s+\\w+",""));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCrime.getDate());
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        mTimeButton.setText(hour + ":" + (minute < 10 ? "0"+minute:minute));
    }


    //This method was used to update a single position at a time for a challenge
    public void returnResult(){
        Intent data = new Intent();
        int position = CrimeLab.get(getActivity()).getCrimes().indexOf(mCrime);
        data.putExtra(POSITION_CHANGED, position);
        getActivity().setResult(Activity.RESULT_OK,data);
    }

    //This method wasnt needed
    public void positionDeleted(){
        Intent data = new Intent();
        UUID uuid = mCrime.getId();
        data.putExtra(CRIME_DELETED,uuid);
        getActivity().setResult(Activity.RESULT_OK,data);
    }

    public static int getPositionChanged(Intent result){
        return result.getIntExtra(POSITION_CHANGED,0);
    }

    //This method wasnt needed
    public static UUID getDeletedCrimeId(Intent result){
        return (UUID) result.getSerializableExtra(CRIME_DELETED);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString  = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat= "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect,suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString,suspect);
        return report;
    }

    public void getPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUPdated(mCrime);
    }

    //Reguired interface for hosting activities
    public interface Callbacks{
        void onCrimeUPdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
