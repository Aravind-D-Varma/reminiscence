package my.project.nostalgia.supplementary;
import androidx.viewpager.widget.ViewPager;

public class CircularViewPager implements ViewPager.OnPageChangeListener {
    private final ViewPager   mViewPager;
    private int         mCurrentPosition;
    private int         mScrollState;

    public CircularViewPager(final ViewPager viewPager) {
        mViewPager = viewPager;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
    }
    @Override
    public void onPageScrollStateChanged(int state) {
        handleScrollState(state);
        mScrollState = state;
    }

    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE)
            setNextItemIfNeeded();
    }

    private void setNextItemIfNeeded() {
        if (mScrollState != ViewPager.SCROLL_STATE_SETTLING)
            handleSetNextItem();
    }

    private void handleSetNextItem() {
        final int lastPosition = mViewPager.getAdapter().getCount() - 1;

        if(mCurrentPosition == 0)
            mViewPager.setCurrentItem(lastPosition, false);

        else if(mCurrentPosition == lastPosition)
            mViewPager.setCurrentItem(0, false);
    }
}
