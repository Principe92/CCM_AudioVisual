package prince.app.ccm.tools;

import java.util.ArrayList;

import prince.app.ccm.Dialog_Call;
import prince.app.ccm.Dialog_Email;
import prince.app.ccm.R;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
	
	private ArrayList<ContactHolder> contactList;
	private ActionBarActivity context;
	private static final String TAG = ContactAdapter.class.getSimpleName();
	
	public ContactAdapter(ArrayList<ContactHolder> list, ActionBarActivity ct){
		this.contactList = list;
		this.context = ct;
	}

	@Override
	public int getItemCount() {
		return contactList.size();
	}

	@Override
	public void onBindViewHolder(ContactViewHolder viewHolder, int position) {
		final ContactHolder contact = contactList.get(position);
		final ContactViewHolder mHolder = viewHolder;
		final int index = position;
		
		mHolder.contactName.setText(contact.name);
		String telephone = "";
		String email = "";
		
		String number[] = contact.numbers;
		String emails[] = contact.emails;
		for (String arg:number){
			if (telephone.isEmpty()) telephone = arg;
			else telephone = telephone + "\n" + arg;
		}
		
		for (String arg:emails){
			if (email.isEmpty()) email = arg;
			else email = email + "\n" + arg;
		}
		
		mHolder.contactNumber.setText(telephone);
		mHolder.contactEmail.setText(email);
		mHolder.contactRole.setText(contact.role);
		mHolder.contactImage.setImageDrawable(contact.image);
		
		// Email button click listener
		mHolder.callContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (contact.numbers.length > 1){
					DialogFragment dialog = Dialog_Call.newInstance(contact.numbers);
					dialog.show(context.getSupportFragmentManager(), TAG);
				}
				
				else if (contact.numbers.length == 1){
					Intent phoneIntent = new Intent(Intent.ACTION_CALL);
					phoneIntent.setData(Uri.parse("tel:" + contact.numbers[0]));
					
					try {
				         context.startActivity(phoneIntent);
				      } catch (android.content.ActivityNotFoundException ex) {
				         Toast.makeText(context, context.getResources().getString(R.string.call_failed), Toast.LENGTH_SHORT).show();
				      }
				}
				
			}});
		
		// Message button click listener
		mHolder.messageContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "send message", Toast.LENGTH_SHORT).show();
			}});
		
		// Email button click listener
		mHolder.emailContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (contact.emails.length > 1){
					DialogFragment dialog = Dialog_Email.newInstance(contact.emails);
					dialog.show(context.getSupportFragmentManager(), TAG);
				}
				
				else if (contact.emails.length == 1){
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setData(Uri.parse("mailto:"));
					emailIntent.setType("text/plain");
					emailIntent.putExtra(Intent.EXTRA_EMAIL  , contact.emails);
					
					try {
				         context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				      } catch (android.content.ActivityNotFoundException ex) {
				         Toast.makeText(context, context.getResources().getString(R.string.email_failed), Toast.LENGTH_SHORT).show();
				      }
				}
			}});
		
		// Menu Overflow button click listener
		mHolder.moreOptions.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "more options", Toast.LENGTH_SHORT).show();
			}});
	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup view, int arg1) {
		View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_card_contacts, view, false);
		return new ContactViewHolder(item);
	}
	
	public final static class ContactViewHolder extends RecyclerView.ViewHolder{
		protected ImageView contactImage;
		protected TextView contactName;
		protected TextView contactNumber;
		protected TextView contactEmail;
		protected TextView contactRole;
		protected ImageButton callContact;
		protected ImageButton messageContact;
		protected ImageButton emailContact;
		protected ImageButton moreOptions;

		public ContactViewHolder(View itemView) {
			super(itemView);
			contactImage = (ImageView) itemView.findViewById(R.id.image_card_contacts);
			contactName = (TextView)  itemView.findViewById(R.id.text_contact_name);
			contactNumber = (TextView)  itemView.findViewById(R.id.text_contact_number);
			contactEmail = (TextView)  itemView.findViewById(R.id.text_contact_email);
			contactRole = (TextView)  itemView.findViewById(R.id.text_contact_role);
			callContact = (ImageButton)     itemView.findViewById(R.id.btn_contact_call);
			messageContact = (ImageButton)   itemView.findViewById(R.id.btn_contact_message);
			emailContact = (ImageButton)   itemView.findViewById(R.id.btn_contact_email);
			moreOptions = (ImageButton)   itemView.findViewById(R.id.btn_contact_options);
		}
	}
	
}
