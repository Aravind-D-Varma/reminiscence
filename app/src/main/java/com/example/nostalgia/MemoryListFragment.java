package com.example.nostalgia;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains list layout of RecyclerView and floating action button to show all memories and to add a new one.
 */
public class MemoryListFragment extends Fragment {

    //region Declarations
    private static final String SAVE_SUBTITLE = "save_subtitle";
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private Button mNoMemoryButton;
    private TextView mNoMemoryTextView;
    private Memory mNewMemory;
    private MemoryAdapter mAdapter;
    private Callbacks mCallbacks;
    private int itemChangedposition;
    private boolean firstTime = true;
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_memory_list, menu);
        MenuItem searchItem = menu.findItem(R.id.memory_search_menu);

        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");
        if (themeValues.equals("Dark")) {
            searchItem.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.search_black));
        }

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.memory_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.searchFilter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.searchFilter(newText);
                return false;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_memory_list, container, false);
        //region RV and FAB
        mRecyclerView = (RecyclerView) view.findViewById(R.id.memory_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.memory_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewMemory = new Memory();
                MemoryLab memoryLab = MemoryLab.get(getActivity());
                memoryLab.addMemory(mNewMemory);
                if(isDeviceTablet())
                    updateUIForTablet();
                mCallbacks.onMemorySelected(mNewMemory);

            }
        });
        //endregion
        //region EmptyRecyclerView
        if(MemoryLab.get(getActivity()).getMemories().size()==0){
            View noMemoryView = inflater.inflate(R.layout.empty_list_page, container, false);
            mNoMemoryTextView = (TextView) noMemoryView.findViewById(R.id.no_memory_text);
            mNoMemoryButton = (Button) noMemoryView.findViewById(R.id.no_memory_button);
            mNoMemoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View noMemoryView) {
                    mNewMemory = new Memory();
                    MemoryLab.get(getActivity()).addMemory(mNewMemory);
                    mCallbacks.onMemorySelected(mNewMemory);
                    if(isDeviceTablet())
                        updateUIForTablet();
                }
            });
            return noMemoryView;
        }
        //endregion
        updateUI();
        return view;
    }

    /**
     * Binds RecyclerView to its adapter
     */
    public void updateUIForTablet() {
        MemoryLab memoryLab = MemoryLab.get(getActivity());
        List<Memory> Memorys = memoryLab.getMemories();
        if(mAdapter == null && Memorys.size()!=0) {
            firstTime = false;
            mAdapter = new MemoryAdapter(Memorys);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            if (Memorys.size() != 0) {
                mAdapter.setMemorys(Memorys);
                mAdapter.notifyDataSetChanged();
            }
        }
        updateSubtitle();
    }

    private boolean isDeviceTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MemoryLab.get(getActivity()).getMemories().size()!=0 && firstTime) {
            MemoryListFragment fragment = new MemoryListFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        updateUI();
    }

    /**
     * Shows how many memories are there on the ActionBar
     */
    private void updateSubtitle(){
        MemoryLab memoryLab = MemoryLab.get(getActivity());
        int MemoryCount = memoryLab.getMemories().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, MemoryCount, MemoryCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Deletes null memories to avoid crashes
     * Deletes all those memories whose title is null and does not contain any photos.
     * Then, hooks up the adapter and RecyclerView.
     * @see #noTitleAndPhotos(List, int)
     */
    public void updateUI() {

        MemoryLab memoryLab = MemoryLab.get(getActivity());
        List<Memory> Memorys = memoryLab.getMemories();
        for(int i = 0; i < Memorys.size();i++) {
            if (Memorys.contains(null)) {
                int nullMemoryposition = Memorys.indexOf(null);
                MemoryLab.get(getActivity()).deleteMemory(Memorys.get(nullMemoryposition));
            }

            if(noTitleAndPhotos(Memorys, i))
                MemoryLab.get(getActivity()).deleteMemory(Memorys.get(i));
        }
        Memorys = memoryLab.getMemories();
        if(mAdapter == null && Memorys.size()!=0) {
            firstTime = false;
            mAdapter = new MemoryAdapter(Memorys);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            if (Memorys.size() != 0) {
                mAdapter.setMemorys(Memorys);
                mAdapter.notifyItemChanged(itemChangedposition);
            }
        }
        updateSubtitle();
    }

    private boolean noTitleAndPhotos(List<Memory> memorys, int i) {
        boolean yesPhotos = true;
        try{
            yesPhotos = memorys.get(i).getMediaPaths().length()<1;
        }
        catch (NullPointerException e){}
        return memorys.get(i).getTitle()==null && yesPhotos;
    }

    /**
     * ViewHolder which sets up individual items of record of memories.
     */
    private class MemoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Memory mMemory;

        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_memory, parent, false));
                itemView.setOnClickListener(this);
                setLayoutTheme(itemView);
                mTitleTextView = (TextView) itemView.findViewById(R.id.memory_title);
                setTextTheme(mTitleTextView);
                mDateTextView = (TextView) itemView.findViewById(R.id.memory_date);
            setTextTheme(mDateTextView);
        }
        private void setLayoutTheme(View v){
            SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
            String themeValues = getData.getString("GlobalTheme", "Dark");

            if (themeValues.equals("Light"))
                v.setBackground(getResources().getDrawable(R.drawable.layout_border_light));
            else if (themeValues.equals("Dark"))
                v.setBackground(getResources().getDrawable(R.drawable.layout_border));
        }

        private void setTextTheme(TextView tv) {
            SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
            String themeValues = getData.getString("GlobalTheme", "Dark");

            if (themeValues.equals("Light"))
                tv.setTextColor(getResources().getColor(R.color.black));
            else if (themeValues.equals("Dark"))
                tv.setTextColor(getResources().getColor(R.color.white));

        }
        public void bind(Memory Memory){
            mMemory = Memory;
            if (mMemory.getTitle()==null)
                mTitleTextView.setText("(No Title set)");
            try{
                if (mMemory.getTitle().equals(""))
                    mTitleTextView.setText("(No Title set)");
                else
                    mTitleTextView.setText(mMemory.getTitle());
            }catch (NullPointerException e){}
            mDateTextView.setText("Noted on: " + DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate()));
        }

        @Override
        public void onClick(View v) {

            int i = 0;
            for (Memory Memory: MemoryLab.get(getActivity()).getMemories()) {
                if (mMemory != null) {
                    if (Memory.getId().equals(mMemory.getId())) {
                        itemChangedposition = i;
                        break;
                    }
                }
                    i++;
            }
            mCallbacks.onMemorySelected(mMemory);
        }

    }


    /**
     * Adapter for RecyclerView to contain record of all memories
     */
    private class MemoryAdapter extends RecyclerView.Adapter<MemoryHolder>{

        private List<Memory> mMemories;
        public MemoryAdapter(List<Memory> Memorys){
            mMemories = Memorys;
        }
        @NonNull
        @Override
        public MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MemoryHolder(layoutInflater, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull MemoryHolder holder, int position) {
            Memory Memory = mMemories.get(position);
            holder.bind(Memory);
        }
        @Override
        public int getItemCount() {
            return mMemories.size();
        }
        public void setMemorys(List<Memory> Memorys){
            mMemories = Memorys;
        }

        /**
         * Searches through all memories whose title contains the text typed and changes display accordingly
         * @param text
         */
        public void searchFilter(String text) {
            try {
                List<Memory> searchMemorysList = new ArrayList<>();
                text = text.toLowerCase();
                for (Memory Memory : MemoryLab.get(getActivity()).getMemories()) {
                    if (Memory.getTitle().contains(text)) {
                        searchMemorysList.add(Memory);
                    }
                }
                mAdapter.setMemorys(searchMemorysList);
                notifyDataSetChanged();
            }catch (NullPointerException e){}
        }
    }

    /**
     * Updates display of memories depending on the event user has selected in the menu of Navigation Drawer.
     * Is written in fragment code since fragment contains details of memories.
     * @see com.example.nostalgia.MemoryListActivity
     * @param event
     */
    public void eventFilter(String event) {
        List<Memory> searchMemorysList = new ArrayList<>();

        for(Memory Memory: MemoryLab.get(getActivity()).getMemories()){
            if(event.equals(getString(R.string.all)))
                searchMemorysList.add(Memory);
            else if(Memory.getEvent().equals(event)){
                searchMemorysList.add(Memory);
            }
        }
        mAdapter.setMemorys(searchMemorysList);
        mAdapter.notifyDataSetChanged();
    }

}
