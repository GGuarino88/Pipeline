package com.packages.disassembler;

// The Disassembler class takes the hex instruction and decodes the instruction into assembly language
public class Disassembler {

    private int hexInstruction = 0;
    private int opCode = 0;
    private int rsReg = 0;
    private int rtReg = 0;
    private int rdReg = 0;
    private int funkCode = 0;
    private short immediate = (short)0x0000;

    private int operation = 0;

    private final MIPS_32bit mipsReference = new MIPS_32bit();

    // constructor
    public Disassembler() {};

    // decode method
    public void decode(int hexInstruction) {

        this.hexInstruction = hexInstruction;

        setOpCode(hexInstruction);      // decoding the opcode for the instruction
        setFunkCode(hexInstruction);    // decode the function code
        setRsCode(hexInstruction);      // for both R-Instruction and I-Instruction we decode the
        setRtCode(hexInstruction);      // Source Register and the Target Register
        setRdCode(hexInstruction);      // decode the destination register
        setImmediate(hexInstruction);   // decode the immediate

    } // end decode method

    // sets and gets
    public void setOpCode(int hexInstruction) {

        this.opCode = (hexInstruction & mipsReference.OP_CODE_MASK) >>> mipsReference.OP_CODE_SHIFT;
    }

    public int getOpCode() {

        return opCode;
    }

    public void setRsCode(int hexInstruction) {

        this.rsReg = (hexInstruction & mipsReference.RS_MASK) >>> mipsReference.RS_SHIFT;
    }

    public int getRsReg() {

        return rsReg;
    }

    public void setRtCode(int hexInstruction) {

        this.rtReg = (hexInstruction & mipsReference.RT_MASK) >>> mipsReference.RT_SHIFT;
    }

    public int getRtReg() {

        return rtReg;
    }

    public void setRdCode(int hexInstruction) {

        this.rdReg = (hexInstruction & mipsReference.RD_MASK) >>> mipsReference.RD_SHIFT;
    }

    public int getRdReg() {

        return rdReg;
    }

    public void setFunkCode(int hexInstruction) {

        this.funkCode = hexInstruction & mipsReference.FUNC_MASK;
    }

    public int getFunkCode() {

        return funkCode;
    }

    public void setImmediate(int hexInstruction) {

        this.immediate = (short)(hexInstruction & mipsReference.IMMEDIATE_MASK);
    }

    public short getImmediate() {

        return immediate;
    }

} // end Disassembler class