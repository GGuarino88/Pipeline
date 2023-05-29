package com.packages.pipeline;

import com.packages.disassembler.Disassembler;

public class Pipeline {

    // two versions of pipeline registers (Read and write)
    private IF_ID if_id_Write = new IF_ID();
    private IF_ID if_id_Read = new IF_ID();

    private ID_EX id_ex_Write = new ID_EX();
    private ID_EX id_ex_Read = new ID_EX();

    private EX_Mem ex_mem_Write = new EX_Mem();
    private EX_Mem ex_mem_Read = new EX_Mem();

    private Mem_WB mem_wb_Write = new Mem_WB();
    private Mem_WB mem_wb_Read = new Mem_WB();

    private Disassembler disassembler = new Disassembler();

    private int address;
    private int instruction;

    private int[] Regs;
    private int[] Main_Memory;

    // constructor
    public Pipeline(int[] Regs, int[] Main_Memory) {        // the constructor takes as arguments the Registers and Main memory
        this.Regs = Regs;                                   // to be able to write and read from them
        this.Main_Memory = Main_Memory;
    } // end constructor

    // setters
    public void setAddress(int address) {

        this.address = address;
    }

    public void setInstruction(int instruction) {

        this.instruction = instruction;
    }

    public void IF_stage() {

        // if the instruction is not noOp than the address is incremented by 4 to get our incremented program counter
        if (instruction != 0) {
            address += 4;
            if_id_Write.incrPC = address;
        }
        // if the instruction is a noOp than we reset the program counter to zero for the purpose of the simulation to
        // clear out the variable
        else {
            if_id_Write.incrPC = 0;
        }

        // write the information to the write version of the if_id pipeline register
        if_id_Write.instr = instruction;

    } // end IF_stage

    public void ID_stage() {

        // pass the incremented pc counter downstream for future use
        id_ex_Write.incrPC = if_id_Read.incrPC;

        // instruction decode
        disassembler.decode(if_id_Read.instr);

        // function will set the control bits based on the operation to be performed
        setControls(disassembler.getOpCode(), disassembler.getFunkCode());

        // register fetch

        // reading the values from the registers decoded from the instruction
        id_ex_Write.readData1 = Regs[disassembler.getRsReg()];
        id_ex_Write.readData2 = Regs[disassembler.getRtReg()];

        // setting up the two possible write register numbers
        id_ex_Write.wReg_20_16 = disassembler.getRtReg();
        id_ex_Write.wReg_15_11 = disassembler.getRdReg();

        // the immediate from the disassembler class is a short, id_ex_Write.seOffSet from the
        // pipeline register is an int, once the short is assigned into an int we will become a
        // 32 bit signed extended offset from the previous 16
        id_ex_Write.seOffSet = disassembler.getImmediate();

    } // end ID_stage


    public void EX_stage() {

        // reading control bits from the read version of the id_ex pipeline register
        ex_mem_Write.memRead = id_ex_Read.memRead;
        ex_mem_Write.memWrite = id_ex_Read.memWrite;
        ex_mem_Write.branch = id_ex_Read.branch;

        ex_mem_Write.memToReg = id_ex_Read.memToReg;
        ex_mem_Write.regWrite = id_ex_Read.regWrite;

        // Note: Branch not supported, NO calcBTA

        // ALU OPERATIONS

        // if the ALUop is 10 than we are into an R-Format instruction
        // alu source set to zero the program uses readData2 for the second operand
        if (id_ex_Read.ALUop == 10 && id_ex_Read.ALUsrc == 0) {

            // the operation to be performed will be in the last 6 bits of the sign extended offset
            // HEX 3F = BINARY 111111
            int operation = id_ex_Read.seOffSet & 0x3F;

            // the result of the operation will be stored into ALUresult in the ex_mem Write version
            // of the pipeline register
            if (operation == 0x20) {
                // if the 6 bits are Ox20 than it is performing an add
                ex_mem_Write.ALUresult = id_ex_Read.readData1 + id_ex_Read.readData2;

            } else if (operation == 0x22) {
                // if the 6 bits are 0x22 than it is performing a subtraction
                ex_mem_Write.ALUresult = id_ex_Read.readData1 - id_ex_Read.readData2;

            }

            // if the ALU result is zero the zero flag is set to TRUE (1)
            ex_mem_Write.zero = ex_mem_Write.ALUresult == 0 ? 1 : 0;

        // if the ALUop is a zero than we are performing an I-Format instruction
        // ALUsrc is set to one the program uses the seOffset for the second operand
        } else if (id_ex_Read.ALUop == 0 && id_ex_Read.ALUsrc == 1) {

            ex_mem_Write.ALUresult = id_ex_Read.readData1 + id_ex_Read.seOffSet;

            // if the ALU result is zero the zero flag is set to TRUE (1)
            ex_mem_Write.zero = ex_mem_Write.ALUresult == 0 ? 1 : 0;
        } else {
            // with we pass a result of zero for the purpose of the simulation, so that if there is value stored in the
            // Alu result we will reset the variable to zero
            ex_mem_Write.ALUresult = 0;
        }

        // get a potential store word value from the id_ex pipeline register
        ex_mem_Write.SW_value = id_ex_Read.readData2;

        // multiplexer will decide which register write to
        // if register destination is a zero the register number from the values in bits 20 t0 16 is used
        // if instead register destination is set to 1 the value from bits 15 to 11 is used
        if (id_ex_Read.regDest == 0)
            ex_mem_Write.writeRegNum = id_ex_Read.wReg_20_16;

        else if (id_ex_Read.regDest == 1)
            ex_mem_Write.writeRegNum = id_ex_Read.wReg_15_11;

    } // end EX_stage

