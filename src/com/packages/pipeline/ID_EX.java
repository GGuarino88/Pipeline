package com.packages.pipeline;

// instruction decode execute stage pipeline register
public class ID_EX {

    public int incrPC = 0;

    public int regDest = 0;
    public int ALUsrc = 0;
    public int ALUop = 0;

    public int memRead = 0;
    public int memWrite = 0;
    public int branch = 0;

    public int memToReg = 0;
    public int regWrite = 0;

    public int readData1 = 0;
    public int readData2 = 0;
    public int seOffSet = 0;
    public int wReg_20_16 = 0;
    public int wReg_15_11 = 0;

    // copy method will create a deep copy of the object
    public void copy(ID_EX id_ex) {

        this.incrPC = id_ex.incrPC;

        this.regDest = id_ex.regDest;
        this.ALUsrc = id_ex.ALUsrc;
        this.ALUop = id_ex.ALUop;

        this.memRead = id_ex.memRead;
        this.memWrite = id_ex.memWrite;
        this.branch = id_ex.branch;

        this.memToReg = id_ex.memToReg;
        this.regWrite = id_ex.regWrite;

        this.readData1 = id_ex.readData1;
        this.readData2 = id_ex.readData2;
        this.seOffSet = id_ex.seOffSet;
        this.wReg_20_16 = id_ex.wReg_20_16;
        this.wReg_15_11 = id_ex.wReg_15_11;

    } // end copy method
}
