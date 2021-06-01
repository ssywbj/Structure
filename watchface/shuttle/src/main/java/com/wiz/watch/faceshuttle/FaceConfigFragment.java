package com.wiz.watch.faceshuttle;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.structure.wallpaper.basic.adapter.RecyclerAdapter;
import com.structure.wallpaper.basic.bean.AppInfo;
import com.structure.wallpaper.basic.bean.AppSortBean;
import com.structure.wallpaper.basic.utils.Constants;
import com.wiz.watch.faceshuttle.view.WizScaleScrollbar;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FaceConfigFragment extends Fragment {
    public static final String TAG = FaceConfigFragment.class.getSimpleName();
    private Activity mContext;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        return LayoutInflater.from(context).inflate(R.layout.fragment_watch_face_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        final WizScaleScrollbar scaleScrollbar = view.findViewById(R.id.wiz_scale_scrollbar);
        scaleScrollbar.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        String selectedComponent = null;
        if (activity != null) {
            Intent extras = activity.getIntent();
            selectedComponent = extras.getExtras()
                    .getString(Constants.KEY_SELECTED_COMPONENT, "");
        }

        List<AppInfo> listApp = new ArrayList<>();
        AppListAdapter adapter = new AppListAdapter(listApp);
        mRecyclerView.setAdapter(adapter);

        final List<String> appSortList = this.getAppSortList();

        Context context = getContext();
        final PackageManager packageManager = context.getPackageManager();
        final LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        final UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        List<UserHandle> userProfiles = userManager.getUserProfiles();
        AppInfo appInfo;
        ComponentName componentName;
        Intent intent;
        for (UserHandle userProfile : userProfiles) {
            //Log.i(TAG, "userProfile: " + userProfile);
            List<LauncherActivityInfo> activityList = launcherApps.getActivityList(null, userProfile);
            for (LauncherActivityInfo launcherActivityInfo : activityList) {
                intent = new Intent(Intent.ACTION_MAIN);
                componentName = launcherActivityInfo.getComponentName();
                intent.setComponent(componentName);
                ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.GET_META_DATA);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                /*Log.v(TAG, "pkg: " + activityInfo.packageName + ", name:" + activityInfo.name
                        + ", label: " + activityInfo.loadLabel(packageManager) + "\ncomponentName: " + componentName.flattenToShortString());*/
                if (activityInfo.metaData != null) {
                    int resId = activityInfo.metaData.getInt(Constants.KEY_ICON_FACE);
                    if (resId != 0 && appSortList.contains(componentName.flattenToShortString())) {
                        Drawable drawable = packageManager.getDrawable(activityInfo.packageName, resId, activityInfo.applicationInfo);
                        drawable.setTint(Color.WHITE);
                        //Log.d(TAG, "resId: " + resId + ", drawable: " + drawable);
                        appInfo = new AppInfo(launcherActivityInfo.getLabel().toString()
                                , activityInfo.packageName, componentName, drawable);
                        appInfo.setSelected((componentName.getPackageName() + Constants.REGEX_SPILT
                                + componentName.getClassName()).equals(selectedComponent));
                        listApp.add(appInfo);
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private List<String> getAppSortList() {
        List<String> list = new ArrayList<>();
        try {
            File file = new File("/system/etc/wiz_home/config/app_config.json");
            if (file.exists()) {
                Gson gson = new Gson();
                AppSortBean sortBean = gson.fromJson(new FileReader(file.getPath()), AppSortBean.class);
                List<AppSortBean.Info> appSorts = sortBean.mAppSorts;
                //Log.d(TAG, "sortBean: " + sortBean + ", " + appSorts);
                if (appSorts != null) {
                    for (AppSortBean.Info appSort : appSorts) {
                        //Log.d(TAG, "app sort info: " + appSort);
                        list.add(appSort.mComponentName);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "get app sort rule error: " + e.toString());
        }

        return list;
    }

    private final class AppListAdapter extends RecyclerAdapter<AppInfo> {

        AppListAdapter(List<AppInfo> dataList) {
            super(dataList);
        }

        @Override
        protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final AppInfo data) {
            if (viewHolder instanceof ContentHolder) {
                ContentHolder contentHolder = (ContentHolder) viewHolder;
                Context context = getContext();
                contentHolder.appName.setText(data.label);
                if (data.isSelected) {
                    contentHolder.imageIcon.setBackgroundResource(R.drawable.corner_btn_blue_bg);
                    contentHolder.appName.setTextColor(ContextCompat.getColor(context, R.color.conner_btn_blue_border));
                } else {
                    contentHolder.imageIcon.setBackgroundResource(R.drawable.corner_btn_bg);
                    contentHolder.appName.setTextColor(Color.WHITE);
                }
                contentHolder.imageIcon.setImageDrawable(null);
                contentHolder.imageIcon.setImageDrawable(data.drawable);

                contentHolder.appName.setOnClickListener((view) -> contentHolder.imageIcon.performClick());
                contentHolder.imageIcon.setOnClickListener((View view) -> {
                    Intent intent = new Intent(Constants.ACTION_UPDATE_WATCH_FACE);
                    intent.putExtra(Constants.KEY_DATA_COMPONENT_NAME, data.componentName);
                    mContext.sendBroadcast(intent);
                });
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.fragment_watch_face_config_adt));
        }

        class ContentHolder extends RecyclerView.ViewHolder {
            TextView appName;
            ImageView imageIcon;

            ContentHolder(View view) {
                super(view);
                appName = view.findViewById(R.id.text_app_name);
                imageIcon = view.findViewById(R.id.image_icon);
            }
        }
    }

}
