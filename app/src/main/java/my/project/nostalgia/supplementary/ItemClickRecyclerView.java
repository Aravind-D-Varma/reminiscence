package my.project.nostalgia.supplementary;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import my.project.nostalgia.R;

/**
 * Provides methods to click/long-click items of RecyclerView.<br>
 * Used for zooming in photo/video when clicked and an option to delete if long clicked.<br>
 * Copied from stackOverflow code. Inner workings not yet understood.
 */
public class ItemClickRecyclerView {
        private final RecyclerView mRecyclerView;
        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    // ask the RecyclerView for the viewHolder of this view.
                    // then use it to get the position for the adapter
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAbsoluteAdapterPosition(), v);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAbsoluteAdapterPosition(), v);
                }
                return false;
            }
        };
        private RecyclerView.OnChildAttachStateChangeListener mAttachListener
                = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                // every time a new child view is attached add click listeners to it
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
            }
        };

        private ItemClickRecyclerView(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            // the ID must be declared in XML, used to avoid
            // replacing the ItemClickRecyclerView without removing
            // the old one from the RecyclerView
            mRecyclerView.setTag(R.id.item_click_support, this);
            mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
        }

        public static ItemClickRecyclerView addTo(RecyclerView view) {
            // if there's already an ItemClickRecyclerView attached
            // to this RecyclerView do not replace it, use it
            ItemClickRecyclerView support = (ItemClickRecyclerView) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new ItemClickRecyclerView(view);
            }
            return support;
        }

        public static ItemClickRecyclerView removeFrom(RecyclerView view) {
            ItemClickRecyclerView support = (ItemClickRecyclerView) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public ItemClickRecyclerView setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public ItemClickRecyclerView setOnItemLongClickListener(OnItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
            return this;
        }

        private void detach(RecyclerView view) {
            view.removeOnChildAttachStateChangeListener(mAttachListener);
            view.setTag(R.id.item_click_support, null);
        }

        public interface OnItemClickListener {

            void onItemClicked(RecyclerView recyclerView, int position, View v);
        }

        public interface OnItemLongClickListener {

            boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
        }
    }
