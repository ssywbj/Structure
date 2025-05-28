package com.suheng.structure.wallpaperpicker;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2;
import android.media.RouteDiscoveryPreference;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

public class MediaRouteRepository {
    private static final String TAG = MediaRouteRepository.class.getSimpleName();

    private static volatile MediaRouteRepository sInstance;
    private final MediaRouter2 mMediaRouter2;

    @Nullable
    private MediaRouter2.RouteCallback mRouteCallback;

    private MediaRouteRepository(@NonNull Context context) {
        mMediaRouter2 = MediaRouter2.getInstance(context);
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

    public void getWifiList(@NonNull Context ctx, @NonNull MediaRouter2.RouteCallback routeCallback) {
        List<MediaRoute2Info> routes = mMediaRouter2.getRoutes();
        List<MediaRouter2.RoutingController> controllers = mMediaRouter2.getControllers();
        Log.d(TAG, "controllers.size(): " + controllers.size() + ", routes.size(): " + routes.size());
        for (MediaRouter2.RoutingController controller : controllers) {
            String controllerId = controller.getId();
            for (MediaRoute2Info route : controller.getSelectedRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.i(TAG, "selectedRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
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
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.d(TAG, "selectableRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }

            for (MediaRoute2Info route : controller.getDeselectableRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.d(TAG, "deselectableRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }
        }

        List<String> preferredFeatures = Collections.singletonList(MediaRoute2Info.FEATURE_LIVE_AUDIO);
        RouteDiscoveryPreference discoveryPreference = new RouteDiscoveryPreference.Builder(preferredFeatures, true).build();
        mMediaRouter2.registerRouteCallback(ctx.getMainExecutor(), routeCallback, discoveryPreference);
        mRouteCallback = routeCallback;

        /*mMediaRouter2.registerTransferCallback(ctx.getMainExecutor(), new MediaRouter2.TransferCallback() {
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
        });*/
    }

    public void transferTo(@NonNull MediaRoute2Info route) {
        mMediaRouter2.transferTo(route);
    }

    public void destroy() {
        if (mRouteCallback != null) {
            mMediaRouter2.unregisterRouteCallback(mRouteCallback);
        }
    }

}
