package palindrome;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.JSONArray;

import org.json.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Christopher Klein
 */
public class Palindrome {
    
    protected static ArrayList firstNameArray = new ArrayList(); //array to store first name
    protected static ArrayList lastNameArray = new ArrayList(); //array to store last name
    protected static ArrayList palindromes = new ArrayList();//array to store count of palindromes
    protected static int count = 0;
    
    public static void search(String userText) throws Exception //search without limit
    {
        String result = null; //Will hold JSON data returned from URL
        URL url = new URL("https://api.nasa.gov/patents/content?query=" + userText + "&api_key=DEMO_KEY"); //Build URL request
        
        try//attempts to read data from URL
        {
            InputStream input = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            result = reader.readLine();
            input.close();

            JSONObject json = new JSONObject(result); //JSON Object reading
            JSONArray jsonResults = new JSONArray(json.getJSONArray("results").toString()); //JSON Array holding ALL results

            for(int i = 0; i < jsonResults.length(); i++) //gets length from total results
            {
                json = (JSONObject) jsonResults.get(i);
                JSONArray jsonResultAuthor = new JSONArray(json.getJSONArray("innovator").toString());//JSON Array holding ALL info of 1 result
                
                for(int k = 0; k < jsonResultAuthor.length(); k++)//gets length from total authors
                {
                    JSONObject jsonParsed = (JSONObject) jsonResultAuthor.getJSONObject(k);//gets author info of specific index of the array
                    firstNameArray.add(jsonParsed.getString("fname"));
                    lastNameArray.add(jsonParsed.getString("lname"));
                }
                
                //System.out.println(firstNameArray.get(i) + " " + lastNameArray.get(i));
            }
            calculate();//Calculate Palindrome
        } 
        catch (IOException | JSONException e)
        {
            System.out.println("Error: " + e);
        }//end of try
    }//end search
    
    public static void search(String userText, int userLimit) throws Exception //search with limit overloaded method
    {
        if (userLimit < 1 || userLimit > 5)
        {
            System.out.println("Please select a limit between 1 and 5.");
        }
        else
        {
            String result = null; //Will hold JSON data returned from URL
            URL url = new URL("https://api.nasa.gov/patents/content?query=" + userText + "&limit=" + userLimit + "&api_key=DEMO_KEY"); //Build URL request
        
            try//attempts to read data from URL
            {
                InputStream input = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                result = reader.readLine();
                input.close();

                JSONObject json = new JSONObject(result); //JSON Object reading
                JSONArray jsonResults = new JSONArray(json.getJSONArray("results").toString()); //JSON Array holding ALL results

                for(int i = 0; i < jsonResults.length(); i++) //gets length from total results
                {
                    json = (JSONObject) jsonResults.get(i);
                    JSONArray jsonResultAuthor = new JSONArray(json.getJSONArray("innovator").toString());//JSON Array holding ALL info of 1 result
                    for(int k = 0; k < jsonResultAuthor.length(); k++)//gets length from total authors
                    {
                        JSONObject jsonParsed = (JSONObject) jsonResultAuthor.getJSONObject(k);//gets author info of specific index of the array
                        firstNameArray.add(jsonParsed.getString("fname"));
                        lastNameArray.add(jsonParsed.getString("lname"));                    
                    }
                    
                    //System.out.println(firstNameArray.get(i) + " " + lastNameArray.get(i));
                
                }
                
                //calculate();//Calculate Palindromes
            } 
            catch (IOException | JSONException e)
            {
                System.out.println("Error: " + e);
            }//end of Try

        }//end if/else for detecting userlimit

    }//end search
    
    
    public static void calculate() //Calculate palindromes
    {
        for (int i = 0; i < firstNameArray.size(); i++)
        {
            String name = firstNameArray.get(i) + "" + lastNameArray.get(i);
            name = name.toLowerCase();
            char[] charArray = name.toCharArray();
            int uniqueChars = name.length();
            int count = 0;
            
            //loops through each character in char array, if duplicate char then deduct 1 from unique chars to count unique characters
            for (int j = 0; j < charArray.length; j++)
            {
                if(j != name.indexOf(charArray[j]))
                {
                    uniqueChars--;
                }
            }
            
            //create logic for if odd or even string
            if ((name.length() % 2) == 0) // if even
            {
                count = (int) Math.pow(uniqueChars, (name.length()/ 2));//palindrome formula for even
            }
            else //if odd, numbers are either even or odd
            {
                count =(int) Math.pow(uniqueChars, ((name.length() + 1) / 2));//palindrome formula for odd
            }
            palindromes.add(count);
            //System.out.println(name + " has " + count + " palindromes!");
        } //end loop through each name
        
        //output(); // begin output to JSON
    } //end method

    public static String output() //outputs to JSON format
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < firstNameArray.size(); i++)
        {
            StringBuilder firstName = new StringBuilder(firstNameArray.get(i).toString());
            StringBuilder lastName = new StringBuilder(lastNameArray.get(i).toString());
            
            //capitalize first letter in first name
            char tempChar = firstName.charAt(0);
            String tempLetter = tempChar + ""; // get char into a string
            tempLetter = tempLetter.toUpperCase();            
            firstName.setCharAt(0, (tempLetter).charAt(0));
            
            //capitalize first setter in last name
            tempChar = lastName.charAt(0);
            tempLetter = tempChar + ""; // get char into a string
            tempLetter = tempLetter.toUpperCase();
            lastName.setCharAt(0, (tempLetter).charAt(0));
            
            //push strings together into proper format ex: Chris Klein
            String tempName = firstName + " " + lastName;
            //append json formatted data to string
            
            if (i == (firstNameArray.size() - 1))
            {
                sb.append("{ \"name\": \"" + tempName + "\", \"count\": " + palindromes.get(i) + " }");//remove , if last one
            }
            else
            {            
                sb.append("{ \"name\": \"" + tempName + "\", \"count\": " + palindromes.get(i) + " },");
            }
            
            //System.out.println(palindromes.get(i));
        }
        sb.append("]");
        return sb.toString();
        //System.out.println(sb);
    }
    
    public static void main(String[] args) throws Exception {
        //todo create method to input string from web service external
        //search("Electricity");
        //calculate();
        //output();
        //System.out.println(Palindrome("Electricity"));
    }
    
    @RequestMapping(value="/palindromes?search={searchID}")
    @ResponseBody
    public String Palindromes(@PathVariable("searchID") String searchTerm) throws Exception
    {
        search(searchTerm);
        calculate();
        return output();
    }
    
    @RequestMapping(value="/palindromes?search={searchID}&limit={userLimit}")
    @ResponseBody    
    public String Palindromes(@PathVariable("searchID") String searchTerm, @PathVariable("userLimit") int limit) throws Exception
    {
        search(searchTerm, limit);
        calculate();
        return output();
    }
    
}
