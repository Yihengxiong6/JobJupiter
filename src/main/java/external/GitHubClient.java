package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;

public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	/**
	 * according to the request in from the client in the jobsearch class, we send a get request to github job api
	 * and then we will have an JSONArray but is very dirty.
	 * So we call the function getItemList within the current class to sort the JSONArray to a list of item(job) object which have
	 * the attributes we want
	 * @param lat
	 * @param lon
	 * @param keyword
	 * @return
	 */
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // RICK SUN -> RICK + SUN 这样才符合标准 RICK+SUN -> RICK%BSUN
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = String.format(URL_TEMPLATE, keyword, lat, lon); // REGEXregular expression
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		
		ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>(){ // transfer the string response to a predfined 
			//format
			
				@Override
				public List<Item> handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status != 200) {
						return new ArrayList<Item>();
					}
					HttpEntity entity = response.getEntity();
					if (entity == null) {
						return new ArrayList<Item>();
					}
					String responseBody = EntityUtils.toString(entity); // transfer the body the string
					System.out.println(responseBody);
					JSONArray array = new JSONArray(responseBody); // get to JSONArray 
					return getItemList(array);
				}
		};
		try {
			List<Item> itemList = httpclient.execute(httpget, responseHandler);
			return itemList; 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<Item>(); 
	}
	
	
	/**
	 * Transform an JSONArray(array of job info in JSONObject format) to an array of item object(job) list. 
	 * And at the same time, we extract more accurate keywords from each JSONObject and add them to the keywords field of item object
	 * @param array this is the jsonarray from the github api
	 * @return
	 */
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		
		// We need to extract keywords for every JSONObeject from description since GitHub API doesn't return keywords.
		for (int i = 0; i < array.length(); i ++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if (description.contentEquals("") || description.contentEquals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			}else {
				descriptionList.add(description);
			}
		}
		
		// We need to get keywords from multiple text in one request since
		// extract the keywords for each 
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));

		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Item item = Item.builder()
					.itemId(getStringFieldOrEmpty(object, "id"))
					.name(getStringFieldOrEmpty(object, "title"))
					.address(getStringFieldOrEmpty(object, "location"))
					.url(getStringFieldOrEmpty(object, "url"))
					.imageUrl(getStringFieldOrEmpty(object, "company_logo"))
					.keywords(new HashSet<String>(keywords.get(i)))
					.build();
			itemList.add(item);
		}

		return itemList;
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

	

}

