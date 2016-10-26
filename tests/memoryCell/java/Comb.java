import java.io.*;
import java.util.*;

// A simple java file for generating instances of Memory for faulty
class Comb{

    /**
    * comb calculates the combination of k elements taken from input
    * @param k  the k
    * @param input  the set of elements
    * @param negated    indicates the element that must be negated, if negated < 0 all are negated
    */
    public static String  comb(int k, int[] input, int negated){
       
            //int[] input = {1, 2, 3};    // input array
            //int k = 2;                             // sequence length

            List<int[]> subsets = new ArrayList<>();

            int[] s = new int[k];                  // here we'll keep indices
                                       // pointing to elements in input array
        
            if (k <= input.length) {
                // first index sequence: 0, 1, 2, ...
                for (int i = 0; (s[i] = i) < k - 1; i++);
                subsets.add(getSubset(input, s));
                for(;;) {
                    int i;
                    // find position of item that can be incremented
                    for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--);
                    if (i < 0) {
                        break;
                    } else {
                        s[i]++;                    // increment this item
                        for (++i; i < k; i++) {    // fill up remaining items
                            s[i] = s[i - 1] + 1;
                        }
                        subsets.add(getSubset(input, s));
                    }
                }
            }
        
            // now we create the string
            String output = new String("");
            for (int i = 0 ; i < subsets.size(); i++){
                int[] current = subsets.get(i);
                if (i >0)
                    output = output + " || (";
                if (i == 0)
                    output = output + "(";
                for (int j = 0; j < current.length; j++){
                    if (j>0)
                        output = output + " && ";
                    if (current[j] == negated && 0 <= negated)
                        output = output+"!c"+current[j];
                    if (current[j] != negated && 0 <= negated)
                        output = output+"c"+current[j];
                    if (0 > negated)
                         output = output+"!c"+current[j];
                }
                output = output + ")";
            }
            return output;
       
    }
    
    // generate actual subset by index sequence
    private static int[] getSubset(int[] input, int[] subset) {
        int[] result = new int[subset.length];
        for (int i = 0; i < subset.length; i++)
            result[i] = input[subset[i]];
        return result;
    }
}
