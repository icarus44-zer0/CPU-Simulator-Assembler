import java.io.*;
import java.util.*;

/**
 *
 */
@SuppressWarnings("all")
public class Stasm {
    private static ArrayList<Instruction> instructionArrayList;
    private static ArrayList<Opcode> opcodeArrayList;
    private static ArrayList<String> mnemonicArrayList;
    private static ArrayList<String> operandArrayList;
    private static ArrayList<String> operandArrayList_Hex;
    private static ArrayList<String> outputList;
    private static HashMap<String, String> opcodeHashMap;
    private static HashMap<String, String> labelValueHashMap;
    private static String fileName;
    private static boolean isPrintToConsole;
    private static int counter;

    /**
     * @param args
     */
    public static void main(String[] args) {

        instructionArrayList = new ArrayList<Instruction>();
        opcodeArrayList = new ArrayList<Opcode>();
        mnemonicArrayList = new ArrayList<String>();
        operandArrayList = new ArrayList<String>();
        operandArrayList_Hex = new ArrayList<String>();
        outputList = new ArrayList<String>();
        opcodeHashMap = new HashMap<String, String>();
        labelValueHashMap = new HashMap<String, String>();
        fileName = "";
        isPrintToConsole = false;
        counter = 0;


        // debug method allows src code to compile without
        // coomand line arguments being supplied
        // output file will be sent to outide of the src dir
        if(true){
            System.err.println("""
                    *
                    * The debug flag for this program has been set to true.
                    *
                    """);
            args = new String[2];
            args[0] = "src/input.txt";
            args[1] = "-l";
        }

        // parse arguments from console
        parseArgs(args);

        // Creates HashMap of valid opCodes and their respective Machine Code
        initOpcodeHashMap(opcodeHashMap);

        // Scans input files and parses data into an Array list fistScanList, of custom
        // Object type CPU_Instruction
        initInstructionArrayList(instructionArrayList,opcodeHashMap);

        // Builds HashMap of Variables listed in input file and assigns value as their
        // address
        initLabelValueHashMap(instructionArrayList, labelValueHashMap);

        // Swaps all variable operands in fistScanList in with their value from
        replaceListLabelsWithLabelValues(instructionArrayList, labelValueHashMap);

        // Builds ArrayList of just Mnemonic and operand
        initOperandArrayList(instructionArrayList, mnemonicArrayList, operandArrayList);

        // Converts Integers to Hex
        operandArrayListToHex(operandArrayList, operandArrayList_Hex);

        // Creates LinkedHashMap with the Mnemonic and Hex Value
        initOpcodeArrayList(opcodeArrayList, mnemonicArrayList, operandArrayList_Hex);

        // Swaps LinkedHashMap Mnemonic opcodes with Machine Language opcode "i.e ADD ->
        // F000"
        replaceMnemonicsWithOpcodes(opcodeHashMap, opcodeArrayList, outputList);

        // parses the file for negative hex values and convertes them to there respective
        // values "1ffffffea" -> "1fea"
        fixNegativeInputs(outputList);

        // Converts all list values toUpper()
        allValuesToUpper(outputList);

        // Writes LinkedHashMap out to a objectfile.txt
        writeToObjectFile(outputList);

        // Prints LinkedHashMap to the screen. Determined by user args input -l
        if (isPrintToConsole){printMap(outputList);}
    }


    private static void parseArgs(String[] args) {
        try {
            fileName = args[0];
        }catch (ArrayIndexOutOfBoundsException e){
            String errorMessage = """
                    Your command line prompt> java Stasm.java
                    USAGE: java Stasm.java <source file> <object file> [-l]
                    -l : print listing to standard output
                    """;
            System.err.println(errorMessage);
            System.exit(42);
        }

        try {
            isPrintToConsole = (args[1].equals("-l") ? true : false);
        }catch (ArrayIndexOutOfBoundsException e){
            //nothing
        }
    }

