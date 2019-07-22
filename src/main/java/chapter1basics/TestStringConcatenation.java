package chapter1basics;

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
}
