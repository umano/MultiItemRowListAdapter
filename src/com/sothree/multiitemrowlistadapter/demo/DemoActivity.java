package com.sothree.multiitemrowlistadapter.demo;

import com.sothree.multiitemrowlistadapter.MultiItemRowListAdapter;
import com.sothree.slidinguppaneldemo.R;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DemoActivity extends ListActivity implements
LoaderManager.LoaderCallbacks<Cursor>,
OnItemClickListener {

    private ContactsAdapter mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mContactsAdapter = new ContactsAdapter(this);
        // We need to set the item on click listener on the original adapter instead of the ListView
        // since each row in MultiItemRowListAdapter has multiple items
        mContactsAdapter.setOnItemClickListener(this);

        int spacing = (int)getResources().getDimension(R.dimen.spacing);
        int itemsPerRow = getResources().getInteger(R.integer.items_per_row);
        MultiItemRowListAdapter wrapperAdapter = new MultiItemRowListAdapter(this, mContactsAdapter, itemsPerRow, spacing);
        setListAdapter(wrapperAdapter);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactsAdapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection;
        String displayNameColumn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            displayNameColumn = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
            projection= new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                displayNameColumn
            };

        } else {
            displayNameColumn = ContactsContract.Contacts.DISPLAY_NAME;
            projection= new String[] {
                ContactsContract.Contacts._ID,
                displayNameColumn
            };
        }
        String selection =
                ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1' AND " +
                "NULLIF(" + displayNameColumn +", '') IS NOT NULL";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.STARRED + " DESC, " + ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        return new CursorLoader(DemoActivity.this, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mContactsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mContactsAdapter.swapCursor(null);
    }
    private static class ContactsAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        private Cursor mCursor;
        private OnItemClickListener mOnItemClickListener;

        private final class ContactViewHolder {
            public TextView mContactName;
            public ImageView mContactImage;
        }

        public ContactsAdapter(Activity context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        public Cursor swapCursor(Cursor newCursor) {
            if (mCursor != null && mCursor != newCursor) {
                mCursor.close();
            }
            mCursor = newCursor;
            notifyDataSetChanged();
            return mCursor;
        }

        @Override
        public int getCount() {
            if (mCursor != null && mCursor.getCount() > 0) {
                return mCursor.getCount();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mCursor != null) {
                mCursor.moveToPosition(position);
                return mCursor;
            }
            return null;
        }

        //return the header view, if it's in a section header position
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ContactViewHolder viewHolder;
            if (convertView == null || !(convertView.getTag() instanceof ContactViewHolder)) {
                convertView = mInflater.inflate(R.layout.include_list_item_contact, parent, false);
                viewHolder = new ContactViewHolder();
                viewHolder.mContactName = (TextView) convertView.findViewById(R.id.lbl_contact_name);
                viewHolder.mContactImage = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ContactViewHolder)convertView.getTag();
            }
            Cursor c = (Cursor)getItem(position);

            if (c == null) {
                return convertView;
            }

            int photoColumnIndex;
            int nameColumnIndex;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                photoColumnIndex = mCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
                nameColumnIndex = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            } else {
                photoColumnIndex = mCursor.getColumnIndex(ContactsContract.Contacts._ID);;
                nameColumnIndex = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            }

            if (nameColumnIndex >= 0) {
                viewHolder.mContactName.setText(c.getString(nameColumnIndex));
            }

            if (photoColumnIndex >= 0 && c.getString(photoColumnIndex) != null) {
                String photoData = c.getString(photoColumnIndex);
                // Creates a holder for the URI.
                Uri thumbUri;
                // If Android 3.0 or later
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
                    thumbUri = Uri.parse(photoData);
                } else {
                    // Prior to Android 3.0, constructs a photo Uri using _ID
                    /*
                     * Creates a contact URI from the Contacts content URI
                     * incoming photoData (_ID)
                     */
                    final Uri contactUri = Uri.withAppendedPath(
                            Contacts.CONTENT_URI, photoData);
                    /*
                     * Creates a photo URI by appending the content URI of
                     * Contacts.Photo.
                     */
                    thumbUri =
                            Uri.withAppendedPath(
                                    contactUri, Photo.CONTENT_DIRECTORY);
                }
                viewHolder.mContactImage.setImageURI(thumbUri);
            } else {
                viewHolder.mContactImage.setImageResource(0);
            }

            final View clickedView = convertView;
            // set the on click listener for each of the items
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(null, clickedView, position, position);
                    }

                }
            });

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public void close() {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (mContactsAdapter != null) {
            Cursor c = (Cursor)mContactsAdapter.getItem(position);
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            Toast t = Toast.makeText(this, "Clicked " + name, Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
