package com.book.dan.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {
    private static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance(){
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();;
        return v;
    }

    private void setupAdapter(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent,0);
        Collections.sort(info, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        o1.loadLabel(pm).toString(),
                        o2.loadLabel(pm).toString()
                );
            }
        });
        mRecyclerView.setAdapter(new ActivityAdapter(info));
        Log.i(TAG, "Found " + info.size() + " activities.");
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mNameTextView;
        private ResolveInfo mResolveInfo;

        public ActivityHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView;
        }

        public void bindActivity(ResolveInfo resolveInfo){
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            mNameTextView.setText(resolveInfo.loadLabel(pm).toString());
            mNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent i = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{
        private List<ResolveInfo> mResolveInfo;

        public ActivityAdapter(List<ResolveInfo> resolveInfo){
            mResolveInfo = resolveInfo;
        }
        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(android.R.layout.simple_list_item_1,parent,false);
            return new ActivityHolder(v);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mResolveInfo.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mResolveInfo.size();
        }
    }
}
