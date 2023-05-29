package com.packages.pipeline;

// Execute Memory Stage pipeline register
public class EX_Mem {
    public int memRead = 0;
    public int memWrite = 0;
    public int branch = 0;

    public int memToReg = 0;
    public int regWrite = 0;

    public int calcBTA = 0;
    public int zero = 0;
    public int ALUresult = 0;
    public int SW_value = 0;
    public int writeRegNum = 0;

    // copy method will create a deep copy of the object
    public void copy(EX_Mem ex_mem) {

        this.memRead = ex_mem.memRead;
        this.memWrite = ex_mem.memWrite;
        this.branch = ex_mem.branch;

        this.memToReg = ex_mem.memToReg;
        this.regWrite = ex_mem.regWrite;

        this.calcBTA = ex_mem.calcBTA;
        this.zero = ex_mem.zero;
        this.ALUresult = ex_mem.ALUresult;
        this.SW_value = ex_mem.SW_value;
        this.writeRegNum = ex_mem.writeRegNum;
    } // end copy method

}
