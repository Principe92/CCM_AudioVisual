package prince.app.ccm.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncSession extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = AsyncSession.class.getSimpleName();
	public static final String MAIN_PAGE = "http://www.iglesiamallorca.com/";
	public static final String USERNAME = "usuario";
	public static final String PASSWORD = "contrasena";
	public static final String LOGIN_PAGE = "http://www.iglesiamallorca.com/av_login.php";
	public static final String SESSION_NAME = "PHPSESSID";

	private static AsyncCallback mCallback;
	private final String mUser;
	private final String mPassword;
	private HttpURLConnection conn;
	private final String USER_AGENT = "Mozilla/5.0";

	public static interface AsyncCallback {
		public void onPostExecute(boolean result);

		public void onCancel();
	}

	public static void setCallback(AsyncCallback callback) {
		mCallback = callback;
	}

	public AsyncSession(String username, String password) {
		this.mUser = username;
		this.mPassword = password;
	}

	private String getLoginFormAction(String html) {
		if (!html.isEmpty()) {
			Document doc = Jsoup.parse(html);

			// Google form id
			Elements loginaction = doc.getElementsByTag("form");
			Element form = loginaction.first();
			return MAIN_PAGE + form.attr("action");
		}

		return "";
	}

	private String GetPageContent(String url) throws Exception {

		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		int responseCode = conn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		}

		return "";
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		try {
			String page = GetPageContent(LOGIN_PAGE);

			String login = getLoginFormAction(page);

			Connection.Response loginForm = Jsoup.connect(login)
					.ignoreContentType(true).userAgent(USER_AGENT)
					.referrer(LOGIN_PAGE).timeout(12000).followRedirects(true)
					.method(Connection.Method.GET).execute();

			Connection.Response loginFormFilled = Jsoup.connect(login)
					.ignoreContentType(true).userAgent(USER_AGENT)
					.followRedirects(true).referrer(LOGIN_PAGE)
					.data(USERNAME, mUser)
					.data(PASSWORD, mPassword)
					.cookies(loginForm.cookies())
					.method(Connection.Method.POST).execute();
			
			int statusCode = loginFormFilled.statusCode();
			
			if (statusCode == HttpURLConnection.HTTP_OK){
				Map<String, String> cookies = loginFormFilled.cookies();
				
				if (cookies.containsKey(SESSION_NAME)){
					Log.e(TAG, loginForm.cookies().toString());
					Log.e(TAG, cookies.toString());
					return true;
				}
			}
			
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (mCallback != null){
			mCallback.onPostExecute(result);
		}
	}
	
	@Override
	protected void onCancelled(){
		if (mCallback != null){
			mCallback.onCancel();
		}
	}

}