    public void MEM_stage() {

        // read control signals from the ex_mem pipeline register
        mem_wb_Write.memToReg = ex_mem_Read.memToReg;
        mem_wb_Write.regWrite = ex_mem_Read.regWrite;

        // pass along information needed in the wb stage
        mem_wb_Write.ALUresult = ex_mem_Read.ALUresult;
        mem_wb_Write.writeRegNum = ex_mem_Read.writeRegNum;

        // if the control bit mem read is set to 1 than the load word value will be found
        // in main memory at the address calculated from the ALU
        if (ex_mem_Read.memRead == 1)
            mem_wb_Write.LWdataValue = Main_Memory[ex_mem_Read.ALUresult];
        else {
            // for the purpose of the simulation if mem read is zero than we reset the LW data value to zero to clear
            // any value stored in previous cycles
            mem_wb_Write.LWdataValue = 0;
        }

        // if the control bit mem write is set to 1 than we write to main memory at the
        // address calculated by the ALU
        if (ex_mem_Read.memWrite == 1)
            Main_Memory[ex_mem_Read.ALUresult] = ex_mem_Read.SW_value;

    } // end MEM_stage

    public void WB_stage() {

        // if regWrite is a 1 than we will be writing to a register, if regWrite is 0 than it doesn't matter
        if (mem_wb_Read.regWrite == 1) {

            // if mem to reg is 1 than we will write the LW data value to the register
            if (mem_wb_Read.memToReg == 1) {

                Regs[mem_wb_Read.writeRegNum] = mem_wb_Read.LWdataValue;

            // if mem to reg is 0 than we will write the ALU result to the register
            } else if (mem_wb_Read.memToReg == 0) {

                Regs[mem_wb_Read.writeRegNum] = mem_wb_Read.ALUresult;
            }
        }
    } // end WB_stage

    // The function will print the pipeline registers in write and the read stage
    public void Print_out_everything() {

        System.out.println("---------------------------------------------------------------------------------------------");

        System.out.println("IF/ID [Write] (Written to by the IF stage)");
        System.out.printf("Inst =  0x%08X\tIncrPC = %X\n\n", if_id_Write.instr, if_id_Write.incrPC);

        System.out.println("IF/ID STAGE() [Read] (Read by the ID stage)");
        System.out.printf("Inst =  0x%08X\tIncrPC = %X\n\n", if_id_Read.instr, if_id_Read.incrPC);

        System.out.println("---------------------------------------------------------------------------------------------");

        System.out.println("ID/EX [Write] (Written to by the ID stage)");
        System.out.printf("Control: RegDst = %d, ALUSrc = %d, ALUOp = %d, MemRead = %d, MemWrite = %d," +
                        "\n Branch = %d, MemToReg = %d, RegWrite = %d\n" +
                        "Incr PC = %X    ReadReg1Value = %X    ReadReg2Value = %X\n" +
                        "SEOffset = %08X   WriteReg_20_16 = %d   WriteReg_15_11 = %d   Function = %X\n\n",
                id_ex_Write.regDest, id_ex_Write.ALUsrc, id_ex_Write.ALUop, id_ex_Write.memRead, id_ex_Write.memWrite,
                id_ex_Write.branch, id_ex_Write.memToReg, id_ex_Write.regWrite,
                id_ex_Write.incrPC, id_ex_Write.readData1, id_ex_Write.readData2, id_ex_Write.seOffSet,
                id_ex_Write.wReg_20_16, id_ex_Write.wReg_15_11, id_ex_Write.seOffSet & 0x3F);

        System.out.println("ID/EX [Read] (Read by the EX stage)");
        System.out.printf("Control: RegDst = %d, ALUSrc = %d, ALUOp = %d, MemRead = %d, MemWrite = %d," +
                        "\n Branch = %d, MemToReg = %d, RegWrite = %d\n" +
                        "Incr PC = %X    ReadReg1Value = %X    ReadReg2Value = %X\n" +
                        "SEOffset = %08X   WriteReg_20_16 = %d   WriteReg_15_11 = %d   Function = %X\n\n",
                id_ex_Read.regDest, id_ex_Read.ALUsrc, id_ex_Read.ALUop, id_ex_Read.memRead, id_ex_Read.memWrite,
                id_ex_Read.branch, id_ex_Read.memToReg, id_ex_Read.regWrite,
                id_ex_Read.incrPC, id_ex_Read.readData1, id_ex_Read.readData2, id_ex_Read.seOffSet,
                id_ex_Read.wReg_20_16, id_ex_Read.wReg_15_11, id_ex_Read.seOffSet & 0x3F);

        System.out.println("---------------------------------------------------------------------------------------------");

        System.out.println("EX/MEM [Write] (Written to by the EX stage)");
        System.out.printf("Control: MemRead = %d, MemWrite = %d, Branch = %d, MemToReg = %d, RegWrite = %d,\n" +
                        "CalcBTA = %X\t\tZero = %s\t\tALUResult = %X\n" +
                        "SWValue = %X\t\tWriteRegNum = %d\n\n",
                ex_mem_Write.memRead, ex_mem_Write.memWrite, ex_mem_Write.branch, ex_mem_Write.memToReg, ex_mem_Write.regWrite,
                ex_mem_Write.calcBTA, ex_mem_Write.zero == 0? "F":"T", ex_mem_Write.ALUresult,
                ex_mem_Write.SW_value, ex_mem_Write.writeRegNum);

        System.out.println("EX/MEM [Read] (Read by the MEM stage)");
        System.out.printf("Control: MemRead = %d, MemWrite = %d, Branch = %d, MemToReg = %d, RegWrite = %d,\n" +
                        "CalcBTA = %X\t\tZero = %s\t\tALUResult = %X\n" +
                        "SWValue = %X\t\tWriteRegNum = %d\n\n",
                ex_mem_Read.memRead, ex_mem_Read.memWrite, ex_mem_Read.branch, ex_mem_Read.memToReg, ex_mem_Read.regWrite,
                ex_mem_Read.calcBTA, ex_mem_Read.zero == 0? "F":"T", ex_mem_Read.ALUresult,
                ex_mem_Read.SW_value, ex_mem_Read.writeRegNum);

        System.out.println("---------------------------------------------------------------------------------------------");

        System.out.println("MEM/WB [Write] (Written to by the MEM stage)");
        System.out.printf("Control: MemToReg = %d, RegWrite = %d,\n" +
                        "LWDataValue = %X \tALUResult = %X\t\tWriteRegNum = %d\n\n",
                mem_wb_Write.memToReg, mem_wb_Write.regWrite,
                mem_wb_Write.LWdataValue,
                mem_wb_Write.ALUresult, mem_wb_Write.writeRegNum);

        System.out.println("MEM/WB [Read] (Read by the WB stage)");
        System.out.printf("Control: MemToReg = %d, RegWrite = %d,\n" +
                        "LWDataValue = %X \tALUResult = %X\t\tWriteRegNum = %d\n\n",
                mem_wb_Read.memToReg, mem_wb_Read.regWrite,
                mem_wb_Read.LWdataValue,
                mem_wb_Read.ALUresult, mem_wb_Read.writeRegNum);

        System.out.println("---------------------------------------------------------------------------------------------");

    } // end Print_out_everything

