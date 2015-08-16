package com.dulcerefugio.app.entunombre.logic;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Slightly more abstract {@link android.content.AsyncQueryHandler} that helps keep a
 * {@link java.lang.ref.WeakReference} back to a listener. Will properly close any
 * {@link android.database.Cursor} if the listener ceases to exist.
 * <p>
 * This pattern can be used to perform background queries without leaking
 * {@link android.content.Context} objects.
 */
public class CustomAsyncHandler extends CustomAsyncQueryHandler {
    private AsyncQueryListener mListener;

    /**
     * Interface to listen for completed query operations.
     */
    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
        void onBulkInsertComplete(int token, Object cookie, int result);
    }

    public CustomAsyncHandler(Context context, AsyncQueryListener listener) {
        super(context.getContentResolver());
        setQueryListener(listener);
    }

    /**
     * Assign the given {@link com.dulcerefugio.app.entunombre.logic.CustomAsyncHandler.AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(AsyncQueryListener listener) {
        mListener = listener;
    }

    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final AsyncQueryListener listener = mListener;
        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onBulkInsertComplete(int token, Object cookie, int result) {
        super.onBulkInsertComplete(token, cookie, result);

        Log.d("CustomAsyncHandler",token + "");
        final AsyncQueryListener listener = mListener;

        if (listener != null) {
            listener.onBulkInsertComplete(token, cookie, result);
        }else{
            //throw new NotImplementedException("You must define AsyncHandler Listener");
        }
    }
}