    /**
     * @param instructionArrayList
     * @param opcodeHashMap
     * @param fileName
     */
    private static void initInstructionArrayList(ArrayList<Instruction> instructionArrayList, HashMap<String, String> opcodeHashMap) {
        try {
            File myObj = new File(fileName);
            Scanner sc = new Scanner(myObj);
            while (sc.hasNextLine()) {
                String label = null;
                String mnemonic = null;
                String operand = null;
                String comment = null;
                String line = sc.nextLine();

                if (line.contains(";")) {
                    int commentIndex = line.indexOf(";"); // Comment Handling
                    comment = line.substring(commentIndex);
                    line = line.replaceAll(comment, "");
                    comment = comment.replace(";", "");// Comment Handling
                }

                StringTokenizer defaultTokenizer = new StringTokenizer(line);

                while (defaultTokenizer.hasMoreTokens()) {
                    String next = defaultTokenizer.nextToken();

                    if (next.contains(":")) {
                        label = next.replace(":", "");
                    } else if (opcodeHashMap.containsKey(next)) {
                        mnemonic = next;
                        counter++;
                    } else
                        operand = next;
                }
                Instruction instruction = new Instruction(label, mnemonic, operand, comment);
                instructionArrayList.add(instruction);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("File " + fileName + " could not be located");
            e.printStackTrace();
        }
    }

    /**
     * @param instructionArrayList
     * @param labelValueHashMap
     */
    private static void initLabelValueHashMap(ArrayList<Instruction> instructionArrayList, HashMap<String, String> labelValueHashMap) {
        for (Instruction instruction : instructionArrayList) {
            if(instruction.getLabel() != null && instruction.getLabel().equalsIgnoreCase("END")){
                labelValueHashMap.put(instruction.getLabel(), Integer.toString(counter+1));
            }else if (instruction.getLabel() != null) {
                labelValueHashMap.put(instruction.getLabel(), instruction.getOperand());
            }
        }
    }

    /**
     * @param fistScanList
     * @param labelAddressMap
     */
    private static void replaceListLabelsWithLabelValues(ArrayList<Instruction> instructionArrayList,
                                                         HashMap<String, String> labelValueHashMap) {
        for (Instruction instruction : instructionArrayList) {
            String op = instruction.getOperand();
            if (isStringOnlyAlphabet(op)) {
                instruction.setOperand(labelValueHashMap.get(op));
            }
        }
    }

    /**
     * @param instructionArrayList
     * @param mnemonicArrayList
     * @param operandArrayList
     */
    private static void initOperandArrayList(ArrayList<Instruction> instructionArrayList, ArrayList<String> mnemonicArrayList,
                                             ArrayList<String> operandArrayList) {

        for (int i = 0; i < instructionArrayList.size(); i++) {
            Instruction instruction = instructionArrayList.get(i);

            String mnemonic = instruction.getMnemonic();
            String operand = instruction.getOperand();

            mnemonicArrayList.add(mnemonic);

            if (operand == null) {
                operand = "";
            }
            operandArrayList.add(operand);
        }
    }


    /**
     * @param operandArrayList
     * @param operandArrayList_Hex
     */
    private static void operandArrayListToHex(ArrayList<String> operandArrayList, ArrayList<String> operandArrayList_Hex) {
        for (String temp : operandArrayList) {
            operandArrayList_Hex.add(DECTOHEX(temp));
        }
    }

    /**
     * @param opcodeArrayList
     * @param mnemonicArrayList
     * @param operandArrayList_Hex
     */
    private static void initOpcodeArrayList(ArrayList<Opcode> opcodeArrayList, ArrayList<String> mnemonicArrayList,
                                            ArrayList<String> operandArrayList_Hex) {
        for (int i = 0; i < mnemonicArrayList.size(); i++)
            if (mnemonicArrayList.get(i) != null) {
                Opcode code = new Opcode(mnemonicArrayList.get(i),operandArrayList_Hex.get(i));
                opcodeArrayList.add(code);
            }
    }

    /**
     * @param opcodeHashMap
     * @param opcodeArrayList
     * @param outputList
     */
    private static void replaceMnemonicsWithOpcodes(HashMap<String, String> opcodeHashMap, ArrayList<Opcode> opcodeArrayList, ArrayList<String> outputList) {

        for (Opcode code : opcodeArrayList) {
            String key = code.getMnemonic();
            String val = code.getOperand();
            String val2 = opcodeHashMap.get(key);

            if (val.equals("0") || val2.length() == 4) {
                val = val2;
            } else if (val.length() == 1) {
                val = val2 + "0" + "0" + val;
            } else if (val.length() == 2) {
                val = val2 + "0" + val;
            } else {
                val = val2 + val;
            }
            outputList.add(val);
        }
    }

    /**
     * used to fix negative Hex Vals
     * @param outputList
     */
    private static void fixNegativeInputs(ArrayList<String> outputList) {
        for (String elem : outputList){
            if(elem.length() > 4) {
                String first = elem.substring(0, 1);
                String lastThree = elem.substring(elem.lastIndexOf('f'));
                if (first.length() + lastThree.length() == 4) {
                    outputList.set(outputList.indexOf(elem), first + lastThree);
                }
            }
        }
    }

    private static void allValuesToUpper(ArrayList<String> outputList) {
        for (String elem : outputList) {
            outputList.set(outputList.indexOf(elem), elem.toUpperCase());
        }
    }


    /**
     *
     * @param list
     */
    private static void writeToObjectFile(ArrayList<String> outputList) {

        try {
            FileWriter myWriter = new FileWriter("output.txt");
            myWriter.write("v2.0 raw");

            for (String item : outputList) {
                myWriter.write("\n" +item);
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param outputList
     */
    private static void printMap(ArrayList<String> outputList) {
        for (String item : outputList)
            System.err.println(item);
    }

    /**
     *
     * @param opcodeHashMap
     */
    private static void initOpcodeHashMap(HashMap<String, String> opcodeHashMap) {
        opcodeHashMap.put("NOP", "0000");
        opcodeHashMap.put("HALT", "0F00");
        opcodeHashMap.put("PUSHPC", "0100");
        opcodeHashMap.put("POPPC", "0200");
        opcodeHashMap.put("LD", "0300");
        opcodeHashMap.put("ST", "0400");
        opcodeHashMap.put("DUP", "0500");
        opcodeHashMap.put("DROP", "0600");
        opcodeHashMap.put("OVER", "0700");
        opcodeHashMap.put("DNEXT", "0800");
        opcodeHashMap.put("SWAP", "0900");
        opcodeHashMap.put("PUSHI", "1"); // + I
        opcodeHashMap.put("PUSH", "2"); // + A
        opcodeHashMap.put("POP", "3"); // + A
        opcodeHashMap.put("JMP", "4"); // + A
        opcodeHashMap.put("JZ", "5"); // + A
        opcodeHashMap.put("JNZ", "6"); // + A
        opcodeHashMap.put("IN", "D"); // + P
        opcodeHashMap.put("OUT", "E"); // + P
        opcodeHashMap.put("ADD", "F000");
        opcodeHashMap.put("SUB", "F001");
        opcodeHashMap.put("MUL", "F002");
        opcodeHashMap.put("DIV", "F003");
        opcodeHashMap.put("MOD", "F004");
        opcodeHashMap.put("SHL", "F005");
        opcodeHashMap.put("SHR", "F006");
        opcodeHashMap.put("BAND", "F007");
        opcodeHashMap.put("BOR", "F008");
        opcodeHashMap.put("BXOR", "F009");
        opcodeHashMap.put("AND", "F00A");
        opcodeHashMap.put("OR", "F00B");
        opcodeHashMap.put("EQ", "F00C");
        opcodeHashMap.put("NE", "F00D");
        opcodeHashMap.put("GE", "F00E");
        opcodeHashMap.put("LE", "F00F");
        opcodeHashMap.put("GT", "F010");
        opcodeHashMap.put("LT", "F011");
        opcodeHashMap.put("NEG", "F012");
        opcodeHashMap.put("BNOT", "F013");
        opcodeHashMap.put("NOT", "F014");
        //opcodeHashMap.put("DW", "");
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
        if (data.equals("")) {
            return "";
        }
        int num = Integer.parseInt(data);
        String ret = Integer.toHexString(num);
        return ret;
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isStringOnlyAlphabet(String str) {

        return ((str != null) && (str.matches("^[a-zA-Z]*$")));
    }
}

/**
 *
 */
class Instruction {
    private String label;
    private String mnemonic;
    private String operand;
    private String comment;

    public Instruction(String label, String mnemonic, String operand, String comment) {
        this.label = label;
        this.mnemonic = mnemonic;
        this.operand = operand;
        this.comment = comment;
    }

    public String getLabel() { return label;    }

    public void setLabel(String label) {this.label = label;}

    public String getMnemonic() {return mnemonic;}

    public void setMnemonic(String mnemonic) {this.mnemonic = mnemonic;}

    public String getOperand() {return operand;}

    public void setOperand(String operand) {this.operand = operand;}

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    @Override
    public String toString() {
        System.err.println(this.label + " " + this.mnemonic + " " + this.operand + " " + this.comment);
        return null;
    }
}


/**
 *
 */
class Opcode {
    private String mnemonic;
    private String operand;


    public Opcode(String mnemonic, String operand) {
        this.mnemonic = mnemonic;
        this.operand = operand;
    }

    public String getMnemonic() {return mnemonic;}

    public void setMnemonic(String mnemonic) {this.mnemonic = mnemonic;}

    public String getOperand() {return operand;}

    public void setOperand(String operand) {this.operand = operand;}

    @Override
    public String toString() {
        System.err.println(this.mnemonic + " " + this.operand);
        return null;
    }
}