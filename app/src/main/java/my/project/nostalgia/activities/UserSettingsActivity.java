package my.project.nostalgia.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import my.project.nostalgia.R;
import my.project.nostalgia.fragments.UserSettingsFragment;
import my.project.nostalgia.supplementary.changeTheme;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
/**
 * Setup of allowing users to change their name, their event preferences and tells about me.
 */
public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new changeTheme(this).setUserTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new UserSettingsFragment()).commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));

        MobileAds.initialize(this,"ca-app-pub-2146954042709344~2304825751");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd (adRequest);

        InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2146954042709344/8070242031");
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                mInterstitialAd.show();
            }
        });
    }
    /**
     * Refreshes previous activity so that if the user changes theme/language, it is reflected.
     * If not refreshed, it will show previous theme/language
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MemoryListActivity.class));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            startActivity(new Intent(this, MemoryListActivity.class));
        return super.onOptionsItemSelected(item);
    }
}