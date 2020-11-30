import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.*;

/**
 *
 */
public class Stasm {
    private static ArrayList<CPU_Instruction> fistScanList;
    private static ArrayList<String> inputFileOpcodes;
    private static ArrayList<String> inputFileValues;
    private static ArrayList<String> inputFileValues_Hex;
    private static HashMap<String, String> opcodeMap;
    private static LinkedHashMap<String, String> inputFileMap;
    private static HashMap<String, String> labelAddressMap;


    /**
     * @param args
     */
    public static void main(String[] args) {
        fistScanList = new ArrayList<CPU_Instruction>();
        inputFileOpcodes = new ArrayList<String>();
        inputFileValues = new ArrayList<String>();
        inputFileValues_Hex = new ArrayList<String>();

        opcodeMap = new HashMap<String, String>();
        inputFileMap = new LinkedHashMap<String, String>();
        labelAddressMap = new HashMap<String, String>();


        //Creates HashMap of valid opCodes and their respective Machine Code
        makeOpcodeMap(opcodeMap);

        // Scans input files and parses data into an Array list fistScanList, of custom Object type CPU_Instruction
        firstScan(fistScanList);

        //Builds HashMap of Variables listed in input file and assigns value as their address
        addAddressValuesToMap(fistScanList,labelAddressMap);

        // Swaps all variable operands in fistScanList in with their value from labelAddressMap
        SwapLabelsWithAddressValues(fistScanList,labelAddressMap);

        //Builds ArrayList of just Mnemonic and operand
        secondScan(fistScanList, inputFileOpcodes, inputFileValues);

        // Converts Integers to Hex
        convertInputValuesToHex(inputFileValues, inputFileValues_Hex);

        //Creates LinkedHashMap with the Mnemonic and Hex Value
        makeInputMap(inputFileOpcodes, inputFileValues_Hex);

        //Swaps LinkedHashMap Mnemonic opcodes with Machine Language opcode "i.e ADD -> F000"
        compareMapsAndReplaceValues(opcodeMap, inputFileMap);

        //Writes LinkedHashMap out to a objectfile.txt
        writeToObjectFile(inputFileMap);

        //Prints LinkedHashMap to the screen. Determined by user args input -l
        printMap(inputFileMap);
    }

