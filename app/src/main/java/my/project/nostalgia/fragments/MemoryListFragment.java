package my.project.nostalgia.fragments;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.project.nostalgia.adapters.MemoryRVAdapter;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryListActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static my.project.nostalgia.activities.LoginActivity.FIRST_TIME;
//TODO Some input files use or override a depecrate API recompile with -Xlint:deprecation for details
/**
 * Contains list layout of RecyclerView and floating action button to show all memories and to add a new one.
 */
public class MemoryListFragment extends Fragment {

    //region Declarations
    public static final String MEMORIES_KEY = "Memories";
    private RecyclerView mRecyclerView;
    private Memory mNewMemory;
    private MemoryRVAdapter mAdapter;
    private Callbacks mCallbacks;
    private boolean isFirstTime = false;

    private static final String[] DECLARED_GETPHOTO_PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_STORAGE_CODE = 102;
    //endregion

    /**
     * MemoryListFragment has a way to call methods on its hosting activity. It does not matter which
     * activity is the host. As long as the activity implements CrimeListFragment.Callbacks, everything in CrimeListFragment can work the same.
     */
    public interface Callbacks{
        void onMemorySelected(Memory Memory);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private boolean isDeviceTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_memory_list, menu);
        MenuItem searchItem = menu.findItem(R.id.memory_search_menu);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.memory_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    searchMemoriesByTitle(query);
                }
                catch (NullPointerException e){
                    Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    searchMemoriesByTitle(newText);
                }
                catch (NullPointerException e){
                    if(newText.length()>=1)
                        Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
    private String stringResource(int resourceID) {
        return getResources().getString(resourceID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        List<Memory> memories = MemoryLab.get(getActivity()).getMemories();
        View view;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        isFirstTime = prefs.getBoolean(FIRST_TIME,true);

        if (isFirstTime) {
            view = inflater.inflate(R.layout.fragment_memory_list, container, false);
            setListAndAddButton(view);
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("Users");
            DocumentReference userDocument = usersCollection.document(userid);
            userDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        if(!hasMediaPermission()) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext()
                                    ,R.style.Theme_AppCompat_Light_Dialog_Alert);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle("Storage permission necessary");
                            alertBuilder.setMessage("In order to load your images and videos of previous memories" +
                                    ", please grant storage permissions");
                            alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) ->
                                    requestPermissions(DECLARED_GETPHOTO_PERMISSIONS, MY_STORAGE_CODE));
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        }
                        prefs.edit().putBoolean(FIRST_TIME,false).apply();
                        MemoryLab memoryLab = MemoryLab.get(getActivity());
                        int number = memoryLab.getMemories().size();
                        Map<String,Object> dataReceived = documentSnapshot.getData();
                        List<HashMap> hashMaps = (List<HashMap>) dataReceived.get(MEMORIES_KEY);
                        for(HashMap hashMap:hashMaps){
                            Memory memory = new Memory();
                            try {memory.setTitle(hashMap.get("title").toString());
                            }catch (NullPointerException e){memory.setTitle("");}
                            try{memory.setDetail(hashMap.get("detail").toString());
                            }catch (NullPointerException e){memory.setDetail("");}
                            try{memory.setMediaPaths(hashMap.get("mediaPaths").toString());
                            }catch (NullPointerException e){memory.setMediaPaths("");}
                            try{
                            memory.setEvent(hashMap.get("event").toString());}
                            catch (NullPointerException e){memory.setEvent("");}
                            memoryLab.addMemory(memory);
                        }

                    }
                }
            });
        }
        else if(memories.size()==0){

            view = inflater.inflate(R.layout.empty_list_page, container, false);
            Button noMemoryButton = view.findViewById(R.id.no_memory_button);

            noMemoryButton.setOnClickListener(noMemoryView -> {
                mNewMemory = new Memory();
                MemoryLab.get(getActivity()).addMemory(mNewMemory);
                if(isDeviceTablet()) {
                    container.removeAllViews();
                    noMemoryView = inflater.inflate(R.layout.fragment_memory_list, container, false);
                    container.addView(noMemoryView);
                    setListAndAddButton(noMemoryView);
                }
                mCallbacks.onMemorySelected(mNewMemory);
            });
        }
        else{
            view = inflater.inflate(R.layout.fragment_memory_list, container, false);
            setListAndAddButton(view);
        }
        updateUI();
        return view;
    }

    private boolean hasMediaPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_GETPHOTO_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void setListAndAddButton(View view) {
        mRecyclerView = view.findViewById(R.id.memory_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton floatingActionButton = view.findViewById(R.id.memory_fab);
        floatingActionButton.setOnClickListener(v -> {
            mNewMemory = new Memory();
            MemoryLab.get(getActivity()).addMemory(mNewMemory);
            updateUI();
            mCallbacks.onMemorySelected(mNewMemory);
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_STORAGE_CODE) {
            if (hasMediaPermission()) {
                startActivity(new Intent(getActivity(), MemoryListActivity.class));
                getActivity().finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void updateUI() {
        MemoryLab memoryLab = MemoryLab.get(getActivity());
        List<Memory> Memorys = memoryLab.getMemories();
        if(mAdapter == null && Memorys.size()!=0) {
            isFirstTime = false;
            mAdapter = new MemoryRVAdapter(getContext(),getActivity(),Memorys);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            if (Memorys.size() != 0) {
                mAdapter.updateList(Memorys);
            }
        }
        updateSubtitle();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(MemoryLab.get(getActivity()).getMemories().size()!=0 && isFirstTime) {
            MemoryListFragment fragment = new MemoryListFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            startActivity(new Intent(getContext(), MemoryListActivity.class));
            getActivity().finish();
            return;
        }
        if(!isFirstTime)
            updateUI();
    }
    /**
     * Shows how many memories are there on the ActionBar
     */
    public void updateSubtitle(){
        MemoryLab memoryLab = MemoryLab.get(getActivity());
        int MemoryCount = memoryLab.getMemories().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, MemoryCount, MemoryCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    public void eventFilter(String event) {
        List<Memory> searchMemorysList = new ArrayList<>();

        for(Memory Memory: MemoryLab.get(getActivity()).getMemories()){
            if(event.equals(getString(R.string.all)))
                searchMemorysList.add(Memory);
            else if(Memory.getEvent().equals(event)){
                searchMemorysList.add(Memory);
            }
        }
        try {
            mAdapter.updateList(searchMemorysList);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
        }
    }
    public void searchMemoriesByTitle(String text) {

        List<Memory> searchMemorysList = new ArrayList<>();
        for (Memory Memory : MemoryLab.get(getActivity()).getMemories()) {
            if (Memory.getTitle().contains(text))
                searchMemorysList.add(Memory);
        }
        try {
            mAdapter.updateList(searchMemorysList);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), stringResource(R.string.emptyfilter), Toast.LENGTH_SHORT).show();
        }
    }
}
