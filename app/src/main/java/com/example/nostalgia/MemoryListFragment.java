package com.example.nostalgia;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

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
        final MenuItem searchItem = menu.findItem(R.id.memory_search_menu);
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
                MemoryLab.get(getActivity()).addMemory(mNewMemory);
                Intent intent = MemoryPagerActivity.newIntent(getActivity(), mNewMemory.getId());
                startActivity(intent);
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
                    Intent intent = MemoryPagerActivity.newIntent(getActivity(), mNewMemory.getId());
                    startActivity(intent);
                }
            });
            return noMemoryView;
        }
        //endregion
        updateUI();
        return view;
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

    //region updateUI (Adapter<->RecyclerView)
    private void updateUI() {

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
                mAdapter.notifyItemChanged(itemChangedposition);
            }
        }
        updateSubtitle();
    }
    //endregion
    //region MemoryHolder (our ViewHolder)
    private class MemoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //region Declarations
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Memory mMemory;
        //endregion

        // region MemoryHolder constructor
        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_memory, parent, false));
                itemView.setOnClickListener(this);

                mTitleTextView = (TextView) itemView.findViewById(R.id.memory_title);
                mDateTextView = (TextView) itemView.findViewById(R.id.memory_date);
        }
        //endregion

        //region bind
        public void bind(Memory Memory){
            mMemory = Memory;
            mTitleTextView.setText(mMemory.getTitle());
            mDateTextView.setText("Noticed on: " + DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate()));
        }
        //endregion
        //region onClick
        @Override
        public void onClick(View v) {
            //region notifyItemChanged position
            int i = 0;
            for (Memory Memory: MemoryLab.get(getActivity()).getMemories()){
                if(Memory.getId().equals(mMemory.getId())) {
                    itemChangedposition = i;
                    break;
                }
                i++;
            }
            //endregion
            mCallbacks.onMemorySelected(mMemory);
        }
        //endregion
    }
    //endregion
    //region MemoryAdapter (our MemoryAdapter)
    private class MemoryAdapter extends RecyclerView.Adapter<MemoryHolder>{

        private List<Memory> mMemories;

        // region MemoryAdapter constructor
        public MemoryAdapter(List<Memory> Memorys){
            mMemories = Memorys;
        }
        //endregion
        //region onCreateViewHolder
        @NonNull
        @Override
        public MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MemoryHolder(layoutInflater, parent);
        }
        //endregion
        //region onBindViewHolder
        @Override
        public void onBindViewHolder(@NonNull MemoryHolder holder, int position) {
            Memory Memory = mMemories.get(position);
            if(Memory.getTitle()==null)
                MemoryLab.get(getActivity()).deleteMemory(Memory);
            else
                holder.bind(Memory);
        }
        //endregion
        @Override
        public int getItemCount() {
            return mMemories.size();
        }
        public void setMemorys(List<Memory> Memorys){
            mMemories = Memorys;
        }

        public void searchFilter(String text) {
            List<Memory> searchMemorysList = new ArrayList<>();
            text = text.toLowerCase();
            for(Memory Memory: MemoryLab.get(getActivity()).getMemories()){
                if(Memory.getTitle().contains(text)){
                    searchMemorysList.add(Memory);
                }
            }
            mAdapter.setMemorys(searchMemorysList);
            notifyDataSetChanged();
        }
    }
    //endregion
    public void eventFilter(String event) {
        List<Memory> searchMemorysList = new ArrayList<>();
        for(Memory Memory: MemoryLab.get(getActivity()).getMemories()){
            if(Memory.getEvent().equals(event)){
                searchMemorysList.add(Memory);
            }
        }
        mAdapter.setMemorys(searchMemorysList);
        mAdapter.notifyDataSetChanged();
    }

}
