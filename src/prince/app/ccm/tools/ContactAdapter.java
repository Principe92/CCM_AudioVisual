package prince.app.ccm.tools;

import java.util.ArrayList;

import prince.app.ccm.Dialog_Call;
import prince.app.ccm.Dialog_Email;
import prince.app.ccm.Dialog_Message;
import prince.app.ccm.Dialog_OnlyRetry;
import prince.app.ccm.Dialog_OnlyRetry.DialogCallback;
import prince.app.ccm.R;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
//	private static final String TAG = ContactAdapter.class.getSimpleName();
	
	private ArrayList<ContactHolder> contactList;
	private ActionBarActivity context;
	private static final String EMAIL_TAG = "email_tag";
	private static final String MESSAGE_TAG = "message_tag";
	private static final String CALL_TAG = "call_tag";
	private static final String NETWORK_TAG = "net_error";
	
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
		
		// Call button click listener
		mHolder.callContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (!Tool.getInstance().isAirplaneMode()){
					if (contact.numbers.length > 1){
						// Inflate Dialog_Call
						FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
						Fragment prev = context.getSupportFragmentManager().findFragmentByTag(CALL_TAG);
						if (prev != null) ft.remove(prev);
						
						
						DialogFragment dialog = Dialog_Call.newInstance(contact.numbers);
						dialog.show(ft, CALL_TAG);
					}
					
					else if (contact.numbers.length == 1){
						Tool.getInstance().makeCall(contact.numbers[0]);
					}
				}else{
					FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
					Fragment prev = context.getSupportFragmentManager().findFragmentByTag(NETWORK_TAG);
					if (prev != null) ft.remove(prev);
					
				
					Dialog_OnlyRetry df = Dialog_OnlyRetry.newInstance(context.getResources().getString(R.string.airplane_mode_on));
					df.setCallback(new DialogCallback(){

						@Override
						public void onRetry() {
							Toast.makeText(context, "RETRY", Toast.LENGTH_SHORT).show();
						}
						
					});
					df.show(ft, NETWORK_TAG);
				}
			}});
		
		
		// Message button click listener
		mHolder.messageContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (contact.numbers.length > 1){
					// Inflate Dialog_Call
					FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
					Fragment prev = context.getSupportFragmentManager().findFragmentByTag(MESSAGE_TAG);
					if (prev != null) ft.remove(prev);
					
					
					DialogFragment dialog = Dialog_Message.newInstance(contact.numbers);
					dialog.show(ft, MESSAGE_TAG);
				}
				
				else if (contact.numbers.length == 1){
					String num = contact.numbers[0];
					Tool.getInstance().sendSMS(num);
				}
			}});
		
		
		// Email button click listener
		mHolder.emailContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (contact.emails.length > 1){
					// Inflate Dialog_Email
					FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
					Fragment prev = context.getSupportFragmentManager().findFragmentByTag(EMAIL_TAG);
					if (prev != null) ft.remove(prev);
					
					
					DialogFragment dialog = Dialog_Email.newInstance(contact.emails);
					dialog.show(ft, EMAIL_TAG);
				}
					
				else if (contact.emails.length == 1){
					Tool.getInstance().sendEmail(contact.emails);
				}
			}});
		
		// Menu Overflow button click listener
		mHolder.moreOptions.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "more options", Toast.LENGTH_SHORT).show();
			}});
	}
	
	public void dismissDialogs(){
		// Dismiss any
		FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
						
		Fragment call = context.getSupportFragmentManager().findFragmentByTag(CALL_TAG);
		if (call != null) ft.remove(call);
		
		Fragment sms = context.getSupportFragmentManager().findFragmentByTag(MESSAGE_TAG);
		if (sms != null) ft.remove(sms);
		
		Fragment email = context.getSupportFragmentManager().findFragmentByTag(EMAIL_TAG);
		if (email != null) ft.remove(email);
		
		Fragment net = context.getSupportFragmentManager().findFragmentByTag(NETWORK_TAG);
		if (net != null) ft.remove(net);
		
		ft.commit();
	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup view, int arg1) {
		View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_card_contacts, view, false);
		return new ContactViewHolder(item);
	}
	
	/**
	 * ViewHolder class for each contact in the view
	 * @author Princewill Okorie
	 *
	 */
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