    /**
     *
     * @param fistScanList
     * @param labelAddressMap
     */
    private static void SwapLabelsWithAddressValues(ArrayList<CPU_Instruction> fistScanList, HashMap<String, String> labelAddressMap) {
        for(CPU_Instruction instruction : fistScanList){
            String op = instruction.getOperand();
            if(isStringOnlyAlphabet(op)){
                instruction.setOperand(labelAddressMap.get(op));
            }
        }
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isStringOnlyAlphabet(String str)
    {
        return ((str != null)
                && (str.matches("^[a-zA-Z]*$")));
    }

    /**
     *
     * @param fistScanList
     * @param labelAddressMap
     */
    private static void addAddressValuesToMap(ArrayList<CPU_Instruction> fistScanList, HashMap<String, String> labelAddressMap) {
        for(CPU_Instruction instruction : fistScanList){
            if(instruction.getLabel() != null){
                labelAddressMap.put(instruction.getLabel(),String.valueOf(fistScanList.indexOf(instruction)));
            }
        }
    }

    /**
     * @param target
     * @param dest
     */
    private static void convertInputValuesToHex(ArrayList<String> target, ArrayList<String> dest) {
        for (String temp : target) {
            dest.add(DECTOHEX(temp));
        }
    }

    /**
     * @param inputArrayList
     */
    private static void firstScan(ArrayList<CPU_Instruction> inputArrayList) {
        try {
            File myObj = new File("in.txt");
            Scanner sc = new Scanner(myObj);
            while (sc.hasNextLine()) {
                String label = null;
                String mnemonic = null;
                String operand = null;
                String comment = null;
                String line = sc.nextLine();

                if (line.contains(";")){
                    int commentIndex = line.indexOf(";");                               //Comment Handling
                    comment = line.substring(commentIndex);
                    line = line.replaceAll(comment,"");
                    comment = comment.replace(";","");//Comment Handling
                }

                StringTokenizer defaultTokenizer = new StringTokenizer(line);

                while (defaultTokenizer.hasMoreTokens()) {
                    String next = defaultTokenizer.nextToken();

                    if (next.contains(":")) {
                        label = next.replace(":","");
                    }else if (opcodeMap.containsKey(next)) {
                        mnemonic = next;
                    } else
                        operand = next;
                }
                CPU_Instruction instruction = new CPU_Instruction(label,mnemonic,operand,comment);
                inputArrayList.add(instruction);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



        /**
         *
         * @param fistScanList
         * @param inputFileOpcodes
         * @param inputArrayList
         */
        private static void secondScan(ArrayList<CPU_Instruction> fistScanList, ArrayList<String> inputFileOpcodes, ArrayList<String> inputArrayList) {
            for (int i = 0; i < fistScanList.size(); i++) {
                CPU_Instruction instruction = fistScanList.get(i);
                String mnemonic = instruction.getMnemonic();
                String operand = instruction.getOperand();

                inputFileOpcodes.add(mnemonic);

                if (operand == null) {
                    operand = "";
                }
                inputArrayList.add(operand);
            }
        }


        /**
         *
         * @param key
         * @param value
         */
        private static void makeInputMap (ArrayList < String > key, ArrayList < String > value){
            for (int i = 0; i < key.size(); i++)
                if(!(key.get(i).equals("DW"))) {
                    inputFileMap.put(key.get(i), value.get(i));
                }
        }


        /**
         *
         * @param CPU_OpCode_Map
         * @param inputFileMap_Hex
         */
        private static void compareMapsAndReplaceValues
        (HashMap < String, String > CPU_OpCode_Map, LinkedHashMap < String, String > inputFileMap_Hex){
            for (Map.Entry<String, String> entry : inputFileMap_Hex.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                String val2 = CPU_OpCode_Map.get(key);

                //TODO Split up for 1000
                if (val.equals("0") || val2.length() == 4) {
                    val = val2;
                } else if (val.length() == 1) {
                    val = val2 + "0" + "0" + val;
                } else if (val.length() == 2) {
                    val = val2 + "0" + val;
                } else {
                    val = val2 + val;
                }
                inputFileMap_Hex.put(key, val);
            }
        }

        /**
         *
         * @param map
         */
        private static void writeToObjectFile (LinkedHashMap < String, String > map){
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
         * @param opcodeMap
         */
        private static void makeOpcodeMap (HashMap < String, String > opcodeMap){
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
            opcodeMap.put("DW", "");
        }


        /**
         *
         * @param data
         * @return data in int
         */
        private static int HEXTODEC (String data){
            return Integer.parseInt(data, 16);
        }

        /**
         *
         * @param data
         * @return data In Hex
         */
        private static String DECTOHEX ( int data){
            return Integer.toHexString(data);
        }


        /**
         *
         * @param data
         * @return data In Hex
         */
        private static @NotNull
        String DECTOHEX (String data){
            if (data.equals("")){
                return "";
            }
            int num = Integer.parseInt(data);
            String ret = Integer.toHexString(num);
            return ret;
        }

        /**
         *
         * @param map
         */
        private static void printMap (LinkedHashMap < String, String > map){
            for (Map.Entry<String, String> entry : map.entrySet())
                System.out.println(entry.getKey() +
                        " : " + entry.getValue());
        }
    }


/**
 *
 */
class CPU_Instruction {
        private String label;
        private String mnemonic;
        private String operand;
        private String comment;

        public CPU_Instruction(String label, String mnemonic, String operand, String comment) {
            this.label = label;
            this.mnemonic = mnemonic;
            this.operand = operand;
            this.comment = comment;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public void setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        }

        public String getOperand() {
            return operand;
        }

        public void setOperand(String operand) {
            this.operand = operand;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString() {
            System.out.println(this.label + " " + this.mnemonic + " " + this.operand + " " + this.comment);
        return null;
        }
    }


