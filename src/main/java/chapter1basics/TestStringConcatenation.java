package chapter1basics;

import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestStringConcatenation {


    public static void main(String[] args) {

        String text = "I am trying to iterate through a string in order to remove the duplicates characters.\n" +
                "\n" +
                "For example the String aabbccdef should become abcdef and the String abcdabcd should become abcd\n" +
                "\n" +
                "Here is what I have so far:";

        long startTime = System.nanoTime();

//        removeDuplicates(text);
        concatenate();

        long time = System.nanoTime() - startTime;
        System.out.println("First: " + time);

        startTime = System.nanoTime();
//        removeDuplicatesWithBuilder(text);
        concatenateStringBuilder();
        time = System.nanoTime() - startTime;

        System.out.println("Second: " + time);


        Observable<Integer> intervalArguments =
                Observable.just(2, 3, 10, 7);

        intervalArguments.flatMap(i ->

                Observable.interval(i, TimeUnit.SECONDS)
                        .map(timer -> i + "s interval: " + (timer + 1) * i + " seconds  elapsed")
        ).subscribe(System.out::println);


        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Arrays.asList();

    }

    public static String concatenate() {
        String text = "I am trying to iterate through a string in order to remove the duplicates characters. " +
                "For example the String aabbccdef should become abcdef and the String abcdabcd should become abcd\n" +
                "\n" +
                "Here is what I have so far:";

        return text + text + text + text + text;

    }


    public static String concatenateStringBuilder() {
        String text = "I am trying to iterate through a string in order to remove the duplicates characters. " +
                "For example the String aabbccdef should become abcdef and the String abcdabcd should become abcd\n" +
                "\n" +
                "Here is what I have so far:";

        StringBuilder sb = new StringBuilder();
        sb.append(text);
        sb.append(text);
        sb.append(text);
        sb.append(text);
        sb.append(text);

        return sb.toString();

    }

    public static String removeDuplicates(String input) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (!result.toString().contains(String.valueOf(input.charAt(i)))) {
                result.append(String.valueOf(input.charAt(i)));
            }
        }
        return result.toString();
    }

    public static String removeDuplicatesWithBuilder(String input) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (!result.toString().contains(String.valueOf(input.charAt(i)))) {
                result.append(String.valueOf(input.charAt(i)));
            }
        }

        return result.toString();
    }

    static List<Integer> oddNumbers(int l, int r) {

        List<Integer> oddNumbers = new ArrayList<>();

        for (int i = l; i <= r; i++) {
            if (i % 2 != 0) {
                oddNumbers.add(i);
            }
        }

        return oddNumbers;

    }

    // Complete the findNumber function below.
    static String findNumber(List<Integer> arr, int k) {

        for (Integer i : arr) {
            if (i == k) return "YES";
        }

        return "NO";
    }
}
