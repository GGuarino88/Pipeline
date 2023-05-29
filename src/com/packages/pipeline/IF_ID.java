package com.packages.pipeline;
// instruction Fetch Instruction decode pipeline register
public class IF_ID {

    public int incrPC = 0x0;
    public int instr = 0;

    // copy method will create a deep copy of the object
    public void copy(IF_ID if_id) {

        this.incrPC = if_id.incrPC;
        this.instr = if_id.instr;
    } // end copy method
}
