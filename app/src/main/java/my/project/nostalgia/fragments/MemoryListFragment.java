package my.project.nostalgia.fragments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryListActivity;
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
                    mAdapter.searchFilter(query);
                }
                catch (NullPointerException e){
                    Toast.makeText(getContext(),getResources().getString(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    mAdapter.searchFilter(newText);
                }
                catch (NullPointerException e){
                    if(newText.length()>=1)
                        Toast.makeText(getContext(),getResources().getString(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
                }
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
        setListAndAddButton(view);
        if(MemoryLab.get(getActivity()).getMemories().size()==0){
            View noMemoryView = inflater.inflate(R.layout.empty_list_page, container, false);
            SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
            String themeValues = getData.getString("GlobalTheme", "Dark");
            Button noMemoryButton = (Button) noMemoryView.findViewById(R.id.no_memory_button);
            if (themeValues.equals("Light")) {
                noMemoryButton.setBackgroundResource(R.drawable.button_border_light);
                noMemoryButton.setTextColor(getResources().getColor(R.color.white));
            }
            noMemoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View noMemoryView) {
                    mNewMemory = new Memory();
                    MemoryLab.get(getActivity()).addMemory(mNewMemory);
                    if(isDeviceTablet()) {
                        container.removeAllViews();
                        noMemoryView = inflater.inflate(R.layout.fragment_memory_list, container, false);
                        container.addView(noMemoryView);
                        setListAndAddButton(noMemoryView);
                        updateUIForTablet();
                    }
                    mCallbacks.onMemorySelected(mNewMemory);
                }
            });
            updateByDevice();
            return noMemoryView;
        }


        updateByDevice();
        return view;
    }

    private void setListAndAddButton(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.memory_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.memory_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewMemory = new Memory();
                MemoryLab.get(getActivity()).addMemory(mNewMemory);
                if(isDeviceTablet())
                    updateUIForTablet();
                mCallbacks.onMemorySelected(mNewMemory);
            }
        });
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
    @Override
    public void onResume() {
        super.onResume();
        if(MemoryLab.get(getActivity()).getMemories().size()!=0 && firstTime) {
            MemoryListFragment fragment = new MemoryListFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            startActivity(new Intent(getContext(), MemoryListActivity.class));
        }
        updateByDevice();
    }
    private void updateByDevice() {
        if (isDeviceTablet())
            updateUIForTablet();
        else
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
    /**
     * Deletes null memories to avoid crashes
     * Deletes all those memories whose title is null and does not contain any photos.
     * Then, hooks up the adapter and RecyclerView.
     * @see #noTitleAndPhotos(Memory)
     */
    public void updateUI() {

        MemoryLab memoryLab = MemoryLab.get(getActivity());
        removeGarbageMemories(memoryLab);
        List<Memory> Memorys;
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

    private void removeGarbageMemories(MemoryLab memoryLab) {
        List<Memory> Memorys = memoryLab.getMemories();
        for(Memory memory:Memorys) {
            if (Memorys.contains(null)) {
                int nullMemoryposition = Memorys.indexOf(null);
                MemoryLab.get(getActivity()).deleteMemory(Memorys.get(nullMemoryposition));
            }
            if(noTitleAndPhotos(memory))
                MemoryLab.get(getActivity()).deleteMemory(memory);
        }
    }

    private boolean noTitleAndPhotos(Memory memory) {
        boolean yesPhotos = true;
        try{
            yesPhotos = memory.getMediaPaths().length()<1;
        }
        catch (NullPointerException e){}
        return memory.getTitle()==null && yesPhotos;
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
                tv.setTextColor(getResources().getColor(R.color.light_purple));

        }
        public void bind(Memory Memory){
            mMemory = Memory;
            if (mMemory.getTitle()==null)
                mTitleTextView.setText(R.string.no_title_set);
            try{
                if (mMemory.getTitle().equals(""))
                    mTitleTextView.setText(R.string.no_title_set);
                else
                    mTitleTextView.setText(mMemory.getTitle());
            }catch (NullPointerException e){}
            mDateTextView.setText(String.format("%s%s", getString(R.string.noted_on), DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate())));
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

                List<Memory> searchMemorysList = new ArrayList<>();
                for (Memory Memory : MemoryLab.get(getActivity()).getMemories()) {
                    if (Memory.getTitle().contains(text)) {
                        searchMemorysList.add(Memory);
                    }
                }
            try {
                mAdapter.setMemorys(searchMemorysList);
                notifyDataSetChanged();
            }catch (NullPointerException e){
                Toast.makeText(getContext(),getResources().getString(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Updates display of memories depending on the event user has selected in the menu of Navigation Drawer.
     * Is written in fragment code since fragment contains details of memories.
     * @see MemoryListActivity
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
        try {
            mAdapter.setMemorys(searchMemorysList);
            mAdapter.notifyDataSetChanged();
        }catch (NullPointerException e){
            Toast.makeText(getContext(),getResources().getString(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
        }

    }

}
