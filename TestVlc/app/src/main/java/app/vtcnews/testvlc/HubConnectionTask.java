package app.vtcnews.testvlc;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.signalr.HubConnection;


public class HubConnectionTask extends AsyncTask<HubConnection, Void, String> {
    private HubCallBack hubCallBack;
    private String LOG_TAG = "HubConnectionTask";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    public HubConnectionTask(HubCallBack callBack){
        this.hubCallBack = callBack;
    }
    @Override
    protected String doInBackground(HubConnection... hubConnections) {

        try {
            HubConnection hubConnection = hubConnections[0];
            hubConnection.start().blockingAwait();
            return hubConnection.getConnectionId();
        } catch (Exception e) {
            Log.e(LOG_TAG, "error connecting tto hub");
            return null;
        }
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        hubCallBack.onCallBack(aVoid);
    }
}
