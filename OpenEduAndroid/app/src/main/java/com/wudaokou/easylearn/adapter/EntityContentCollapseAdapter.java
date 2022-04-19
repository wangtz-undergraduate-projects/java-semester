package com.wudaokou.easylearn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.data.EntityFeature;
import com.wudaokou.easylearn.data.SearchRecord;

import java.util.List;

public class EntityContentCollapseAdapter extends BaseAdapter {
    public List<EntityFeature> entityFeatureList;
    public int resourceId;
    private LayoutInflater inflater;

    EntityContentCollapseAdapter(List<EntityFeature> entityFeatureList,
                                 int resourceId, LayoutInflater inflater) {
        this.entityFeatureList = entityFeatureList;
        this.resourceId = resourceId;
        this.inflater = inflater;
        Log.e("collapse", String.format("共有%d条记录",
                entityFeatureList == null ? 0 : entityFeatureList.size()));
    }

    @Override
    public int getCount() {
        if (entityFeatureList != null)
            return entityFeatureList.size();
        return 0;
    }

    @Override
    public EntityFeature getItem(int position) {
        if (entityFeatureList != null)
            return entityFeatureList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(resourceId, null);
        TextView featureKey = view.findViewById(R.id.featureKeyTextView);
        TextView featureValue = view.findViewById(R.id.featureValueTextView);
        EntityFeature entityFeature = getItem(position);
        if (entityFeature != null) {
            featureKey.setText(entityFeature.feature_key);
            featureValue.setText(entityFeature.feature_value);
        }
        return view;
    }
}
