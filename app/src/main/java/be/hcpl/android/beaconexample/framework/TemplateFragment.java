package be.hcpl.android.beaconexample.framework;


import android.support.v4.app.Fragment;

import be.hcpl.android.beaconexample.MainActivity;
import be.hcpl.android.beaconexample.R;

/**
 * A template for all fragments, this adds a name for the fragment to be listed in menu
 * <p/>
 * Created by hcpl on 12/05/14.
 */
public class TemplateFragment extends Fragment {

    public static TemplateFragment createInstance() {
        return new TemplateFragment();
    }

    /**
     * @return the class name of the fragment, this can ben loaded in the drawer without need of a
     * special adapter
     */
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * retrieve a translated title for this fragment that can be used for display in menu
     *
     * @return
     */
    public int getTitleResourceId() {
        return R.string.empty_menu_item;
    }

    public boolean endsWithSeparator() {
        return false;
    }

    public boolean startsWithSeparator() {
        return false;
    }

    public int getIconResourceId() {
        return -1;
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
