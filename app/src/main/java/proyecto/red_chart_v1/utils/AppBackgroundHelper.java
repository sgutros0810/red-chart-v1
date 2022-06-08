package proyecto.red_chart_v1.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import java.util.List;

import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;


public class AppBackgroundHelper {

    //Cambia el estado
    public static void online(Context context, boolean status) {
        UsersProvider usersProvider = new UsersProvider();
        AuthProvider authProvider = new AuthProvider();

        //Que el id del usuario no sea null
        if(authProvider.getId() != null){

            //Si la app se envió a background(la puso en segundo plano o la cerró)
            if(isApplicationSentToBackground(context)){
                //actualiza el campo 'online' a 'true'
                usersProvider.updateOnline(authProvider.getId(), status);

            } else if(status) {
                //actualiza el campo 'online' a 'true'
                usersProvider.updateOnline(authProvider.getId(), status);
            }

        }

    }

    //Método para saber si puso la app en segundo plano o la cerró
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}


