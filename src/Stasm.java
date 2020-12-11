import java.io.*;
import java.util.*;

/**
 *
 */
@SuppressWarnings("all")
public class Stasm {
    private static ArrayList<MachineState> machineStatesArrayList;
    private static HashMap<String, String> opcodeHashMap;
    private static LinkedHashMap<String, String> labelValueHashMap;
    private static String inputFileName;
    private static String outputFileName;
    private static boolean isPrintToConsole;
    private static int counter;

    /**
     * @param args
     */
    public static void main(String[] args) {
        machineStatesArrayList = new ArrayList<MachineState>();

        opcodeHashMap = new HashMap<String, String>();
        labelValueHashMap = new LinkedHashMap<String, String>();

        inputFileName = "";
        outputFileName = "";
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
            args = new String[3];
            args[0] = "src/input.txt";
            args[1] = "src/output.txt";
            args[2] = "-l";
        }

        // parse arguments from console
        parseArgs(args);

        // Creates HashMap of valid opCodes and their respective Machine Code
        initOpcodeHashMap();

        // Scans input files and parses data into an Array list fistScanList, of custom
        initMachineStateArrayList();

        //if (isPrintToConsole){printMap();}
        // Builds HashMap of Variables listed in input file and assigns value as their
        parseLabelsWithNoOpperands();

        // Swaps all variable operands in fistScanList in with their value from
        replaceListLabelsWithLabelValues();

        // Swaps LinkedHashMap Mnemonic opcodes with Machine Language opcode "i.e ADD ->
        // F000"
        replaceMnemonicsWithOpcodes();

        //adds varable hex inputs (suffix) to Opcode character (prefix )
        addHexValuesToOpcodes();

        // parses the file for negative hex values and convertes them to there respective
        // values "1ffffffea" -> "1fea"
        formatNegativeHexVals();

        // Converts all list values toUpper()
        //
        allValuesToUpper();

        // Writes LinkedHashMap out to a objectfile.txt
        //
        writeToObjectFile();

