package be.hcpl.android.beaconexample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.ui.LibsActivity;

import be.hcpl.android.beaconexample.BuildConfig;
import be.hcpl.android.beaconexample.R;
import be.hcpl.android.beaconexample.framework.TemplateFragment;


/**
 * Created by hanscappelle on 23/10/14.
 */
public class AboutFragment extends TemplateFragment {

    /**
     * ctor
     * @return
     */
    public static AboutFragment createInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textAbout = (TextView) view.findViewById(R.id.text_info);
        textAbout.setText(Html.fromHtml(getString(R.string.info_about).replace("${app_name}", getString(R.string.app_name)).replace("${app_version}", BuildConfig.VERSION_NAME)));
        Linkify.addLinks(textAbout, Linkify.ALL);

        view.findViewById(R.id.button_libraries).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Create an intent with context and the Activity class
                        Intent i = new Intent(getActivity(), LibsActivity.class);
                        //Pass the fields of your application to the lib so it can find all external lib information
                        i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));

                        //Define the libs you want (only those which don't include the information, and are not autoDetected)
                        //(OPTIONAL if all used libraries offer the information, or are autoDetected)
                        i.putExtra(Libs.BUNDLE_LIBS, new String[]{"altbeacon"});

                        //Display the library version (OPTIONAL)
                        i.putExtra(Libs.BUNDLE_VERSION, true);
                        //Display the library license (OPTIONAL
                        i.putExtra(Libs.BUNDLE_LICENSE, true);

                        //Set a title (OPTIONAL)
                        i.putExtra(Libs.BUNDLE_TITLE, "Open Source");

                        //start the activity
                        startActivity(i);
                    }
                }
        );
    }

    @Override
    public int getTitleResourceId() {
        return R.string.title_about;
    }

    @Override
    public boolean startsWithSeparator() {
        return true;
    }
}
