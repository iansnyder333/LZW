import java.io.*;
import java.util.*;

public class LZWmod{

    private static final int R = 256;        // number of input chars
    private static int W = 9;         // codeword width
    private static final int MAXIMUM_WIDTH = 16;
    private static int L = 512;       // number of codewords = 2^W
    private static final int MAXIMUM_CODEWORDS = 65536;
    private static boolean ResetFlag = false; //false is 0 and true is 1


    public static void compress() {
        //Initializes TST
        TSTmod<Integer> st = new TSTmod<Integer>();
        int i;
        for (i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF
        i+=1;


        //no reset
        if(ResetFlag==false){
        //mark compressed file with a 0 (false) for decompression
        BinaryStdOut.write(0,2);

        //initialize the current string
        StringBuilder current = new StringBuilder();
        //read and append the first char
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) {
          if(W<16 && i>=L){
            W++;
            L= 1<<W;
          }
            //read and append the next char to current
            codeword = st.get(current);
            c = BinaryStdIn.readChar();
            current.append(c);
            if(!st.contains(current)){
              BinaryStdOut.write(codeword, W);
              if (code < L){    // Add to symbol table if not full
                  st.put(current, code++);
                  i++;
                }
              //reset current
              current = new StringBuilder();
              current.append(c);
            }
        }
        //Write the codeword of whatever remains
        //in current
          codeword = st.get(current);
          BinaryStdOut.write(codeword, W);
        BinaryStdOut.write(R, W); //Write EOF
        BinaryStdOut.close();

      }
      else{
        //mark file with 1 (true) so expansion knows to reset.
        BinaryStdOut.write(1,2);


      //initialize the current string
      StringBuilder current = new StringBuilder();
      //read and append the first char
      char c = BinaryStdIn.readChar();
      current.append(c);
      Integer codeword = st.get(current);
      while (!BinaryStdIn.isEmpty()) {
        if(W<16 && i>=L){
          W++;
          L= 1<<W;
        }
        if(i>= MAXIMUM_CODEWORDS){
          st = new TSTmod<Integer>();
          for (i = 0; i < R; i++)
              st.put(new StringBuilder("" + (char) i), i);
          code = R+1;  // R is codeword for EOF
          i+=1;
          W=9;
          L=1<<9;
        }
          //read and append the next char to current
          codeword = st.get(current);
          c = BinaryStdIn.readChar();
          current.append(c);
          if(!st.contains(current)){
            BinaryStdOut.write(codeword, W);
            if (code < L){    // Add to symbol table if not full
                st.put(current, code++);
                i++;
              }
            //reset current
            current = new StringBuilder();
            current.append(c);
          }
      }
      //Write the codeword of whatever remains
      //in current
        codeword = st.get(current);
        BinaryStdOut.write(codeword, W);
      BinaryStdOut.write(R, W); //Write EOF
      BinaryStdOut.close();
      }
    }


    public static void expand() {
      //read binary flag from compressed file
      int flag = BinaryStdIn.readInt(2);
      //no reset
      if(flag==0){
        String[] st = new String[MAXIMUM_CODEWORDS];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
          if(W<16 && i>=L-1){
            W++;
            L=1<<W;
          }
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;

        }
        BinaryStdOut.close();
      }

      //Need to reset
      else if(flag==1){
        String[] st = new String[MAXIMUM_CODEWORDS];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
          if(i==MAXIMUM_CODEWORDS-1){
            st = new String[MAXIMUM_CODEWORDS];
            for (i = 0; i < R; i++)
                st[i] = "" + (char) i;
            st[i++] = "";
            W=9;
            L=1<<W;
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
          }
          if(i>=L-1 && W<16){
            W++;
            L=1<<W;
          }
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;

        }
        BinaryStdOut.close();
      }
      //if we got here something went wrong
      else throw new RuntimeException("File cannot be decompressed");


    }
    public static void main(String[] args) {
      /*
      ```shell
      $ java LZWmod - r < bogus.txt > bogus.lzw
      <file to compress/expand> location to compress into aka this file will be changed.
      ```
      */

      if(args[0].equals("-")){
         if(args.length>1){
           if(args[1].equals("r")) ResetFlag=true;
           //else if(args[1].equals("n")) ResetFlag=false;
           compress();
         }
         else compress();
       }
      else if (args[0].equals("+")) expand();
      else throw new RuntimeException("Illegal command line argument");


        /* if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
        */
    }


}