    public void Copy_write_to_read() {

        if_id_Read.copy(if_id_Write);
        id_ex_Read.copy(id_ex_Write);
        ex_mem_Read.copy(ex_mem_Write);
        mem_wb_Read.copy(mem_wb_Write);

    } // end Copy_write_to_read


    public void setControls(int opCode, int funkCode) {

        // NO OP -> OP CODE AND FUNCTION CODE = 0, all control bits are set to zero
        if (opCode == 0 && funkCode == 0) {

            id_ex_Write.regDest = 0;
            id_ex_Write.ALUsrc = 0;
            id_ex_Write.ALUop = 0;

            id_ex_Write.memRead = 0;
            id_ex_Write.memWrite = 0;
            id_ex_Write.branch = 0;

            id_ex_Write.memToReg = 0;
            id_ex_Write.regWrite = 0;

            // R-TYPE Instruction, op CODE ZERO
        } else if (opCode == 0) {

            // for the operation add (0x20) and subtract (0x22) the control bits are the same
            if (funkCode == 0x20 || funkCode == 0x22) {

                id_ex_Write.regDest = 1;
                id_ex_Write.ALUsrc = 0;
                id_ex_Write.ALUop = 10;

                id_ex_Write.memRead = 0;
                id_ex_Write.memWrite = 0;
                id_ex_Write.branch = 0;

                id_ex_Write.memToReg = 0;
                id_ex_Write.regWrite = 1;

            }

            // I-TYPE INSTRUCTIONS
            // Setting control bits for the instruction load byte (0x20)
        } else if (opCode == 0x20) {

            id_ex_Write.regDest = 0;
            id_ex_Write.ALUsrc = 1;
            id_ex_Write.ALUop = 0;

            id_ex_Write.memRead = 1;
            id_ex_Write.memWrite = 0;
            id_ex_Write.branch = 0;

            id_ex_Write.memToReg = 1;
            id_ex_Write.regWrite = 1;

            // Setting control bits for the instruction save byte (0x28)
        } else if (opCode == 0x28) {

            id_ex_Write.regDest = 0;
            id_ex_Write.ALUsrc = 1;
            id_ex_Write.ALUop = 0;

            id_ex_Write.memRead = 0;
            id_ex_Write.memWrite = 1;
            id_ex_Write.branch = 0;

            id_ex_Write.memToReg = 0;
            id_ex_Write.regWrite = 0;

        }
    } // end setControls
}
