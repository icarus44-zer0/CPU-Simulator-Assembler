import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The STUMPED program implements an SIMULATED CPU that
 * uses micro code and assemlby to simulate CPU opperations
 *
 * @author  Josh Poe
 * @version 1.0
 * @since   11/20/2020
 */

@SuppressWarnings("all")
public class Stumped {

    private static Stack<Integer> stack;
    private static ArrayList<String> arr;
    private static int[] mem;
    private static int inputCMD = 0;
    private static boolean isInput = true;
    private static int counter = 0;
    private static boolean isStackTrace = false;
    private static boolean isDebug = true;
    private static int sucessCounter = -4095;

    /**
     *
     * @param args
     */
public void runCPU(String args[]){
        mem = new int[4095];
        arr = new ArrayList<String>();
        stack = new Stack<Integer>();

        try {
            File myObj = new File(args[0]);
            Scanner myReader = new Scanner(myObj);
            myReader.nextLine();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                arr.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inputCMD = Integer.parseInt(args[1]);
            isInput = true;
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        while (!(arr.get(counter).equals("0F00"))) {
            CPU(arr.get(counter));
        }

    }

    /**
     *
     * @param args
     */
    private static void CPU(String args) {
        counter++;
        try {
            TimeUnit.MILLISECONDS.sleep(0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (isDebug){
            debug(args);
        }
        String opcode = args.substring(0, 1);
        String dataHex = args.substring(1);
        int data = HEXTODEC(dataHex);
        switch (opcode) {
            case "0":
                CONTROL(data);
                break;
            case "1":
                PUSHI(dataHex);
                if(isStackTrace){ stackTrace(data,"PUSHI");}
                break;
            case "2":
                PUSHA(data);
                if(isStackTrace){ stackTrace(data,"PUSHA");}
                break;
            case "3":
                POPA(data);
                if(isStackTrace){ stackTrace(data,"POPA");}
                break;
            case "4":
                JMPA(dataHex);
                if(isStackTrace){ stackTrace(data,"JMPA");}
                break;
            case "5":
                JZA(dataHex);
                if(isStackTrace){ stackTrace(data,"JZA");}
                break;
            case "6":
                JNZA(dataHex);
                if(isStackTrace){ stackTrace(data,"JNZA");}
                break;
            case "D":
                IN(data);
                if(isStackTrace){ stackTrace(data,"IN");}
                break;
            case "E":
                OUT(data);
                if(isStackTrace){ stackTrace(data,"OUT");}
                break;
            case "F":
                ALU(data);
                if(isStackTrace){ stackTrace(data,"ALU") ;}
                break;
            default:
                break;
        }

    }

    /**
     *
     * @param data
     */
    private static void CONTROL(int data) {
        switch (data) {
            case 0: // 0x00)
                NOP(data);
                if(isStackTrace){ stackTrace(data,"NOP");}
                break;
            case 256: // 0x100
                PUSHPC(data);
                if(isStackTrace){ stackTrace(data,"PUSHPC");}
                break;
            case 512: // 0x200
                POPPC(data);
                if(isStackTrace){ stackTrace(data,"POPPC");}
                break;
            case 768: // 0x300
                LD(data);
                if(isStackTrace){ stackTrace(data,"LD");}
                break;
            case 1024: // 0x400
                ST();
                if(isStackTrace){ stackTrace(data,"ST");}
                break;
            case 1280: // 0x500
                DUP(data);
                if(isStackTrace){ stackTrace(data,"DUP");}
                break;
            case 1536: // 0x600
                DROP(data);
                if(isStackTrace){ stackTrace(data,"DROP");}
                break;
            case 1792: // 0x700
                OVER(data);
                if(isStackTrace){ stackTrace(data,"OVER");}
                break;
            case 2048: // 0x800
                DNEXT(data);
                if(isStackTrace){ stackTrace(data,"DNEXT");}
                break;
            case 2304: // 0x900
                SWAP(data);
                if(isStackTrace){ stackTrace(data,"SWAP");}
                break;
            case 3840: // 0xF00
                HALT(data);
                if(isStackTrace){ stackTrace(data,"HALT");}
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param data
     */
    private static void ALU(int data) {

        switch (data) {
            case 0:
                ADD(data);
                if(isStackTrace){ stackTrace(data,"ADD");}
                break;
            case 1:
                SUB(data);
                if(isStackTrace){ stackTrace(data,"SUB");}
                break;
            case 2:
                MUL(data);
                if(isStackTrace){ stackTrace(data,"MUL");}
                break;
            case 3:
                DIV(data);
                if(isStackTrace){ stackTrace(data,"DIV");}
                break;
            case 4:
                MOD(data);
                if(isStackTrace){ stackTrace(data,"MOD");}
                break;
            case 5:
                SHL(data);
                if(isStackTrace){ stackTrace(data,"SHL");}
                break;
            case 6:
                SHR(data);
                if(isStackTrace){ stackTrace(data,"SHR");}
                break;
            case 7:
                BAND(data);
                if(isStackTrace){ stackTrace(data,"BAND");}
                break;
            case 8:
                BOR(data);
                if(isStackTrace){ stackTrace(data,"BOR");}
                break;
            case 9:
                BXOR(data);
                if(isStackTrace){ stackTrace(data,"BXOR");}
                break;
            case 10:
                AND(data);
                if(isStackTrace){ stackTrace(data,"AND");}
                break;
            case 11:
                OR(data);
                if(isStackTrace){ stackTrace(data,"OR");}
                break;
            case 12:
                EQ(data);
                if(isStackTrace){ stackTrace(data,"EQ");}
                break;
            case 13:
                NE(data);
                if(isStackTrace){ stackTrace(data,"NE");}
                break;
            case 14:
                GE(data);
                if(isStackTrace){ stackTrace(data,"GE");}
                break;
            case 15:
                LE(data);
                if(isStackTrace){ stackTrace(data,"LE");}
                break;
            case 16:
                GT(data);
                if(isStackTrace){ stackTrace(data,"GT");}
                break;
            case 17:
                LT(data);
                if(isStackTrace){ stackTrace(data,"LT");}
                break;
            case 18:
                NEG(data);
                if(isStackTrace){ stackTrace(data,"NEG");}
                break;
            case 19:
                BNOT(data);
                if(isStackTrace){ stackTrace(data,"BNOT");}
                break;
            case 20:
                NOT(data);
                if(isStackTrace){ stackTrace(data,"NOT");}
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param data
     */
    private static void NOP(int data) {

    }

    /**
     *
     * @param data
     */
    private static void HALT(int data) {
        System.exit(42);
    }

    /**
     *
     * @param data
     */
    private static void PUSHPC(int data) {
        stack.push(counter);
    }

    /**
     *
     * @param data
     */
    private static void POPPC(int data) {
        counter = stack.pop();
    }

    /**
     *
     * @param data
     */
    private static void LD(int data) {
        int top = stack.pop();
        stack.push(mem[top]);
    }

    /**
     *
     * @param data
     */
    private static void ST() {
        int top = stack.pop();
        int next = stack.pop();
        mem[next] = top;
    }

    /**
     *
     * @param data
     */
    private static void DUP(int data) {
        int top = stack.pop();

        stack.push(top);
        stack.push(top);
    }

    /**
     *
     * @param data
     */
    private static void DROP(int data) {
        stack.pop();
    }

    /**
     *
     * @param data
     */
    private static void OVER(int data) {
        int top = stack.pop();
        int next = stack.pop();

        stack.push(next);
        stack.push(top);
        stack.push(next);
    }

    /**
     *
     * @param data
     */
    private static void DNEXT(int data) {
        int top = stack.pop();
        stack.pop();

        stack.push(top);
    }

    /**
     *
     * @param data
     */
    private static void SWAP(int data) {
        int top = stack.pop();
        int next = stack.pop();

        stack.push(top);
        stack.push(next);
    }

    /**
     *
     * @param dataHex
     */
    private static void PUSHI(String dataHex) {
        String builder = "";
        for (int i = 0; i < dataHex.length(); i++) {
            switch (dataHex.substring(i, i + 1)) {
                case "0":
                    builder += "0000";
                    break;
                case "1":
                    builder += "0001";
                    break;
                case "2":
                    builder += "0010";
                    break;
                case "3":
                    builder += "0011";
                    break;
                case "4":
                    builder += "0100";
                    break;
                case "5":
                    builder += "0101";
                    break;
                case "6":
                    builder += "0110";
                    break;
                case "7":
                    builder += "0111";
                    break;
                case "8":
                    builder += "1000";
                    break;
                case "9":
                    builder += "1001";
                    break;
                case "A":
                    builder += "1010";
                    break;
                case "B":
                    builder += "1011";
                    break;
                case "C":
                    builder += "1100";
                    break;
                case "D":
                    builder += "1101";
                    break;
                case "E":
                    builder += "1110";
                    break;
                case "F":
                    builder += "1111";
                    break;
                default:
                    break;
            }
        }
        String sign = builder.substring(0, 1);
        String signExt = "";

        if (sign.equals("1")) {
            for (int i = builder.length(); i < 16; i++) {
                signExt += "1";
            }
        } else if (sign.equals("0")) {
            for (int i = builder.length(); i < 16; i++) {
                signExt += "0";
            }
        }
        builder = signExt + builder;

        String b2HEX = "";

        for (int i = 0; i < builder.length(); i += 4) {
            switch (builder.substring(i, i + 4)) {
                case "0000":
                    b2HEX += "0";
                    break;
                case "0001":
                    b2HEX += "1";
                    break;
                case "0010":
                    b2HEX += "2";
                    break;
                case "0011":
                    b2HEX += "3";
                    break;
                case "0100":
                    b2HEX += "4";
                    break;
                case "0101":
                    b2HEX += "5";
                    break;
                case "0110":
                    b2HEX += "6";
                    break;
                case "0111":
                    b2HEX += "7";
                    break;
                case "1000":
                    b2HEX += "8";
                    break;
                case "1001":
                    b2HEX += "9";
                    break;
                case "1010":
                    b2HEX += "A";
                    break;
                case "1011":
                    b2HEX += "B";
                    break;
                case "1100":
                    b2HEX += "C";
                    break;
                case "1101":
                    b2HEX += "D";
                    break;
                case "1110":
                    b2HEX += "E";
                    break;
                case "1111":
                    b2HEX += "F";
                    break;
                default:
                    break;
            }
        }

        short st = (short) Integer.parseInt(b2HEX, 16);
        stack.push((int) st);

    }

    /**
     *
     * @param data
     */
    private static void PUSHA(int data) {
        int top = mem[data];
        stack.push(top);
    }

    /**
     *
     * @param data
     */
    private static void POPA(int data) {
        mem[data] = stack.pop();
    }

    /**
     *
     * @param dataHex
     */
    private static void JMPA(String dataHex) {
        counter = HEXTODEC(dataHex);
        CPU(arr.get(counter));
    }

    /**
     *
     * @param dataHex
     */
    private static void JZA(String dataHex) {
        if (!stack.empty()) {
            int test = stack.pop();
            if (test == 0) {
                counter = HEXTODEC(dataHex);
                CPU(arr.get(counter));
            }
        }
    }

    /**
     *
     * @param dataHex
     */
    private static void JNZA(String dataHex) {
        if (!stack.empty()) {
            int test = stack.pop();
            if (test != 0) {
                counter = HEXTODEC(dataHex);
                CPU(arr.get(counter));
            }
        }
    }

    /**
     *
     * @param data
     */
    private static void IN(int data) {
        if (isInput) {
            stack.push(inputCMD);
        }
    }

    /**
     *
     * @param data
     */
    private static void OUT(int data) {
        //System.out.println("\nOUTPUT:" + stack.pop());
        System.out.println(stack.pop());
    }

    /**
     *
     * @param data
     */
    private static void ADD(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next + top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void SUB(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next - top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void MUL(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next * top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void DIV(int data) {

        int top = stack.pop();
        int next = stack.pop();

        if (top == 0) {
            CPU("0F00");
        } else {
            int output = next / top;
            stack.push(output);
        }
    }

    /**
     *
     * @param data
     */
    private static void MOD(int data) {
        int top = stack.pop();
        int next = stack.pop();
        if (top == 0) {
            CPU("0F00");
        } else {
            int output = next % top;
            stack.push(output);
        }
    }

    /**
     *
     * @param data
     */
    private static void SHL(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next << top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void SHR(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next >> top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void BAND(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next & top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void BOR(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next | top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void BXOR(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = next ^ top;
        stack.push(output);
    }

    /**
     *
     * @param data
     */
    private static void AND(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = 0;
        boolean a = false;
        boolean b = false;

        if (next != 0) {
            a = true;
        }
        if (top != 0) {
            b = true;
        }

        if (a && b) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void OR(int data) {
        int top = stack.pop();
        int next = stack.pop();
        int output = 0;
        boolean a = false;
        boolean b = false;

        if (next != 0) {
            a = true;
        }
        if (top != 0) {
            b = true;
        }

        if (a || b) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void EQ(int data) {
        int top = stack.pop();
        int next = stack.pop();

        if (next == top) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void NE(int data) {
        int top = stack.pop();
        int next = stack.pop();

        if (next != top) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void GE(int data) {
        int top = stack.pop();
        int next = stack.pop();

        if (next >= top) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void LE(int data) {
        int top = stack.pop();
        int next = stack.pop();

        if (next <= top) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void GT(int data) {
        int top = stack.pop();
        int next = stack.pop();

        if (next > top) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void LT(int data) {
        int top = stack.pop();
        int next = stack.pop();

        if (next < top) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    /**
     *
     * @param data
     */
    private static void NEG(int data) {
        int top = stack.pop();
        top = -top;
        stack.add(top);
    }

    /**
     *
     * @param data
     */
    private static void BNOT(int data) {
        int top = stack.pop();

        top = ~top;
        stack.push(top);
    }

    /**
     *
     * @param data
     */
    private static void NOT(int data) {
        int top = stack.pop();
        stack.push(top);
    }

    /*
     * The Following Methods are Helper Methods
     *
     *
     */

    /**
     *
     * @param b
     */
    private static void isStacktrace(boolean b) {
        isStackTrace = b;
    }

    /**
     *
     * @param data
     * @param args
     */
    private static void stackTrace(int data, String args) {
        if(isStackTrace) {
            System.out.print("ARGS: " + args + "Input: " + data + "Stack: " + stack.toString());
        }
    }

    /**
     *
     * @param args
     * @throws InterruptedException
     */
    private static void debug(String args) {
       // debug1(args);
       // debug2(args);
        debug3(args);

        return;
    }

    private static void debug3(String args) {
        sucessCounter++;
        if (sucessCounter > 0){
            System.out.println("OpCODE: " + args + " Counter: " + counter +" Sucesses:" + sucessCounter);
        }
    }

    private static void debug2(String args) {
        if (sucessCounter > 0) {
            if (!stack.empty()) {
                System.out.print(" Stack: " + stack.toString());
                System.out.print("\n arg: " + args);
                System.out.print(" " + "Counter: " + counter);
            }
        }
    }


    private static void debug1(String args) {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!stack.empty()) {
            System.out.print(" Stack: " + stack.toString());
            System.out.print("\n arg: " + args);
            System.out.print(" " + "Counter: " + counter);
        }
        return;
    }



    /**
     *
     * @param data
     * @return
     */
    private static int HEXTODEC(String data) {
        int ret = Integer.parseInt(data, 16);
        return ret;
    }

    /**
     *
     * @param data
     * @return
     */
    private static String DECTOHEX(int data) {
        String ret = Integer.toHexString(data);
        return ret;
    }

}