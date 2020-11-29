import java.io.*;
import java.util.*;

/**
 *
 */
public class Stasm {
    private static ArrayList<String> inputFileOpcodes;
    private static ArrayList<String> inputFileValues;
    private static ArrayList<String> inputFileValues_Hex;
    private static HashMap<String, String> opcodeMap;
    private static HashMap<String, String> inputFileMap;


    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        inputFileOpcodes = new ArrayList<String>();
        inputFileValues = new ArrayList<String>();
        inputFileValues_Hex = new ArrayList<String>();
        opcodeMap = new HashMap<String, String>();
        inputFileMap = new HashMap<String, String>();

        makeOpcodeMap();
        readInputFile();
        convertInputValuesToHex(inputFileValues, inputFileValues_Hex);
        makeInputMap(inputFileOpcodes, inputFileValues_Hex);
        compareMapsAndReplaceValues(opcodeMap, inputFileMap);
        writeToObjectFile(inputFileMap);

        printMap(inputFileMap);

    }

    /**
     *
     * @param target
     * @param dest
     */
    private static void convertInputValuesToHex(ArrayList<String> target, ArrayList<String> dest) {
        for (String temp : target) {
            dest.add(DECTOHEX(temp));
        }
    }

    /**
     *
     */
    private static void readInputFile() {
        try {
            File myObj = new File("input.txt");
            Scanner sc = new Scanner(myObj);
            sc.nextLine();
            while (sc.hasNextLine()) {
                String opCode = sc.next();
                inputFileOpcodes.add(opCode);
                if (sc.hasNextInt()){
                    String value = sc.next();
                    inputFileValues.add(value);
                } else {
                    inputFileValues.add("00");
                }

            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param key
     * @param value
     */
    private static void makeInputMap(ArrayList<String> key, ArrayList<String> value) {
        for(int i = 0 ; i < key.size(); i++)
            inputFileMap.put(key.get(i),value.get(i));
    }


    /**
     *
     * @param CPU_OpCode_Map
     * @param inputFileMap_Hex
     */
    private static void compareMapsAndReplaceValues(HashMap<String, String> CPU_OpCode_Map, HashMap<String, String> inputFileMap_Hex) {
        for (Map.Entry<String, String> entry : inputFileMap_Hex.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            String val2 = CPU_OpCode_Map.get(key);

            if(val.equals("0") || val2.length() == 4) {
                val = val2;
            }else if(val.length() == 1){
                val = val2 + "0" + "0" + val;
            }else if(val.length() == 2){
                val = val2 + "0"  + val;
            }else {
                val = val2 + val;
            }
            inputFileMap_Hex.put(key,val);
        }
    }

    /**
     *
     * @param map
     */
    private static void writeToObjectFile(HashMap<String, String> map) {
        try {
            PrintStream fileStream = new PrintStream(new File("objectfile.txt"));
            fileStream.println("v2.0 raw");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                fileStream.println(entry.getValue());
            }
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static void makeOpcodeMap() {
        opcodeMap.put("NOP", "0000");
        opcodeMap.put("HALT", "0F00");
        opcodeMap.put("PUSHPC", "0100");
        opcodeMap.put("POPPC", "0200");
        opcodeMap.put("LD", "0300");
        opcodeMap.put("ST", "0400");
        opcodeMap.put("DUP", "0500");
        opcodeMap.put("DROP", "0600");
        opcodeMap.put("OVER", "0700");
        opcodeMap.put("DNEXT", "0800");
        opcodeMap.put("SWAP", "0900");
        opcodeMap.put("PUSHI", "1");     // + I
        opcodeMap.put("PUSHA", "2");     // + A
        opcodeMap.put("POPA", "3");      // + A
        opcodeMap.put("JMPA", "4");      // + A
        opcodeMap.put("JZA", "5");       // + A
        opcodeMap.put("JNZA", "6");      // + A
        opcodeMap.put("IN", "D");        // + P
        opcodeMap.put("OUT", "E");       // + P
        opcodeMap.put("ADD", "F000");
        opcodeMap.put("SUB", "F001");
        opcodeMap.put("MUL", "F002");
        opcodeMap.put("DIV", "F003");
        opcodeMap.put("MOD", "F004");
        opcodeMap.put("SHL", "F005");
        opcodeMap.put("SHR", "F006");
        opcodeMap.put("BAND", "F007");
        opcodeMap.put("BOR", "F008");
        opcodeMap.put("BXOR", "F009");
        opcodeMap.put("AND", "F00A");
        opcodeMap.put("OR", "F00B");
        opcodeMap.put("EQ", "F00C");
        opcodeMap.put("NE", "F00D");
        opcodeMap.put("GE", "F00E");
        opcodeMap.put("LE", "F00F");
        opcodeMap.put("GT", "F010");
        opcodeMap.put("LT", "F011");
        opcodeMap.put("NEG", "F012");
        opcodeMap.put("BNOT", "F013");
        opcodeMap.put("NOT", "F014");
    }


    /**
     *
     * @param data
     * @return data in int
     */
    private static int HEXTODEC(String data) {
        return Integer.parseInt(data, 16);
    }

    /**
     *
     * @param data
     * @return data In Hex
     */
    private static String DECTOHEX(int data) {
        return Integer.toHexString(data);
    }


    /**
     *
     * @param data
     * @return data In Hex
     */
    private static String DECTOHEX(String data) {
        int num = Integer.parseInt(data);
        String ret = Integer.toHexString(num);
        return ret;
    }

    /**
     *
     * @param map
     */
    private static void printMap(HashMap<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet())
            System.out.println(entry.getKey() +
                    " : " + entry.getValue());
    }
}