        // Prints LinkedHashMap to the screen. Determined by user args input -l
        //
        if (isPrintToConsole){printMap();}
    }


    private static void parseArgs(String[] args) {
        try {
            inputFileName = args[0];
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
            outputFileName = args[1];
        }catch (ArrayIndexOutOfBoundsException e){
            String errorMessage = """
                    Your command line prompt> java Stasm.java <source file>
                    USAGE: java Stasm.java <source file> <object file> [-l]
                    -l : print listing to standard output
                    """;
            System.err.println(errorMessage);
            System.exit(42);
        }

        try {
            isPrintToConsole = (args[2].equals("-l") ? true : false);
        }catch (ArrayIndexOutOfBoundsException e){
            //nothing
        }
    }

    private static void initMachineStateArrayList() {
        try {
            File myObj = new File(inputFileName);
            Scanner sc = new Scanner(myObj);
            while (sc.hasNextLine()){

                String line = null;
                String label = null;
                String mnemonic = null;
                String operand = null;
                String comment = null;

                line = sc.nextLine();
                if(line.isEmpty()){
                    continue;
                }
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
                        } else {
                            operand = next;
                        }
                    }
                    //loks for EOF signal "END"
                    if(!(label == null) && (label.equalsIgnoreCase("END"))) {
                        labelValueHashMap.put(label,Integer.toString(counter));
                    }
                    //ignores labels with no Opperand
                     else if( !(label == null) && (operand == null) && !(label.equalsIgnoreCase("END")) ){
                        labelValueHashMap.put(label,null);
                    }
                    //Adds labels with Opperand to labelValueHashMap and machineStatesArrayList
                    else if( !(label == null) && !(operand == null) && !(label.equalsIgnoreCase("END")) ){
                        labelValueHashMap.put(label,operand);
                        String address = String.format("%03d", counter);
                        MachineState state = new MachineState(address, null, mnemonic, label, operand, DECTOHEX(operand));
                        machineStatesArrayList.add(state);
                        counter++;
                    }
                    //ignores lines with only comments
                    else if( !(label == null) || !(mnemonic == null) || !(operand == null) ) {
                        String address = String.format("%03d", counter);
                        MachineState state = new MachineState(address, null, mnemonic, label, operand, DECTOHEX(operand));
                        machineStatesArrayList.add(state);
                        counter++;
                    }
                }

            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("File " + inputFileName + " could not be located");
            e.printStackTrace();
        }
    }

    private static void parseLabelsWithNoOpperands() {
        ArrayList<String> keysWithNoValues = new ArrayList<String>();

        for (Map.Entry<String, String> entry : labelValueHashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(value == null){
                keysWithNoValues.add(key);
            }
            if(value != null && keysWithNoValues.size() != 0){
                int i = 0;
                while (!(keysWithNoValues.isEmpty())){
                    String key2 = keysWithNoValues.get(0);
                    labelValueHashMap.put(key2,value);
                    keysWithNoValues.remove(0);
                }
            }
        }
    }

    private static void replaceListLabelsWithLabelValues() {
        for (MachineState state : machineStatesArrayList) {
            String operand = state.getOperand();
            if (isStringOnlyAlphabet(operand)) {
                state.setHex(DECTOHEX(labelValueHashMap.get(operand)));
            }
        }
    }

    private static void replaceMnemonicsWithOpcodes() {
        for (MachineState state : machineStatesArrayList) {
            if (state.getMnemonic() != null) {
                String opCode = opcodeHashMap.get(state.getMnemonic());
                state.setOpCode(opCode);
            }
        }
    }

    private static void addHexValuesToOpcodes(){
        for (MachineState state : machineStatesArrayList) {
            if (state.getOpCode() != null && state.getOpCode().length() == 1) {
                String hex = state.getHex();
                String opCode = state.getOpCode();
                hex = String.format("%1$" + 3 + "s", hex).replace(' ', '0');
                state.setOpCode(opCode+hex);
            }
        }
    }

    private static void formatNegativeHexVals() {
        for (MachineState state : machineStatesArrayList){
            if(state.getOpCode() != null && state.getOpCode().length() > 4) {
                String opCode = state.getOpCode();
                String first = opCode.substring(0, 1);
                String lastThree = opCode.substring(opCode.length() - 3);
                if (first.length() + lastThree.length() == 4) {
                    state.setOpCode(first + lastThree);
                }
            }
        }
    }

    private static void allValuesToUpper() {
        for (MachineState state : machineStatesArrayList) {
            if(state.getOpCode() != null) {
                String opCode = state.getOpCode();
                state.setOpCode(opCode.toUpperCase());
            }
        }
    }


    /**
     *
     * @param list
     */
    private static void writeToObjectFile() {
        try {
            FileWriter myWriter = new FileWriter(outputFileName);
            myWriter.write("v2.0 raw");

            for (MachineState state : machineStatesArrayList) {
                if(state.getOpCode() != null)
                    myWriter.write("\n" +state.getOpCode());
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printMap() {
        StringBuilder builder = new StringBuilder();
        String header1 = "*** LABEL LIST ***";
        String header2 = "*** MACHINE PROGRAM ***";

        builder.append(header1 + "\n");

        for (MachineState state : machineStatesArrayList){
            if(state.getLabel() != null){
                builder.append(state.getLabel() + "\t\t" + state.getAddress() + "\n");
            }
        }

        builder.append("\n"+ header2 + "\n");

        for (MachineState state : machineStatesArrayList){
            String address = "";
            String opcode = "";
            String mnemonic = "";
            String label = "";
            String opperand = "";

            if(state.getAddress() != null){
                address = state.getAddress();
                builder.append(state.getAddress() + ":" );
            }
            if(state.getOpCode() != null){
                opcode = state.getOpCode();
                builder.append(state.getOpCode() + "\t\t" );
            }
            if(state.getMnemonic() != null){
                mnemonic = state.getMnemonic();
                builder.append(state.getMnemonic() + " " );
            }
            if(state.getLabel() != null){
                label = state.getLabel();
                builder.append("0000\t\t" + state.getLabel() + ": ");
            }
            if(state.getOperand() != null){
                opperand = state.getOperand();
                builder.append(state.getOperand() + " ");
            }
            builder.append("\n");
        }
        System.err.print(builder);
    }

    private static void initOpcodeHashMap() {
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
        opcodeHashMap.put("PUSHI", "1");    // + I
        opcodeHashMap.put("PUSH", "2");     // + A
        opcodeHashMap.put("POP", "3");      // + A
        opcodeHashMap.put("JMP", "4");      // + A
        opcodeHashMap.put("JZ", "5");       // + A
        opcodeHashMap.put("JNZ", "6");      // + A
        opcodeHashMap.put("IN", "D");       // + P
        opcodeHashMap.put("OUT", "E");      // + P
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

        private static int HEXTODEC(String data) {
            return Integer.parseInt(data, 16);
        }

        private static String DECTOHEX(int data) {
            return Integer.toHexString(data);
        }

        private static String DECTOHEX(String data) {
            try {
                int num = Integer.parseInt(data);
                String ret = Integer.toHexString(num);
                return ret;
            } catch (NumberFormatException e) {
                //
            } catch (NullPointerException e) {
                //
            }
            return "";
        }

        public static boolean isStringOnlyAlphabet(String str) {

            return ((str != null) && (str.matches("^[a-zA-Z]*$")));
        }
    }

    class MachineState {
        private String address;
        private String opCode;
        private String mnemonic;
        private String label;
        private String operand;
        private String hex;

        public MachineState(String address, String opCode, String mnemonic, String label, String operand, String hex) {
            this.address = address;
            this.opCode = opCode;
            this.mnemonic = mnemonic;
            this.label = label;
            this.operand = operand;
            this.hex = hex;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getOpCode() {
            return opCode;
        }

        public void setOpCode(String opCode) {
            this.opCode = opCode;
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public void setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getOperand() {
            return operand;
        }

        public void setOperand(String operand) {
            this.operand = operand;
        }

        public String getHex() {
            return hex;
        }

        public void setHex(String hex) {
            this.hex = hex;
        }

        @Override
        public String toString() {
            return "MachineState {" +
                    " address:'" + address +  "\t" +
                    " opCode:'" + opCode +  "\t" +
                    " mnemonic:'" + mnemonic + "\t" +
                    " label:'" + label  + "\t" +
                    " operand:'" + operand + "\t" +
                    " hex:'" + hex + "\t" +
                    '}' +"\n";
        }
    }