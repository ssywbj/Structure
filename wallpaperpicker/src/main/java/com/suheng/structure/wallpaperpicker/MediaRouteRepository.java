package com.suheng.structure.wallpaperpicker;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2;
import android.media.RouteDiscoveryPreference;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suheng.structure.wallpaperpicker.bean.RouteData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class MediaRouteRepository {
    private static final String TAG = MediaRouteRepository.class.getSimpleName();

    private static volatile MediaRouteRepository sInstance;
    private final MediaRouter2 mMediaRouter2;
    private final Map<String, RouteData> mMapRouteData = new HashMap<>();
    private final Map<String, MediaRoute2Info> mMapRoute2Info = new HashMap<>();

    @Nullable
    private MediaRouter2.RouteCallback mRouteCallback;
    private final AudioManager mAudioManager;

    private MediaRouteRepository(@NonNull Context context) {
        mMediaRouter2 = MediaRouter2.getInstance(context);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static MediaRouteRepository getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (MediaRouteRepository.class) {
                if (sInstance == null) {
                    sInstance = new MediaRouteRepository(context);
                }
            }
        }
        return sInstance;
    }

    public List<RouteData> getRouteList(@NonNull Executor executor, @Nullable OnDataLChangedListener onDataLChangedListener) {
        List<MediaRoute2Info> routes = mMediaRouter2.getRoutes();
        final List<RouteData> routeDataList = new ArrayList<>();
        for (MediaRoute2Info route : routes) {
            mMapRoute2Info.put(route.getId(), route);
            final RouteData routeData = new RouteData(route);
            mMapRouteData.put(route.getId(), routeData);

            routeDataList.add(routeData);
        }

        List<MediaRouter2.RoutingController> controllers = mMediaRouter2.getControllers();
        Log.d(TAG, "controllers.size(): " + controllers.size() + ", routes.size(): " + routes.size());
        for (MediaRouter2.RoutingController controller : controllers) {
            String controllerId = controller.getId();
            for (MediaRoute2Info route : controller.getSelectedRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = route.getType();
                Log.d(TAG, "selectedRoutes, controllerId: " + controllerId + ", routeId: "
                        + routeId + "\nname: " + name + ", type: " + type + ", HashCode: "
                        + System.identityHashCode(route));
            }

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                it.transferableRoutes.forEach {
                    val routeId = it.id
                    val name = it.name
                    val type = it.type
                    Log.i("Wbj", "transferableRoutes, controllerId:$controllerId, routeId:$routeId, name:$name, type:$type")
                }
            }*/

            for (MediaRoute2Info route : controller.getSelectableRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = route.getType();
                Log.d(TAG, "selectableRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }

            for (MediaRoute2Info route : controller.getDeselectableRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = route.getType();
                Log.d(TAG, "deselectableRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }
        }

        List<String> preferredFeatures = Collections.singletonList(MediaRoute2Info.FEATURE_LIVE_AUDIO);
        RouteDiscoveryPreference discoveryPreference = new RouteDiscoveryPreference.Builder(preferredFeatures, true).build();
        MediaRouter2.RouteCallback routeCallback = new MediaRouter2.RouteCallback() {
            @Override
            public void onRoutesUpdated(@NonNull List<MediaRoute2Info> routes) {
                super.onRoutesUpdated(routes);
                Log.d(TAG, "onRoutesUpdated, routes.size(): " + routes.size());
                for (MediaRoute2Info route : routes) {
                    final String id = route.getId();
                    final int type = route.getType();
                    final CharSequence name = route.getName();

                    final RouteData cacheRouteData = mMapRouteData.get(id);
                    if (cacheRouteData == null) {
                        mMapRoute2Info.put(id, route);
                        final RouteData routeData = new RouteData(route);
                        mMapRouteData.put(id, routeData);

                        if (onDataLChangedListener != null) {
                            Log.i(TAG, "onRoutesUpdated, onRouteAdded: " + routeData
                                    + "\nid: " + id + ", type: " + type + ", name: " + name);
                            onDataLChangedListener.onRouteAdded(routeData);
                        }
                    } else {
                        cacheRouteData.updateData(route);
                        if (onDataLChangedListener != null) {
                            Log.i(TAG, "onRoutesUpdated, onRouteUpdated: " + cacheRouteData
                                    + "\nid: " + id + ", type: " + type + ", name: " + name);
                            onDataLChangedListener.onRouteUpdated(cacheRouteData);
                        }
                    }
                }

                Iterator<Map.Entry<String, RouteData>> iterator = mMapRouteData.entrySet().iterator();
                while (iterator.hasNext()) {//example: 2, 1, 3, 4
                    Map.Entry<String, RouteData> dataEntry = iterator.next();
                    String cacheId = dataEntry.getKey();
                    boolean exclude = true;
                    for (MediaRoute2Info route : routes) {//example: 1, 4
                        if (route.getId().equals(cacheId)) {
                            exclude = false;
                            break;
                        }
                    }

                    if (exclude) {
                        mMapRoute2Info.remove(cacheId);
                        iterator.remove();

                        RouteData cacheRouteData = dataEntry.getValue();
                        if (onDataLChangedListener != null) {
                            Log.i(TAG, "onRoutesUpdated, onRouteRemoved: " + cacheRouteData);
                            onDataLChangedListener.onRouteRemoved(cacheRouteData);
                        }
                    }
                }

            }
        };
        mMediaRouter2.registerRouteCallback(executor, routeCallback, discoveryPreference);
        mRouteCallback = routeCallback;

        mMediaRouter2.registerTransferCallback(executor, new MediaRouter2.TransferCallback() {
            @Override
            public void onTransfer(@NonNull MediaRouter2.RoutingController oldController, @NonNull MediaRouter2.RoutingController newController) {
                super.onTransfer(oldController, newController);
                Log.i("Wbj", "onTransfer, oldController: " + oldController + ", newController: " + newController);
            }

            @Override
            public void onTransferFailure(@NonNull MediaRoute2Info requestedRoute) {
                super.onTransferFailure(requestedRoute);
                Log.w("Wbj", "onTransferFailure: " + requestedRoute);
            }

            @Override
            public void onStop(@NonNull MediaRouter2.RoutingController controller) {
                super.onStop(controller);
                Log.i("Wbj", "onStop, controller");
            }
        });

        mMediaRouter2.registerControllerCallback(executor, new MediaRouter2.ControllerCallback() {
            @Override
            public void onControllerUpdated(@NonNull MediaRouter2.RoutingController controller) {
                super.onControllerUpdated(controller);
                Log.i("Wbj", "onControllerUpdated: " + controller);
            }
        });

        return routeDataList;
    }

    public void transferTo(@NonNull RouteData routeData) {
        MediaRoute2Info route2Info = mMapRoute2Info.get(routeData.getId());
        Log.d(TAG, "transferTo, route2Info: " + route2Info);
        if (route2Info == null) {
            return;
        }
        mMediaRouter2.transferTo(route2Info);
    }

    public void setVolume(@NonNull RouteData routeData, int volume) {
        MediaRoute2Info route2Info = mMapRoute2Info.get(routeData.getId());
        Log.i(TAG, "setVolume, volume: " + volume + ", route2Info: " + route2Info
                + ", " + mAudioManager.isVolumeFixed() + ", " + mAudioManager.isMusicActive());
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }

    public void destroy() {
        if (mRouteCallback != null) {
            mMediaRouter2.unregisterRouteCallback(mRouteCallback);
        }
        mMapRoute2Info.clear();
        mMapRouteData.clear();
    }

    public abstract static class OnDataLChangedListener {

        abstract void onRouteRemoved(@NonNull RouteData routeData);


        abstract void onRouteAdded(@NonNull RouteData routeData);


        abstract void onRouteUpdated(@NonNull RouteData routeData);
    }

}
