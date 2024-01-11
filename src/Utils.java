import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
public class Utils {

  static private final String CAT_FACT_URL = "https://catfact.ninja/fact";
  static private final String DOG_FACT_URL = "https://dogapi.dog/api/v2/facts";
  static private final String JOKE_URL = "https://official-joke-api.appspot.com/random_joke";
  static private final String ACTIVITY_URL = "https://www.boredapi.com/api/activity";

  static Map<String, String[]> apiVersions = new HashMap<String, String[]>() {{
    this.put("CatFact", new String[]{"v1", "v2"});
    this.put("DogFact", new String[]{"v1", "v2"});
    this.put("Joke", new String[]{"v1", "v2"});
    this.put("ActivityIdea", new String[]{"v1", "v2"});
    this.put("Pythagorean", new String[]{"v1"});
  }};

  static Map<String, String[]> nodeTypes= new HashMap<String, String[]>() {{
    put("TYPE_A", new String[]{"CatFact", "DogFact", "Joke", "Math"});
    put("TYPE_B", new String[]{"DogFact", "Joke"});
    put("TYPE_C", new String[]{"Joke", "ActivityIdea"});
    put("TYPE_D", new String[]{"Joke", "Pythagorean"});
  }};

  public static String randomApiRequest(String apiEndpoint) {
    String message = "";
    try {
      URL url = new URL(apiEndpoint);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("GET");

      int responseCode = httpURLConnection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();
        message = response.toString();
      } else {
        System.out.println("HTTP request failed: " + responseCode);
      }
      httpURLConnection.disconnect();

    } catch (Exception e) {
      e.printStackTrace();
      message = "HTTP request failed.";
    }
    return message;
  }

  public static String getCatFact(String version) {
    String message = "";
    switch(version){
      case("v1"):
        message = randomApiRequest(CAT_FACT_URL);
        break;
      case("v2"):
        try{
          message = new JSONObject(randomApiRequest(CAT_FACT_URL)).getString("fact");
        }catch (Exception e){
          message = "";
        }
        break;
      default:
        message = randomApiRequest(CAT_FACT_URL);
        break;
    }
    return message;
  }

  public static String getDogFact(String version) {
    String message = "";
    switch(version){
      case("v1"):
        message = randomApiRequest(DOG_FACT_URL);
        break;
      case("v2"):
        try{
          message = new JSONObject(randomApiRequest(DOG_FACT_URL))
                  .getJSONArray("data")
                  .getJSONObject(0)
                  .getJSONObject("attributes")
                  .getString("body");
        }catch (Exception e){
          message = "";
        }
        break;
      default:
        message = randomApiRequest(DOG_FACT_URL);
        break;
    }
    return message;
  }

  public static String getJoke(String version) {
    String message = "";
    switch(version){
      case("v1"):
        message = randomApiRequest(JOKE_URL);
        break;
      case("v2"):
        try{
          String apiResponse = randomApiRequest(JOKE_URL);
          message = new JSONObject(apiResponse).getString("setup") + " ... " + new JSONObject(apiResponse).getString("punchline");
        }catch (Exception e){
          message = "";
        }
        break;
      default:
        message = randomApiRequest(JOKE_URL);
        break;
    }
    return message;
  }

  public static String getActivityIdea(String version) {
    String message = "";
    switch(version){
      case("v1"):
        message = randomApiRequest(ACTIVITY_URL);
        break;
      case("v2"):
        try{
          message = new JSONObject(randomApiRequest(ACTIVITY_URL)).getString("activity");
        }catch (Exception e){
          message = "";
        }
        break;
      default:
        message = randomApiRequest(ACTIVITY_URL);
        break;
    }
    return message;
  }

  public static String getHypotenuse(int a, int b, String version) {
    if (a <= 0 || b <= 0) {
      return "Values are negative.";
    }
    return String.valueOf(Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));
  }

  public static String computeMethod(String[] clientMessage) {
    String methodName = clientMessage[1];
    String version = clientMessage[2];
    switch (methodName) {
      case "CatFact":
        return Utils.getCatFact(version);
      case "DogFact":
        return Utils.getDogFact(version);
      case "Joke":
        return Utils.getJoke(version);
      case "ActivityIdea":
        return Utils.getActivityIdea(version);
      case "Pythagorean":
        if (clientMessage.length>4){
          int a = Integer.valueOf(clientMessage[3]);
          int b = Integer.valueOf(clientMessage[4]);
          return Utils.getHypotenuse(a,b, version);
        }else{
          return "Provide values for a and b.";
        }

      default:
        return "";
    }
  }

  public static String getMethodsForNodeType(String nodeType) {
    String[] services = nodeTypes.getOrDefault(nodeType, new String[0]);

    StringBuilder result = new StringBuilder();
    for (String serviceName : services) {
      if (result.length() > 0) {
        result.append(", ");
      }
      result.append(serviceName).append(" [").append(String.join(", ", apiVersions.get(serviceName))).append("]");
    }

    return "node services: " + result.toString();
  }
}
