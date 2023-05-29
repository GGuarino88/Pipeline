/**Author: @GiuseppeGuarino
 * CS-472
 * PROJECT 3 - PIPELINE
 * */

import com.packages.pipeline.Pipeline;

public class Main {

    public static void main(String[] args) {

        // constants
        final int MM_SIZE = 1024;
        final int REG_SIZE = 32;

        // main memory and registers array
        int[] Main_Mem = new int[MM_SIZE];
        int[] Regs = new int[REG_SIZE];

        initializeMM(Main_Mem);
        initializeRegs(Regs);

        // instructions
        int[] InstructionCache = {0xa1020000, 0x810AFFFC, 0x00831820, 0x01263820, 0x01224820, 0x81180000,
                0x81510010, 0x00624022, 0x00000000, 0x00000000, 0x00000000, 0x00000000};

        // pipeline initialization
        Pipeline pipeline = new Pipeline(Regs, Main_Mem);

        pipeline.IF_stage();
        pipeline.ID_stage();
        pipeline.EX_stage();
        pipeline.MEM_stage();
        pipeline.WB_stage();

        // printing out clock cycle 0 showing an empty pipeline
        System.out.println("Clock Cycle 0  (Before we start with the specified instructions)");
        pipeline.Print_out_everything();

        // printing out the register values for each clock cycle
        System.out.println("\nRegisters at clock cycle 0\n");
        printRegs(Regs);

        pipeline.Copy_write_to_read();

        // setting up the first address where the instruction resides
        pipeline.setAddress(0x7A000);

        // for each instruction we perform all the pipeline stages and print the result to the console
        for (int i = 0; i < InstructionCache.length; i++ ) {

            pipeline.setInstruction(InstructionCache[i]);

            pipeline.IF_stage();
            pipeline.ID_stage();
            pipeline.EX_stage();
            pipeline.MEM_stage();
            pipeline.WB_stage();

            System.out.println("---------------------------------------------------------------------------------------------");
            System.out.println("Clock Cycle " + (i + 1) + " (Before we copy the write side of pipeline registers to the read side)");
            pipeline.Print_out_everything();

            // printing out the register values for each clock cycle
            System.out.println("\nRegisters after clock cycle " + (i + 1) + "\n");
            printRegs(Regs);

            pipeline.Copy_write_to_read();

        }

    }
    // main memory will be initialized with all values between 0 and FF
    public static void initializeMM(int[] MM) {

        int x = 0x0;
        for (int i = 0; i < MM.length; i++) {

            MM[i] = x;
            x++;

            if (x > 0xFF)
                x = 0x0;
        }
    }
    // initialize registers and skip register zero
    public static void initializeRegs(int[] regs) {

        regs[0] = 0;
        int x = 0x100;

        for (int i = 1; i < regs.length; i++) {

            regs[i] = x + i;

        }
    }

    // printing out registers formatted as an output in 8 in each row
    public static void printRegs(int[] arr) {

        for (int i = 0; i < arr.length; i++) {
            System.out.printf("$%-2d = %-3S\t", i, Integer.toHexString(arr[i]));
            if ((i + 1) % 8 == 0)
                System.out.println();
        }
        System.out.println();
    }

}
