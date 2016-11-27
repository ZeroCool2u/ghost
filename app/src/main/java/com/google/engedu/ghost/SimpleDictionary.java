package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import static com.google.engedu.ghost.GhostActivity.USER_FIRST_TURN;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random rand = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        int dictSize = words.size();
        if (prefix.isEmpty()){
            int randInt = rand.nextInt(dictSize);
            return words.get(randInt);
        }
        else{
            return binarySearch(0,dictSize,prefix);
        }

    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        int dictSize = words.size();
        if (prefix.isEmpty()){
            int randInt = rand.nextInt(dictSize);
            return words.get(randInt);
        }
        else{
            return advancedBinarySearch(0,dictSize,prefix);
        }
    }

    // Returns target prefix if it is present in arr[l..r], else
    // return null
    private String binarySearch(int l, int r, String prefix)
    {
        if (r>=l){
            int mid = l + (r - l)/2;

            // Determine if the middle element and our target are equal.
            String midWord = words.get(mid);
            if (midWord.startsWith(prefix))
                return midWord;

            // Search left subarray.
            if (midWord.compareTo(prefix) > 0)
                return binarySearch(l, mid-1, prefix);

            //Search right subarray.
            return binarySearch(mid+1, r, prefix);
        }

        // We reach here when element is not present in array
        return null;
    }

    private String advancedBinarySearch(int l, int r, String prefix)
    {
        if (r>=l){
            int mid = l + (r - l)/2;

            // Determine if the middle element and our target are equal.
            //If so, we perform a linear search above and below the target value s.t. all words
            //beginning with our prefix are added to the goodWords even or odd list.
            //Finally, return a randomly picked word based on which player went first.
            String midWord = words.get(mid);
            if (midWord.startsWith(prefix)){
                ArrayList<String> evenWords = new ArrayList<>();
                ArrayList<String> oddWords = new ArrayList<>();
                if (midWord.length() % 2 == 0) evenWords.add(midWord);
                else{
                    oddWords.add(midWord);
                }
                int i = mid + 1;
                int k = mid - 1;
                while(words.get(i).startsWith(prefix) && !prefix.equals("")){
                    if(words.get(i).length() % 2 == 0) evenWords.add(words.get(i));
                    else evenWords.add(words.get(i));
                    i++;
                }
                while(words.get(k).startsWith(prefix) && !prefix.equals("")){
                    if(words.get(k).length() % 2 != 0) oddWords.add(words.get(k));
                    else oddWords.add(words.get(k));
                    k++;
                }
                if(USER_FIRST_TURN){
                    int randInt = rand.nextInt(evenWords.size());
                    midWord = evenWords.get(randInt);
                }else{
                    int randInt = rand.nextInt(oddWords.size());
                    midWord = oddWords.get(randInt);
                }

                evenWords.clear();
                oddWords.clear();
                System.gc();

                return midWord;
            }

            // Search left subarray.
            if (midWord.compareTo(prefix) > 0)
                return advancedBinarySearch(l, mid-1, prefix);

            //Search right subarray.
            return advancedBinarySearch(mid+1, r, prefix);
        }

        // We reach here when element is not present in array
        return null;
    }


}
