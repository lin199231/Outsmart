package findix.meetingreminder.sms;

import java.util.Random;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.telephony.PhoneNumberUtils;

public class Contact {
	public static String getContactId(Context context, String number) {
		Cursor c = null;
		try {
			c = context.getContentResolver().query(Phone.CONTENT_URI,
					new String[] { Phone.CONTACT_ID, Phone.NUMBER }, null,
					null, null);
			if (c != null && c.moveToFirst()) {
				while (!c.isAfterLast()) {

					if (PhoneNumberUtils.compare(number, c.getString(1))) {
						return c.getString(0);
					}
					c.moveToNext();
				}
			}
		} catch (Exception e) {
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return null;
	}

	public static String getDisplayName(Context context, String contacts_id) {
		Cursor c = null;
		try {
			c = context.getContentResolver().query(Contacts.CONTENT_URI,
					new String[] { Contacts.DISPLAY_NAME },
					Contacts._ID + "=" + contacts_id, null, null);
			if (c != null && c.moveToFirst()) {
				return c.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return null;
	}

	public static Bitmap getContactsPhoto(Context context, String contactId) {
		Cursor c = null;
		// load icon
		byte[] icon = null;
		try {
			// get contact photo URI
			Uri refUri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactId);
			refUri = Uri.withAppendedPath(refUri,
					ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
			// get cursor
			c = context.getContentResolver().query(refUri,
					new String[] { Photo.PHOTO }, null, null, null);
			if (c != null && c.moveToFirst()) {
				icon = c.getBlob(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		if (icon != null) {
			try {
				return BitmapFactory.decodeByteArray(icon, 0, icon.length);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
				return null;
			}
		}
		return null;
	}
}
