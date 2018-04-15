package club.psarda.dynamicinstantcircumengine.Adapters.ListTargetResults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.TargetAdapterData;
import club.psarda.dynamicinstantcircumengine.R;

/**
 * Created by pfsar on 8/09/2017.
 */

public class ListAdatperTargetResults extends ArrayAdapter<TargetAdapterData> {

    public ListAdatperTargetResults(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdatperTargetResults(Context context, int resource, List<TargetAdapterData> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_adatper_reuslt_single_value, null);
        }

        TargetAdapterData targetAdapterData = getItem(position);

        if (targetAdapterData != null) {
            TextView title = (TextView) v.findViewById(R.id.input_title);
            TextView subTitle = (TextView) v.findViewById(R.id.input_subtitle);
            TextView result = (TextView) v.findViewById(R.id.result);

            if (title != null) {
                title.setText(targetAdapterData.get_title());
            }

            if (subTitle != null) {
                subTitle.setText(targetAdapterData.get_subTitle());
            }

            if (result != null) {
                result.setText(targetAdapterData.get_inputtedText());
            }
        }

        return v;
    }
}