package de.symeda.sormas.app.backend.config;

import android.util.Log;

import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.SurveillanceActivity;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.user.SyncUsersTask;
import de.symeda.sormas.app.util.SyncInfrastructureTask;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 10.08.2016.
 */
public final class ConfigProvider {

    private static String KEY_USER_UUID = "userUuid";
    private static String KEY_SERVER_REST_URL = "serverRestUrl";

    public static ConfigProvider instance = null;

    public static void init() {
        if (instance != null) {
            Log.e(ConfigProvider.class.getName(), "ConfigProvider has already been initalized");
        }
        instance = new ConfigProvider();
    }

    private String serverRestUrl;
    private User user;

    public static User getUser() {
        if (instance.user == null)
            synchronized (ConfigProvider.class) {
                if (instance.user == null) {

                    // get user from config
                    Config config = DatabaseHelper.getConfigDao().queryForId(KEY_USER_UUID);
                    if (config != null) {
                        instance.user = DatabaseHelper.getUserDao().queryUuid(config.getValue());
                    }

                    if (instance.user == null) {
                        // no user found. Take first surveillance officer...
                        List<User> users = DatabaseHelper.getUserDao().queryForAll();
                        // this seems to lock the thread and isn't necessary anyway
//                        if (users.size() == 0) {
//                            // no user existing yet? Try to load them from the server
//                            try {
//                                new SyncUsersTask().execute().get();
//                            } catch (InterruptedException e) {
//                                Log.e(ConfigProvider.class.getName(), e.toString(), e);
//                            } catch (ExecutionException e) {
//                                Log.e(ConfigProvider.class.getName(), e.toString(), e);
//                            }
//                            users = DatabaseHelper.getUserDao().queryForAll();
//                        }
                        for (User dbUser : users) {
                            if (UserRole.SURVEILLANCE_OFFICER.equals(dbUser.getUserRole())) {
                                // got it
                                setUser(dbUser);
                                break;
                            }
                        }
                    }
                }
            }
        return instance.user;
    }

    public static void setUser(User user) {
        if (user != null && user.equals(instance.user))
            return;

        boolean wasNull = instance.user == null;
        instance.user = user;
        DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USER_UUID, user.getUuid()));

        if (!wasNull) {
            try {
                TableUtils.clearTable(DatabaseHelper.getCaseDao().getConnectionSource(), Case.class);
            } catch (SQLException e) {
                Log.e(ConfigProvider.class.getName(), "User switch: Clearing cases failed");
            }
        }
    }

    public static String getServerRestUrl() {
        if (instance.serverRestUrl == null)
            synchronized (ConfigProvider.class) {
                if (instance.serverRestUrl == null) {

                    // get user from config
                    Config config = DatabaseHelper.getConfigDao().queryForId(KEY_SERVER_REST_URL);
                    if (config != null) {
                        instance.serverRestUrl = config.getValue();
                    }

                    if (instance.serverRestUrl == null) {
                        setServerUrl("https://sormas.symeda.de/sormas-rest/");
                    }
                }
            }
        return instance.serverRestUrl;
    }

    public static void setServerUrl(String serverRestUrl) {
        if (serverRestUrl != null && serverRestUrl.equals(instance.serverRestUrl))
            return;

        boolean wasNull = instance.serverRestUrl == null;
        instance.serverRestUrl = serverRestUrl;
        DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_SERVER_REST_URL, serverRestUrl));

        if (!wasNull) {
            // reset everything
            RetroProvider.reset();
            DatabaseHelper.clearTables();
            try {
                new SyncInfrastructureTask().execute().get();
                new SyncPersonsTask().execute().get();
                new SyncCasesTask().execute().get();
            } catch (InterruptedException e) {
                Log.e(ConfigProvider.class.getName(), e.toString(), e);
            } catch (ExecutionException e) {
                Log.e(ConfigProvider.class.getName(), e.toString(), e);
            }
        }
    }
}