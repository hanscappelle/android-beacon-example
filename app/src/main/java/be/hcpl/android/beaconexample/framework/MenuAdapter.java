package be.hcpl.android.beaconexample.framework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.hcpl.android.beaconexample.R;

/**
 * adapter for menu items. This way we can have translated labels in the menu for each fragment
 * <p/>
 * Created by hcpl on 15/07/14.
 * TODO convert to baseAdapter instead
 */
public class MenuAdapter extends ArrayAdapter<TemplateFragment> {

    private List<TemplateFragment> mFragments;

    private Context mContext;

    public MenuAdapter(Context context, List<TemplateFragment> fragments) {
        super(context, R.layout.listitem_menu, fragments);
        mFragments = fragments;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get view inflater ref
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // get fragment
        TemplateFragment fragment = mFragments.get(position);

        // general menu items
        View rowView = inflater.inflate(R.layout.listitem_menu, parent, false);

        if (fragment == null)
            return rowView;

        // get views
        ImageView icon = (ImageView) rowView.findViewById(R.id.menu_icon);
        TextView text = (TextView) rowView.findViewById(R.id.menu_title);
        View dividerTop = rowView.findViewById(R.id.menu_divider_top);
        View dividerBottom = rowView.findViewById(R.id.menu_divider_bottom);

        final int iconId = fragment.getIconResourceId();
        if (iconId > 0) {
            icon.setBackgroundResource(iconId);
            icon.setVisibility(View.VISIBLE);
        }
        text.setText(fragment.getTitleResourceId());
        dividerBottom.setVisibility(fragment.endsWithSeparator() ? View.VISIBLE : View.GONE);
        dividerTop.setVisibility(fragment.startsWithSeparator() ? View.VISIBLE : View.GONE);

        return rowView;
    }

}
