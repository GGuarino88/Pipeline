package com.packages.pipeline;

// Mem Write Back pipeline register
public class Mem_WB {

    public int memToReg = 0;
    public int regWrite = 0;

    public int LWdataValue = 0;
    public int ALUresult = 0;
    public int writeRegNum = 0;

    // copy method will create a deep copy of the object
    public void copy(Mem_WB mem_wb) {

        this.memToReg = mem_wb.memToReg;
        this.regWrite = mem_wb.regWrite;

        this.LWdataValue = mem_wb.LWdataValue;
        this.ALUresult = mem_wb.ALUresult;
        this.writeRegNum = mem_wb.writeRegNum;
    } // end copy method
}
